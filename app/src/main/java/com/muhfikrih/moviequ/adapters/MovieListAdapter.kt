package com.muhfikrih.moviequ.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.movie.DataMovie
import com.muhfikrih.moviequ.models.genre.GenresItem
import com.bumptech.glide.Glide
import com.muhfikrih.moviequ.BuildConfig
import com.muhfikrih.moviequ.databinding.ItemMovieBinding

class MovieListAdapter() : RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    private lateinit var onClickListener: OnClickListener
    private val genreList = ArrayList<GenresItem>()

    fun onClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener;
    }

    private val differCallback = object : DiffUtil.ItemCallback<DataMovie>() {
        override fun areItemsTheSame(oldItem: DataMovie, newItem: DataMovie): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DataMovie, newItem: DataMovie): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun setGenres(list: List<GenresItem>) {
        this.genreList.clear()
        this.genreList.addAll(list)
    }

    inner class ViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]) {
                binding.apply {
                    Glide.with(itemView).load("${BuildConfig.PHOTO_BASE_URL}$posterPath").into(ivPoster)
                    val map = genreList.associate { it.id to it.name }
                    val genres = StringBuilder()
                    val genresId = ArrayList<Int>()
                    if (genreIds != null) {
                        genresId.addAll(genreIds)
                        for (data in genreIds) {
                            genres.append("${map[data]}, ")
                        }
                    }
//                    genre.text = genres.dropLast(2)
                    btnTicket.setOnClickListener {
                        onClickListener.onClicked(this@with, genres.toString())
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}