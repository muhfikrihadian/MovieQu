package com.muhfikrih.moviequ.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muhfikrih.moviequ.databinding.ItemReviewBinding
import com.muhfikrih.moviequ.listeners.OnClickListener
import com.muhfikrih.moviequ.models.review.ResultsItem

class ReviewAdapter() : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    private lateinit var onClickListener: OnClickListener

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

    inner class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]) {
                binding.apply {
                    val words = author?.split(" ")
                    val initials = words?.mapNotNull { it.firstOrNull() }
                    val initName = initials?.joinToString(separator = "")
                    tvName.text = author
                    tvRating.text = authorDetails?.rating.toString()
                    tvReview.text = content
                    tvProfile.text = if(initName == null) "-" else initName?.toUpperCase()
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}