package com.example.pix.data.room

import com.example.pix.data.room.dao.PictureDao
import com.example.pix.data.room.mapper.toDomain
import com.example.pix.data.room.mapper.toEntity
import com.example.pix.domain.model.Picture
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val pictureDao: PictureDao
){
    suspend fun getPictures(): List<Picture> {
        val entities = pictureDao.getAll()
        return entities.map { it.toDomain() }
    }

    suspend fun clearAll() {
        pictureDao.clearAll()
    }

    suspend fun insertAll(pictures: List<Picture>) {
        val entities = pictures.map { it.toEntity() }
        pictureDao.insertAll(entities)
    }

    suspend fun getById(id: String): Picture {
        val entity = pictureDao.getById(id)
        return entity.toDomain()
    }
}