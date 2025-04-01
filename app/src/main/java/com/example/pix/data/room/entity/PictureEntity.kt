package com.example.pix.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures")
data class PictureEntity (
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String,
    val label: String,
)