package com.example.pix.data.flickr

import com.example.pix.domain.model.Picture

class FlickrRepository(
    private val flickrApi: FlickrApi,
    // TODO
) {
    suspend fun search(
        text: String = "cats",
        page: Int = 1,
        count: Int = 100
    ): Result<List<Picture>> = runCatching {
        val result = flickrApi.search(text, page, count)
        TODO()
    }
}