package com.example.pix.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pix.data.room.entity.PictureEntity

@Dao
interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<PictureEntity>)

    @Query("delete from pictures")
    suspend fun clearAll()

    @Query("select * from pictures")
    suspend fun getAll(): List<PictureEntity>
}