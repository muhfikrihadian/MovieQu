package com.muhfikrih.moviequ.features

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muhfikrih.moviequ.BuildConfig
import com.muhfikrih.moviequ.R
import com.muhfikrih.moviequ.adapters.ReviewAdapter
import com.muhfikrih.moviequ.adapters.VideoListAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivityMovieDetailBinding
import com.muhfikrih.moviequ.helpers.InternetChecker
import com.muhfikrih.moviequ.models.movie.DataMovie
import com.muhfikrih.moviequ.viewmodels.MovieViewModel

class MovieDetailActivity : AppCompatActivity() {
    private var _binding: ActivityMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var videoAdapter: VideoListAdapter? = null
    private var reviewAdapter: ReviewAdapter? = null
    private val viewModel: MovieViewModel by viewModels()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var reviewLayoutManager: RecyclerView.LayoutManager? = null
    private var idMovie: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.parcelable<DataMovie>(movie)?.let {
            intent.getStringExtra(genres)?.let { genres ->
                setupData(it, genres)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val movie = "movie"
        const val genres = "genres"
    }

    private fun setupData(movie: DataMovie, genres: String) {
        with(movie) {
            binding.apply {
                Glide.with(this@MovieDetailActivity)
                    .load("${BuildConfig.PHOTO_BASE_URL}$posterPath").into(ivPoster)
                tvTitle.text = title
                tvGenre.text = ": " + genres.dropLast(2)
                tvRelease.text = ": " + releaseDate
                tvRating.text = if (voteAverage.toString().length > 3) voteAverage.toString()
                    .substring(0, 3) else voteAverage.toString()
                ratingBar.rating = voteAverage?.div(2) ?: 0f
                tvSynopsis.text = movie.overview
            }
        }
        idMovie = movie.id ?: 0
        val networkUtils = InternetChecker(this@MovieDetailActivity)
        if (networkUtils.isNetworkAvailable()) {
            getVideos()
            getReview()
        } else {
            Toast.makeText(
                this@MovieDetailActivity,
                resources.getString(R.string.ErrServer),
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun getVideos() {
        viewModel.getVideos(idMovie)
        viewModel.dataMovieVideo.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {
                        binding.progressVideo.show()
                    }

                    is RequestState.Success -> {
                        binding.progressVideo.hide()
                        it.data?.results?.let { data -> videoAdapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        binding.progressVideo.hide()
                        binding.tvInfoVideo.text = resources.getString(R.string.ErrServer)
                        binding.tvInfoVideo.visibility = View.VISIBLE
                    }
                }
            }
        }
        videoAdapter = VideoListAdapter()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.apply {
            rcyVideo.adapter = videoAdapter
            rcyVideo.layoutManager = layoutManager
        }
    }

    private fun getReview() {
        viewModel.getReview(idMovie)
        viewModel.dataMovieReview.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {
                        binding.progressReview.show()
                    }

                    is RequestState.Success -> {
                        binding.progressReview.hide()
                        if (it.data?.results?.isEmpty() == true) {
                            binding.tvInfoReview.visibility = View.VISIBLE
                        }
                        it.data?.results?.let { data -> reviewAdapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        binding.progressReview.hide()
                        binding.tvInfoReview.text = resources.getString(R.string.ErrServer)
                        binding.tvInfoReview.visibility = View.VISIBLE
                    }
                }
            }
        }
        reviewAdapter = ReviewAdapter()
        reviewLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            rcyReview.adapter = reviewAdapter
            rcyReview.layoutManager = reviewLayoutManager
            rcyReview.addOnScrollListener(reviewScrollListener)
        }
    }

    private val reviewScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.getReview(idMovie)
            }
        }
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}