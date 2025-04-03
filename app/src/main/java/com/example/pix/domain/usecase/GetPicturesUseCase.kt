package com.example.pix.domain.usecase

import com.example.pix.domain.error.ErrorHandler
import com.example.pix.domain.repository.PictureRepository
import javax.inject.Inject

private const val UNKNOWN_ERROR = "Unknown error"

class GetPicturesUseCase @Inject constructor(
    private val pictureRepository: PictureRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            val result = pictureRepository.getPictures()
            if (result.isFailure) {
                val exp = result.exceptionOrNull()
                throw exp ?: Throwable(UNKNOWN_ERROR)
            }
            result
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(errorHandler.handleError(it)) }
        )
    }
}