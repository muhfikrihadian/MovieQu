package com.muhfikrih.moviequ.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.models.MovieResponse
import com.muhfikrih.moviequ.models.responses.ResponseGenres
import com.ansorisan.movieku_kt.repositories.MovieRepository
import com.muhfikrih.moviequ.models.responses.ResponseVideos
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
    private var upcomingMovieResponse: MovieResponse? = null
    private var popularMovieResponse: MovieResponse? = null
    private var searchMovieResponse: MovieResponse? = null
    private var videosMovieResponse: ResponseVideos? = null
    private var _upcomingResponse = MutableLiveData<RequestState<MovieResponse?>>()
    private var _popularResponse = MutableLiveData<RequestState<MovieResponse?>>()
    private var _searchResponse = MutableLiveData<RequestState<MovieResponse?>>()
    private var _videosResponse = MutableLiveData<RequestState<ResponseVideos?>>()
    var upcomingResponse: LiveData<RequestState<MovieResponse?>> = _upcomingResponse
    var popularResponse: LiveData<RequestState<MovieResponse?>> = _popularResponse
    var searchResponse: LiveData<RequestState<MovieResponse?>> = _searchResponse
    var videosResponse: LiveData<RequestState<ResponseVideos?>> = _videosResponse

    fun getPopularMovie() {
        viewModelScope.launch {
            _popularResponse.postValue(RequestState.Loading)
            val response = repo.getPopularMovie(popularPage)
            _popularResponse.postValue(handlePopularMovieResponse(response))
        }
    }

    private fun handlePopularMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                popularPage++
                if (popularMovieResponse == null) popularMovieResponse = it else {
                    val oldMovies = popularMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(popularMovieResponse ?: response.body())
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
            _upcomingResponse.postValue(RequestState.Loading)
            val response = repo.getUpcomingMovie(popularPage)
            _upcomingResponse.postValue(handleUpcomingMovieResponse(response))
        }
    }

    private fun handleUpcomingMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                upcomingPage++
                if (upcomingMovieResponse == null) upcomingMovieResponse = it else {
                    val oldMovies = upcomingMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(upcomingMovieResponse ?: response.body())
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
            _searchResponse.postValue(RequestState.Loading)
            val response = repo.searchMovie(query, searchPage)
            _searchResponse.postValue(handleSearchMovieResponse(response))
        }
    }

    private fun handleSearchMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                searchPage++
                if (searchMovieResponse == null) searchMovieResponse = it else {
                    val oldMovies = searchMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }
            RequestState.Success(searchMovieResponse ?: response.body())
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
            _videosResponse.postValue(RequestState.Loading)
            val response = repo.getVideos(id)
            _videosResponse.postValue(handleResponseVideos(response))
        }
    }

    private fun handleResponseVideos(response: Response<ResponseVideos>): RequestState<ResponseVideos?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                if (videosMovieResponse == null) videosMovieResponse = it else {
                    val oldVideos = videosMovieResponse?.results
                    val newVideos = it.results
                    oldVideos?.addAll(newVideos)
                }
            }
            RequestState.Success(videosMovieResponse ?: response.body())
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