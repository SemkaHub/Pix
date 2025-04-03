package com.example.pix.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pix.data.room.dao.PictureDao
import com.example.pix.data.room.entity.PictureEntity

@Database(entities = [PictureEntity::class], version = 1)
abstract class PictureDatabase: RoomDatabase() {
    abstract fun getPictureDao(): PictureDao
}