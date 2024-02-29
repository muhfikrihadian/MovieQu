package com.muhfikrih.moviequ.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.R
import com.muhfikrih.moviequ.adapters.MovieListAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivityMainBinding
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.movie.DataMovie
import com.muhfikrih.moviequ.viewmodels.MovieViewModel
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var nowplayingAdapter: MovieListAdapter? = null
    private var upcomingAdapter: MovieListAdapter? = null
    private var layoutManagerNowplaying: RecyclerView.LayoutManager? = null
    private var layoutManagerUpcoming: RecyclerView.LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    var bannerImages = intArrayOf(
        R.drawable.img_slider_0,
        R.drawable.img_slide_1,
        R.drawable.img_slider_2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setup() {
        getBanners()
        getGenres()
        viewModel.getPlayingMovie()
        viewModel.getUpcomingMovie()
        getPlayingMovie()
        getUpcomingMovies()
        nowplayingAdapter = MovieListAdapter()
        upcomingAdapter = MovieListAdapter()
        layoutManagerUpcoming = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerNowplaying = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.apply {
            movieList.adapter = nowplayingAdapter
            movieList.layoutManager = layoutManagerNowplaying
            movieList.addOnScrollListener(scrollListener)
        }
        binding.apply {
            rcyUpcoming.adapter = upcomingAdapter
            rcyUpcoming.layoutManager = layoutManagerUpcoming
            rcyUpcoming.addOnScrollListener(upcomingScrollListener)
        }
        nowplayingAdapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movie: DataMovie, genres: String) {
                val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movie)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })
        upcomingAdapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movie: DataMovie, genres: String) {
                val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movie)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })
        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword"
                else -> {
                    val intent = Intent(this, SearchMovieActivity::class.java)
                    intent.putExtra(SearchMovieActivity.query, query)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getBanners() {
        var imageListener = ImageListener { position, imageView ->
            imageView.setImageResource(
                bannerImages.get(position)
            )
        }
        val carouselView = findViewById<CarouselView>(R.id.carouselView)
        carouselView.setPageCount(bannerImages.size);
        carouselView.setImageListener(imageListener);
    }

    private fun getGenres() {
        viewModel.getGenres().observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {}
                    is RequestState.Success -> {
                        it.data.genres?.let { data ->
                            nowplayingAdapter?.setGenres(data)
                            upcomingAdapter?.setGenres(data)
                        }
                    }

                    is RequestState.Error -> {}
                }
            }
        }
    }

    private fun getPlayingMovie() {
        viewModel.dataPlayingMovie.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {
                        binding.loadingNowPlaying.show()
                    }

                    is RequestState.Success -> {
                        binding.loadingNowPlaying.hide()
                        it.data?.results?.let { data -> nowplayingAdapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        binding.loadingNowPlaying.hide()
                    }
                }
            }
        }
    }

    private fun getUpcomingMovies() {
        viewModel.dataMovieUpcoming.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {
                        binding.loadingUpcoming.show()
                    }

                    is RequestState.Success -> {
                        binding.loadingUpcoming.hide()
                        it.data?.results?.let { data -> upcomingAdapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        binding.loadingUpcoming.hide()
                    }
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollHorizontally(1)) {
                viewModel.getPlayingMovie()
            }
        }
    }

    private val upcomingScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollHorizontally(1)) {
                viewModel.getUpcomingMovie()
            }
        }
    }
}