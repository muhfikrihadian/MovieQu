package com.muhfikrih.moviequ.features

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muhfikrih.moviequ.BuildConfig
import com.muhfikrih.moviequ.adapters.VideoListAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivityMovieDetailBinding
import com.muhfikrih.moviequ.models.DataMovie
import com.muhfikrih.moviequ.viewmodels.MovieViewModel

class MovieDetailActivity : AppCompatActivity() {
    private var _binding: ActivityMovieDetailBinding? = null
    private val binding get() = _binding!!
    private var videoAdapter: VideoListAdapter? = null
    private val viewModel: MovieViewModel by viewModels()
    private var layoutManager: RecyclerView.LayoutManager? = null

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
                tvRelease.text = releaseDate
                tvRating.text = voteAverage.toString()
                ratingBar.rating = voteAverage?.div(2) ?: 0f
                tvGenre.text = genres.dropLast(2)
                tvSynopsis.text = movie.overview
            }
        }
        getVideos(movie.id ?: 0)
    }

    private fun getVideos(id: Int){
        viewModel.getVideos(id)
        viewModel.videosResponse.observe(this) {
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

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}