package com.example.pix.data.flickr

import com.example.pix.data.flickr.mapper.toDomain
import com.example.pix.domain.model.Picture
import com.example.pix.domain.model.PictureSize
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

const val DEFAULT_SEARCH_TEXT = "cats"
val DEFAULT_QUALITY = PictureSize.Q.prefix


const val RESPONSE_OK = "ok"
const val UNEXPECTED_ERROR_CODE = 999
const val UNEXPECTED_ERROR_MESSAGE = "Unexpected error"
const val ERROR_MEDIA_TYPE = "text/plain"


class FlickrRepository @Inject constructor(
    private val flickrApi: FlickrApi
) {

    suspend fun search(
        text: String = DEFAULT_SEARCH_TEXT,
        page: Int = 1,
        count: Int = 100,
        quality: String = DEFAULT_QUALITY
    ): Result<List<Picture>> = runCatching {
        val result = flickrApi.search(text = text, page = page, count = count)
        if (result.stat != RESPONSE_OK) {
            throw HttpException(
                Response.error<Unit>(
                    result.code ?: UNEXPECTED_ERROR_CODE,
                    (result.message ?: UNEXPECTED_ERROR_MESSAGE)
                        .toResponseBody(ERROR_MEDIA_TYPE.toMediaTypeOrNull())
                )
            )
        }
        result.photos?.photo?.map { it.toDomain(quality) } ?: emptyList()
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )
}