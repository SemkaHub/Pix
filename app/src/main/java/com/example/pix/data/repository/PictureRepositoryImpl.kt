package com.example.pix.data.repository

import com.example.pix.data.flickr.FlickrRepository
import com.example.pix.data.room.RoomRepository
import com.example.pix.domain.model.Picture
import com.example.pix.domain.model.PictureSize
import com.example.pix.domain.repository.PictureRepository
import javax.inject.Inject

private const val INVALID_URL_FORMAT_EXCEPTION =
    "Invalid URL format: Cannot find '_quality.ext' structure in "

class PictureRepositoryImpl @Inject constructor(
    private val flickrRepository: FlickrRepository,
    private val roomRepository: RoomRepository
) : PictureRepository {

    override fun observePictures() = roomRepository.getPicturesFlow()

    override suspend fun getPictures(): Result<Unit> = runCatching {
        val remotePictures = flickrRepository.search().getOrThrow()
        roomRepository.clearAll()
        roomRepository.insertAll(remotePictures)
        //roomRepository.getPictures()
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )

    // https://live.staticflickr.com/7372/12502775644_acfd415fa7_w.jpg
    override suspend fun getPictureById(id: String): Result<Picture> = runCatching {
        val picture = roomRepository.getById(id)
        val betterQualityPicture = PictureSize.B.prefix
        val oldUrl = picture.url
        val newUrl = getBestQualityPictureUrl(oldUrl, betterQualityPicture)
        picture.copy(url = newUrl)
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(it) }
    )

    private fun getBestQualityPictureUrl(oldUrl: String, quality: String): String {

        // Находим индекс последнего символа '_'
        val lastUnderscoreIndex = oldUrl.lastIndexOf('_')
        // Находим индекс последнего символа '.'
        val lastDotIndex = oldUrl.lastIndexOf('.')

        if (lastUnderscoreIndex == -1 || lastDotIndex == -1 || lastUnderscoreIndex > lastDotIndex) {
            throw IllegalArgumentException(INVALID_URL_FORMAT_EXCEPTION + oldUrl)
        }

        // Часть строки до качества фото
        val prefix = oldUrl.substring(0, lastUnderscoreIndex + 1)

        // Часть строки после качества фото
        val extension = oldUrl.substring(lastDotIndex)

        return "$prefix$quality$extension"
    }
}