package com.example.pix.domain.usecase

import com.example.pix.domain.error.ErrorHandler
import com.example.pix.domain.model.Picture
import com.example.pix.domain.repository.PictureRepository
import javax.inject.Inject

class GetPictureByIdUseCase @Inject constructor(
    private val pictureRepository: PictureRepository,
    private val errorHandler: ErrorHandler
) {
    suspend operator fun invoke(id: String): Result<Picture> {
        return runCatching {
            pictureRepository.getPictureById(id)
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(errorHandler.handleError(it)) }
        )
    }
}