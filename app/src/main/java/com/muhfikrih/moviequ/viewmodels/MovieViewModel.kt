package com.muhfikrih.moviequ.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.models.movie.MovieResponse
import com.muhfikrih.moviequ.models.genre.ResponseGenres
import com.ansorisan.movieku_kt.repositories.MovieRepository
import com.muhfikrih.moviequ.models.review.ResponseReview
import com.muhfikrih.moviequ.models.video.ResponseVideos
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

class MovieViewModel : ViewModel() {
    private val repo: MovieRepository = MovieRepository()
    private var upcomingPage = 1
    private var popularPage = 1
    private var searchPage = 1
    private var reviewPage = 1

    private var modelMoviePlaying: MovieResponse? = null
    private var mutableMoviePlaying = MutableLiveData<RequestState<MovieResponse?>>()
    var dataPlayingMovie: LiveData<RequestState<MovieResponse?>> = mutableMoviePlaying

    private var modelMovieUpcoming: MovieResponse? = null
    private var mutableMovieUpcoming = MutableLiveData<RequestState<MovieResponse?>>()
    var dataMovieUpcoming: LiveData<RequestState<MovieResponse?>> = mutableMovieUpcoming

    private var modelMovieSearch: MovieResponse? = null
    private var mutableMovieSearch = MutableLiveData<RequestState<MovieResponse?>>()
    var dataMovieSearch: LiveData<RequestState<MovieResponse?>> = mutableMovieSearch

    private var modelMovieVideo: ResponseVideos? = null
    private var mutableMovieVideo = MutableLiveData<RequestState<ResponseVideos?>>()
    var dataMovieVideo: LiveData<RequestState<ResponseVideos?>> = mutableMovieVideo

    private var modelMovieReview: ResponseReview? = null
    private var mutableMovieReview = MutableLiveData<RequestState<ResponseReview?>>()
    var dataMovieReview: LiveData<RequestState<ResponseReview?>> = mutableMovieReview

    fun getPlayingMovie() {
        viewModelScope.launch {
            mutableMoviePlaying.postValue(RequestState.Loading)
            val response = repo.getPlayingMovie(popularPage)
            mutableMoviePlaying.postValue(handlePlayingMovie(response))
        }
    }

    private fun handlePlayingMovie(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                popularPage++
                if (modelMoviePlaying == null) modelMoviePlaying = it else {
                    val oldMovies = modelMoviePlaying?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(modelMoviePlaying ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            }catch (e:JSONException){
                e.localizedMessage
            } as String
        )
    }

    fun getUpcomingMovie() {
        viewModelScope.launch {
            mutableMovieUpcoming.postValue(RequestState.Loading)
            val response = repo.getUpcomingMovie(upcomingPage)
            mutableMovieUpcoming.postValue(handleUpcomingMovieResponse(response))
        }
    }

    private fun handleUpcomingMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                upcomingPage++
                if (modelMovieUpcoming == null) modelMovieUpcoming = it else {
                    val oldMovies = modelMovieUpcoming?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(modelMovieUpcoming ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            }catch (e:JSONException){
                e.localizedMessage
            } as String
        )
    }

    fun searchMovie(query: String){
        viewModelScope.launch {
            mutableMovieSearch.postValue(RequestState.Loading)
            val response = repo.searchMovie(query, searchPage)
            mutableMovieSearch.postValue(handleSearchMovieResponse(response))
        }
    }

    private fun handleSearchMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                searchPage++
                if (modelMovieSearch == null) modelMovieSearch = it else {
                    val oldMovies = modelMovieSearch?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(modelMovieSearch ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            }catch (e:JSONException){
                e.localizedMessage
            } as String
        )
    }

    fun getGenres(): LiveData<RequestState<ResponseGenres>> = liveData {
        try {
            val response = repo.getGenres()
            emit(RequestState.Success(response))
        } catch (e: HttpException) {
            e.response()?.errorBody()?.string()?.let {
                RequestState.Error(it)
            }?.let {
                emit(it)
            }
        }
    }

    fun getVideos(id: Int) {
        viewModelScope.launch {
            mutableMovieVideo.postValue(RequestState.Loading)
            val response = repo.getVideos(id)
            mutableMovieVideo.postValue(handleResponseVideos(response))
        }
    }

    private fun handleResponseVideos(response: Response<ResponseVideos>): RequestState<ResponseVideos?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                if (modelMovieVideo == null) modelMovieVideo = it else {
                    val oldVideos = modelMovieVideo?.results
                    val newVideos = it.results
                    oldVideos?.addAll(newVideos)
                }
            }
            RequestState.Success(modelMovieVideo ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            }catch (e:JSONException){
                e.localizedMessage
            } as String
        )
    }

    fun getReview(id: Int) {
        viewModelScope.launch {
            mutableMovieReview.postValue(RequestState.Loading)
            val response = repo.getReview(id, reviewPage)
            mutableMovieReview.postValue(handleResponseReviews(response))
        }
    }

    private fun handleResponseReviews(response: Response<ResponseReview>): RequestState<ResponseReview?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                reviewPage++
                if (modelMovieReview == null) modelMovieReview = it else {
                    val oldReview = modelMovieReview?.results
                    val newReview = it.results
                    oldReview?.addAll(newReview)
                }
            }
            RequestState.Success(modelMovieReview ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            }catch (e:JSONException){
                e.localizedMessage
            } as String
        )
    }
}