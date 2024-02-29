package com.muhfikrih.moviequ.api

import com.muhfikrih.moviequ.models.MovieResponse
import com.muhfikrih.moviequ.models.responses.ResponseGenres
import com.muhfikrih.moviequ.models.responses.ResponseVideos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/now_playing")
    suspend fun getPopularMovie(
        @Query("api_key") key: String?,
        @Query("page") page: Int?
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") key: String?,
        @Query("query") query: String?,
        @Query("page") page: Int?
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") key: String?
    ): ResponseGenres

    @GET("movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("api_key") key: String?,
        @Query("page") page: Int?,
        @Query("region") region: String?
    ): Response<MovieResponse>

    @GET("movie/{movieId}/videos")
    suspend fun getVideos(
        @Path("movieId") id: Int?,
        @Query("api_key") key: String?
    ): Response<ResponseVideos>
}