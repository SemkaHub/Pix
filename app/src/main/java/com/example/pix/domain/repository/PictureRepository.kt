package com.example.pix.domain.repository

import com.example.pix.domain.model.Picture
import kotlinx.coroutines.flow.Flow

interface PictureRepository {
    suspend fun getPictures(): Result<Unit>
    suspend fun getPictureById(id: String): Result<Picture>
    fun observePictures(): Flow<List<Picture>>
}