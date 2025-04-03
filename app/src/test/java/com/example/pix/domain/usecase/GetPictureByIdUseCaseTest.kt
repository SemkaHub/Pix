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

class GetPictureByIdUseCaseTest {

    private val pictureRepository = mockk<PictureRepository>()
    private val errorHandler = mockk<ErrorHandler>()
    private val getPictureByIdUseCase = GetPictureByIdUseCase(pictureRepository, errorHandler)

    private val picture = Picture(
        id = "1",
        url = "https://example.com/image.jpg",
        title = "Example Image"
    )

    @Test
    fun `invoke should return result from repository if successful`() = runTest {

        // Arrange
        val expectedResult = Result.success(picture)
        coEvery { pictureRepository.getPictureById("1") } returns expectedResult

        // Act
        val result = getPictureByIdUseCase("1")

        // Assert
        assert(result.isSuccess)
        assert(result == expectedResult)
    }

    @Test
    fun `invoke should return result from DomainError if repository fails`() = runTest {

        // Arrange
        val expectedError = Result.failure<Picture>(DomainError.Network)
        val error = IOException()
        every { errorHandler.handleError(error) } returns DomainError.Network
        coEvery { pictureRepository.getPictureById("1") } throws error

        // Act
        val result = getPictureByIdUseCase("1")

        // Assert
        assert(result.isFailure)
        assert(result == expectedError)
    }
}