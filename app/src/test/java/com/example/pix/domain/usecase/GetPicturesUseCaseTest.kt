package com.example.pix.domain.usecase

import com.example.pix.domain.error.DomainError
import com.example.pix.domain.error.ErrorHandler
import com.example.pix.domain.repository.PictureRepository
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class GetPicturesUseCaseTest {

    @RelaxedMockK
    private lateinit var pictureRepository: PictureRepository

    @RelaxedMockK
    private lateinit var errorHandler: ErrorHandler

    private lateinit var getPicturesUseCase: GetPicturesUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getPicturesUseCase = GetPicturesUseCase(pictureRepository, errorHandler)
    }

    @Test
    fun `invoke should return result from repository if successful`() = runTest {

        // Arrange
        val expectedResult = Result.success(Unit)
        coEvery { pictureRepository.getPictures() } returns expectedResult

        // Act
        val result = getPicturesUseCase()

        // Assert
        assert(result.isSuccess)
        verify { errorHandler wasNot Called }
        assert(result == expectedResult)
        coVerify(exactly = 1) { pictureRepository.getPictures() }
    }

    @Test
    fun `invoke() failure - repository returns failure with specific exception`() = runTest {

        // Arrange
        val repositoryException = IOException("Network failed")
        val repositoryFailureResult = Result.failure<Unit>(repositoryException)
        val expectedDomainError = DomainError.Network

        coEvery { pictureRepository.getPictures() } returns repositoryFailureResult
        every { errorHandler.handleError(repositoryException) } returns expectedDomainError

        // Act
        val result = getPicturesUseCase()

        // Assert
        assertTrue("UseCase should return failure", result.isFailure)
        assertEquals(
            "UseCase failure should contain DomainError from ErrorHandler",
            expectedDomainError,
            result.exceptionOrNull()
        )
        coVerify(exactly = 1) { pictureRepository.getPictures() }
        verify(exactly = 1) { errorHandler.handleError(repositoryException) }
    }
}