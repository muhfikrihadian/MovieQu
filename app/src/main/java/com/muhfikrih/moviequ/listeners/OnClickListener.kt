package com.muhfikrih.moviequ.listeners

import com.muhfikrih.moviequ.models.DataMovie

interface OnClickListener {
    fun onClicked(movie: DataMovie, genres: String)
}