package com.muhfikrih.moviequ.listeners

import com.muhfikrih.moviequ.models.movie.DataMovie

interface OnClickListener {
    fun onClicked(movie: DataMovie, genres: String)
}