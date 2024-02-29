package com.ansorisan.movieku_kt.repositories

import com.muhfikrih.moviequ.api.ApiConfig
import com.muhfikrih.moviequ.helpers.DataHelper
import com.muhfikrih.moviequ.BuildConfig

class MovieRepository {
    private val client = ApiConfig.getApiService()
    suspend fun getPlayingMovie(page: Int) = client.getPlayingMovie(BuildConfig.API_KEY, DataHelper.Default_Region, page)
    suspend fun searchMovie(query: String, page: Int) = client.searchMovie(BuildConfig.API_KEY, query, page)
    suspend fun getGenres() = client.getGenres(BuildConfig.API_KEY)
    suspend fun getUpcomingMovie(page: Int) = client.getUpcomingMovie(BuildConfig.API_KEY, page, DataHelper.Default_Region)
    suspend fun getVideos(id: Int) = client.getVideos(id, BuildConfig.API_KEY)
    suspend fun getReview(id: Int, page: Int) = client.getReview(id, BuildConfig.API_KEY, page)
}