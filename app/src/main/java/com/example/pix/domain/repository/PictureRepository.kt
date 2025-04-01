package com.example.pix.domain.repository

import com.example.pix.domain.model.Picture

interface PictureRepository {
    suspend fun getPictures(): Result<List<Picture>>
    suspend fun getPictureById(id: String): Result<Picture>
}