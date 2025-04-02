package com.example.pix.data.flickr.mapper

import com.example.pix.data.flickr.dto.PhotoDto
import com.example.pix.domain.model.Picture

// https://www.flickr.com/services/api/misc.urls.html
fun PhotoDto.toDomain(quality: String): Picture = Picture(
    id = id,
    title = title,
    url = "https://live.staticflickr.com/${server}/${id}_${secret}_${quality}.jpg",
)

