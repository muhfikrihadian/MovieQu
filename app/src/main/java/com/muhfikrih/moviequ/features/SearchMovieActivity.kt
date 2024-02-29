package com.muhfikrih.moviequ.features

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.R
import com.muhfikrih.moviequ.adapters.SearchMovieAdapter
import com.muhfikrih.moviequ.api.RequestState
import com.muhfikrih.moviequ.databinding.ActivitySearchMovieBinding
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.DataMovie
import com.muhfikrih.moviequ.viewmodels.MovieViewModel

class SearchMovieActivity : AppCompatActivity() {
    private var _binding: ActivitySearchMovieBinding? = null
    private val binding get() = _binding!!
    private var adapter: SearchMovieAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    private var isSearchAgain = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestThenObserveAnychangeGenres()
        binding.search.setText(intent.getStringExtra(query))
        if (!isSearchAgain) viewModel.searchMovie(binding.search.text.toString())
        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword!"
                else -> {
                    isSearchAgain = true
                    viewModel.searchMovie(query)
                }
            }
        }
        observeAnychangeSearchMovie()
        setupRecyclerView()
        adapter?.onClickListener(object : OnClickListener {
            override fun onClicked(movies: DataMovie, genres: String) {
                val intent = Intent(this@SearchMovieActivity, MovieDetailActivity::class.java)
                intent.putExtra(MovieDetailActivity.movie, movies)
                intent.putExtra(MovieDetailActivity.genres, genres)
                startActivity(intent)
            }
        })
    }

    private fun observeAnychangeSearchMovie() {
        viewModel.searchResponse.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> showLoading()
                    is RequestState.Success -> {
                        hideLoading()
                        it.data?.results?.let { data -> adapter?.differ?.submitList(data.toList()) }
                    }

                    is RequestState.Error -> {
                        hideLoading()
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

    private fun setupRecyclerView() {
        adapter = SearchMovieAdapter()
        layoutManager = GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            rcyMovieList.adapter = adapter
            rcyMovieList.layoutManager = layoutManager
            rcyMovieList.addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.searchMovie(binding.search.text.toString())
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }

    private fun hideLoading() {
        binding.loading.hide()
    }

    companion object {
        const val query = "query"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}