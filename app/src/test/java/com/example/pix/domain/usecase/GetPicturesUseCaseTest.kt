package com.example.pix.domain.usecase

import com.example.pix.domain.error.DomainError
import com.example.pix.domain.error.ErrorHandler
import com.example.pix.domain.model.Picture
import com.example.pix.domain.repository.PictureRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class GetPicturesUseCaseTest {

    private val pictureRepository = mockk<PictureRepository>()
    private val errorHandler = mockk<ErrorHandler>()
    private val getPicturesUseCase = GetPicturesUseCase(pictureRepository, errorHandler)

    @Test
    fun `invoke should return result from repository if successful`() = runTest {

        // Arrange
        val expectedResult = Result.success(emptyList<Picture>())
        coEvery { pictureRepository.getPictures() } returns expectedResult

        // Act
        val result = getPicturesUseCase()

        // Assert
        assert(result.isSuccess)
        assert(result == expectedResult)
    }

    @Test
    fun `invoke should return result from DomainError if repository fails`() = runTest {

        // Arrange
        val expectedError = Result.failure<List<Picture>>(DomainError.Network)
        val error = IOException()
        every { errorHandler.handleError(error) } returns DomainError.Network
        coEvery { pictureRepository.getPictures() } throws error

        // Act
        val result = getPicturesUseCase()

        // Assert
        assert(result.isFailure)
        assert(result == expectedError)
    }
}