package com.muhfikrih.moviequ.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.databinding.ItemGenreBinding
import com.muhfikrih.moviequ.listeners.OnClickGenreListener
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.genre.GenresItem

class GenresAdapter() : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {
    private lateinit var onClickListener: OnClickGenreListener

    fun onClickListener(onClickListener: OnClickGenreListener) {
        this.onClickListener = onClickListener;
    }

    private val differCallback = object : DiffUtil.ItemCallback<GenresItem>() {
        override fun areItemsTheSame(oldItem: GenresItem, newItem: GenresItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: GenresItem, newItem: GenresItem): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class ViewHolder(val binding: ItemGenreBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]) {
                binding.apply {
                    tvGenre.text = name
                    itemView.setOnClickListener {
                        onClickListener.onClicked(id ?: 0)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}