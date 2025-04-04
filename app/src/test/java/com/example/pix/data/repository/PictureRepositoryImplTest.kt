package com.example.pix.data.repository

import com.example.pix.data.flickr.FlickrRepository
import com.example.pix.data.room.RoomRepository
import com.example.pix.domain.model.Picture
import com.example.pix.domain.model.PictureSize
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PictureRepositoryImplTest {

    @MockK
    private lateinit var flickrRepository: FlickrRepository

    @MockK
    private lateinit var roomRepository: RoomRepository

    private lateinit var pictureRepository: PictureRepositoryImpl

    private val testPicture1 = Picture(
        id = "1",
        url = "https://live.staticflickr.com/server/1_id1_s.jpg",
        title = "Title 1"
    )
    private val testPicture2 = Picture(
        id = "2",
        url = "https://live.staticflickr.com/server/2_id2_s.jpg",
        title = "Title 2"
    )
    private val remotePictures = listOf(testPicture1, testPicture2)

    @Before
    fun setUp() {
        // Инициализируем моки перед каждым тестом
        MockKAnnotations.init(this)
        pictureRepository = PictureRepositoryImpl(flickrRepository, roomRepository)
    }

    // --- Тесты для getPictures ---

    @Test
    fun `getPictures() success - fetches remote, clears, inserts`() = runTest {

        // Arrange
        coEvery { flickrRepository.search() } returns Result.success(remotePictures)

        coEvery { roomRepository.replaceAll(remotePictures) } just Runs

        // Act
        val result = pictureRepository.getPictures()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())

        coVerifyOrder {
            flickrRepository.search()
            roomRepository.replaceAll(remotePictures)
        }
    }

    @Test
    fun `getPictures() failure - flickrRepository fails`() = runTest {

        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { flickrRepository.search() } returns Result.failure(exception)

        // Act
        val result = pictureRepository.getPictures()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify(exactly = 0) { roomRepository.clearAll() }
        coVerify(exactly = 0) { roomRepository.insertAll(any()) }
        coVerify(exactly = 0) { roomRepository.getPictures() }
    }

    @Test
    fun `getPictures() failure - roomRepository replaceAll fails`() = runTest {

        // Arrange
        val exception = RuntimeException("DB clear error")
        coEvery { flickrRepository.search() } returns Result.success(remotePictures)
        coEvery { roomRepository.replaceAll(remotePictures) } throws exception

        // Act
        val result = pictureRepository.getPictures()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        // Проверяем вызовы
        coVerify { flickrRepository.search() }
        coVerify { roomRepository.replaceAll(remotePictures) }
        // Последующие шаги не должны были выполниться
        coVerify(exactly = 0) { roomRepository.getPictures() }
    }


    // --- Тесты для getPictureById ---

    @Test
    fun `getPictureById() success - finds picture, transforms url`() = runTest {

        // Arrange
        val testId = "12502775644"
        val originalUrl = "https://live.staticflickr.com/7372/${testId}_acfd415fa7_w.jpg"
        val originalPicture = Picture(id = testId, url = originalUrl, title = "Original Title")
        val expectedQualityPrefix = PictureSize.B.prefix // "b"
        val expectedNewUrl =
            "https://live.staticflickr.com/7372/${testId}_acfd415fa7_${expectedQualityPrefix}.jpg"
        val expectedPicture = originalPicture.copy(url = expectedNewUrl)

        coEvery { roomRepository.getById(testId) } returns originalPicture

        // Act
        val result = pictureRepository.getPictureById(testId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedPicture, result.getOrNull())

        coVerify { roomRepository.getById(testId) }
    }

    @Test
    fun `getPictureById() success - handles url with different initial quality`() = runTest {

        // Arrange
        val testId = "testId"
        val originalUrl = "https://example.com/somepath/${testId}_anotherid_s.png"
        val originalPicture = Picture(id = testId, url = originalUrl, title = "Original Title")
        val expectedQualityPrefix = PictureSize.B.prefix // "b"
        val expectedNewUrl =
            "https://example.com/somepath/${testId}_anotherid_${expectedQualityPrefix}.png"
        val expectedPicture = originalPicture.copy(url = expectedNewUrl)

        coEvery { roomRepository.getById(testId) } returns originalPicture

        // Act
        val result = pictureRepository.getPictureById(testId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedPicture, result.getOrNull())
        coVerify { roomRepository.getById(testId) }
    }

    @Test
    fun `getPictureById() failure - roomRepository getById fails`() = runTest {

        // Arrange
        val testId = "nonexistent"
        val exception = NoSuchElementException("Picture not found")
        coEvery { roomRepository.getById(testId) } throws exception

        // Act
        val result = pictureRepository.getPictureById(testId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { roomRepository.getById(testId) }
    }

    @Test
    fun `getPictureById() failure - url format incorrect (missing underscore)`() = runTest {

        // Arrange
        val testId = "badurlid"
        val badUrl = "https://example.com/nodatahere.jpg" // URL без '_' перед расширением
        val pictureWithBadUrl = Picture(id = testId, url = badUrl, title = "Bad URL")

        coEvery { roomRepository.getById(testId) } returns pictureWithBadUrl

        // Act
        val result = pictureRepository.getPictureById(testId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IndexOutOfBoundsException || result.exceptionOrNull() is IllegalArgumentException)
        coVerify { roomRepository.getById(testId) }
    }

    @Test
    fun `getPictureById() failure - url format incorrect (missing dot)`() = runTest {

        // Arrange
        val testId = "badurlid2"
        val badUrl = "https://example.com/some_path_nodot" // URL без '.'
        val pictureWithBadUrl = Picture(id = testId, url = badUrl, title = "Bad URL 2")

        coEvery { roomRepository.getById(testId) } returns pictureWithBadUrl

        // Act
        val result = pictureRepository.getPictureById(testId)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IndexOutOfBoundsException || result.exceptionOrNull() is IllegalArgumentException)
        coVerify { roomRepository.getById(testId) }
    }
}