package com.muhfikrih.moviequ.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieResponse(

	@field:SerializedName("page")
	val page: Int? = 0,

	@field:SerializedName("total_pages")
	val totalPages: Int? = 0,

	@field:SerializedName("results")
	val results: MutableList<DataMovie>,

	@field:SerializedName("total_results")
	val totalResults: Int? = 0
) : Parcelable