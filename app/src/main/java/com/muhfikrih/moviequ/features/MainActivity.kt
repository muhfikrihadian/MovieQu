package com.muhfikrih.moviequ.features

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.muhfikrih.moviequ.R
import com.muhfikrih.moviequ.adapters.GenresAdapter
import com.muhfikrih.moviequ.adapters.MovieListAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivityMainBinding
import com.muhfikrih.moviequ.helpers.InternetChecker
import com.muhfikrih.moviequ.listeners.OnClickGenreListener
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
    private var genreAdapter: GenresAdapter? = null
    private var layoutManagerNowplaying: RecyclerView.LayoutManager? = null
    private var layoutManagerUpcoming: RecyclerView.LayoutManager? = null
    private var layoutManagerGenre: FlexboxLayoutManager? = null

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
        val networkUtils = InternetChecker(this@MainActivity)
        if (networkUtils.isNetworkAvailable()) {
            getGenres()
            viewModel.getPlayingMovie()
            viewModel.getUpcomingMovie()
            getPlayingMovie()
            getUpcomingMovies()
        } else {
            binding.loadingGenres.hide()
            binding.loadingNowPlaying.hide()
            binding.loadingUpcoming.hide()
            binding.tvInfoGenres.text = resources.getString(R.string.ErrServer)
            binding.tvInfoPlaying.text = resources.getString(R.string.ErrServer)
            binding.tvInfoUpcoming.text = resources.getString(R.string.ErrServer)
            binding.tvInfoGenres.visibility = View.VISIBLE
            binding.tvInfoPlaying.visibility = View.VISIBLE
            binding.tvInfoUpcoming.visibility = View.VISIBLE
        }
        nowplayingAdapter = MovieListAdapter()
        upcomingAdapter = MovieListAdapter()
        genreAdapter = GenresAdapter()
        layoutManagerUpcoming = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerNowplaying = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerGenre = FlexboxLayoutManager(this)
        layoutManagerGenre!!.flexWrap = FlexWrap.WRAP
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
        binding.apply {
            rcyGenres.adapter = genreAdapter
            rcyGenres.layoutManager = layoutManagerGenre
        }
        genreAdapter?.onClickListener(object : OnClickGenreListener {
            override fun onClicked(id: Int) {
                val intent = Intent(this@MainActivity, GenreActivity::class.java)
                intent.putExtra(GenreActivity.genreId, id.toString())
                startActivity(intent)
            }
        })
        nowplayingAdapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movie: DataMovie, genres: String) {
                if (networkUtils.isNetworkAvailable()) {
                    val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                    intent.putExtra(MovieDetailActivity.movie, movie)
                    intent.putExtra(MovieDetailActivity.genres, genres)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.ErrServer),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        upcomingAdapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movie: DataMovie, genres: String) {
                if (networkUtils.isNetworkAvailable()) {
                    val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                    intent.putExtra(MovieDetailActivity.movie, movie)
                    intent.putExtra(MovieDetailActivity.genres, genres)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.ErrServer),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword"
                else -> {
                    if (networkUtils.isNetworkAvailable()) {
                        val intent = Intent(this, SearchMovieActivity::class.java)
                        intent.putExtra(SearchMovieActivity.query, query)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            resources.getString(R.string.ErrServer),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                    is RequestState.Loading -> {
                        binding.loadingGenres.show()
                    }

                    is RequestState.Success -> {
                        binding.loadingGenres.hide()
                        it.data.genres?.let { data ->
                            genreAdapter?.differ?.submitList(data.toList())
                            nowplayingAdapter?.setGenres(data)
                            upcomingAdapter?.setGenres(data)
                        }
                    }

                    is RequestState.Error -> {
                        binding.loadingGenres.show()
                        binding.tvInfoGenres.text = resources.getString(R.string.ErrServer)
                        binding.tvInfoGenres.visibility = View.VISIBLE
                    }
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
                        binding.tvInfoPlaying.text = resources.getString(R.string.ErrServer)
                        binding.tvInfoPlaying.visibility = View.VISIBLE
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
                        binding.tvInfoUpcoming.text = resources.getString(R.string.ErrServer)
                        binding.tvInfoUpcoming.visibility = View.VISIBLE
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