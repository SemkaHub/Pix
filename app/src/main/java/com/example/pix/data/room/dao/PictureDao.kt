package com.example.pix.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.pix.data.room.entity.PictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    @Transaction
    suspend fun clearAndInsertAll(pictures: List<PictureEntity>) {
        clearAll()
        insertAll(pictures)
    }

    @Query("select * from pictures")
    fun getPicturesFlow(): Flow<List<PictureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<PictureEntity>)

    @Query("delete from pictures")
    suspend fun clearAll()

    @Query("select * from pictures")
    suspend fun getAll(): List<PictureEntity>

    @Query("select * from pictures where id = :id")
    suspend fun getById(id: String): PictureEntity
}