package com.muhfikrih.moviequ.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.databinding.ItemVideoBinding
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.responses.ResultsItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


class VideoListAdapter() : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private lateinit var onClickListener: OnClickListener
    private val videoList = ArrayList<ResultsItem>()

    fun onClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener;
    }

    private val differCallback = object : DiffUtil.ItemCallback<ResultsItem>() {
        override fun areItemsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ResultsItem, newItem: ResultsItem): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]) {
                binding.apply {
                    ytPlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            val videoId = key ?: ""
                            youTubePlayer.loadVideo(videoId, 0f)
                            youTubePlayer.pause()
                        }
                    })
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}