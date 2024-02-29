package com.muhfikrih.moviequ.models.video

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseVideos(

	@field:SerializedName("id")
	val id: Int? = 0,

	@field:SerializedName("results")
	val results: MutableList<ResultsItem>
) : Parcelable