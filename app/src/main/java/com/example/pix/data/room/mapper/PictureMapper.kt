package com.example.pix.data.room.mapper

import com.example.pix.data.room.entity.PictureEntity
import com.example.pix.domain.model.Picture

const val DEFAULT_LABEL = "default"

fun PictureEntity.toDomain(): Picture = Picture(
    id = id,
    url = url,
    title = title
)

fun Picture.toEntity(label: String = DEFAULT_LABEL): PictureEntity = PictureEntity(
    id = id,
    url = url,
    title = title,
    label = label
)