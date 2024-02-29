package com.muhfikrih.moviequ.api

import com.muhfikrih.moviequ.models.movie.MovieResponse
import com.muhfikrih.moviequ.models.genre.ResponseGenres
import com.muhfikrih.moviequ.models.review.ResponseReview
import com.muhfikrih.moviequ.models.video.ResponseVideos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/now_playing")
    suspend fun getPlayingMovie(
        @Query("api_key") key: String?,
        @Query("region") region: String?,
        @Query("page") page: Int?
    ): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovie(
        @Query("api_key") key: String?,
        @Query("page") page: Int?,
        @Query("region") region: String?
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

    @GET("movie/{movieId}/videos")
    suspend fun getVideos(
        @Path("movieId") id: Int?,
        @Query("api_key") key: String?
    ): Response<ResponseVideos>

    @GET("movie/{movieId}/reviews")
    suspend fun getReview(
        @Path("movieId") id: Int?,
        @Query("api_key") key: String?,
        @Query("page") page: Int?
    ): Response<ResponseReview>
}