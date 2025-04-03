package com.example.pix.data.flickr

import com.example.pix.data.flickr.dto.FlickrResult
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.pix.BuildConfig

// https://www.flickr.com/services/api/flickr.photos.search.html
interface FlickrApi {
    @GET(SEARCH_URL)
    suspend fun search(
        @Query("method") method: String = "flickr.photos.getRecent",
        @Query("api_key") apiKey: String = BuildConfig.FLICKR_API_KEY,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("text") text: String = "cats",
        @Query("page") page: Int = 1,
        @Query("per_page") count: Int = 100,
    ): FlickrResult

    companion object {
        private const val SEARCH_URL = "/services/rest/"
    }
}