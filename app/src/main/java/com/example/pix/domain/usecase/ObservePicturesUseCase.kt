package com.example.pix.domain.usecase

import com.example.pix.domain.repository.PictureRepository
import javax.inject.Inject

class ObservePicturesUseCase @Inject constructor(
    private val pictureRepository: PictureRepository
) {
    operator fun invoke() = pictureRepository.observePictures()
}