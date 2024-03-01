package com.muhfikrih.moviequ.features

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.adapters.SearchMovieAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivityGenreBinding
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.movie.DataMovie
import com.muhfikrih.moviequ.viewmodels.MovieViewModel

class GenreActivity : AppCompatActivity() {
    private var _binding: ActivityGenreBinding? = null
    private val binding get() = _binding!!
    private var adapter: SearchMovieAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    private var idGenre: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGenreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestThenObserveAnychangeGenres()
        idGenre = intent.getStringExtra(genreId)
        idGenre?.let { viewModel.searchMovie(it) }
        observeAnychangeSearchMovie()
        adapter = SearchMovieAdapter()
        layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            rcyMovieList.adapter = adapter
            rcyMovieList.layoutManager = layoutManager
            rcyMovieList.addOnScrollListener(scrollListener)
        }
        adapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movies: DataMovie, genres: String) {
                val intent = Intent(this@GenreActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movies)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val genreId = "genreId"
    }

    private fun observeAnychangeSearchMovie() {
        viewModel.dataMovieSearch.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {
                        binding.loading.show()
                    }

                    is RequestState.Success -> {
                        binding.loading.hide()
                        it.data?.results?.let { data -> adapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        binding.loading.hide()
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun requestThenObserveAnychangeGenres() {
        viewModel.getGenres().observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {}
                    is RequestState.Success -> it.data.genres?.let { data -> adapter?.setGenres(data) }
                    is RequestState.Error -> Toast.makeText(this, it.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                idGenre?.let { viewModel.searchMovieByGenre(it) }
            }
        }
    }
}