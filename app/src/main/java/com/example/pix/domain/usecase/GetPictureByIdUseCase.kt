package com.example.pix.domain.usecase

import com.example.pix.domain.error.ErrorHandler
import com.example.pix.domain.model.Picture
import com.example.pix.domain.repository.PictureRepository
import javax.inject.Inject

private const val UNKNOWN_ERROR = "Unknown error"

class GetPictureByIdUseCase @Inject constructor(
    private val pictureRepository: PictureRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(id: String): Result<Picture> {
        return runCatching {
            val picture = pictureRepository.getPictureById(id)
            if (picture.isFailure) {
                val exp = picture.exceptionOrNull()
                throw exp ?: Throwable(UNKNOWN_ERROR)
            }
            picture
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(errorHandler.handleError(it)) }
        )
    }
}