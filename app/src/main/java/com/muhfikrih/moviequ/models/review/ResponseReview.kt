package com.muhfikrih.moviequ.models.review

import com.google.gson.annotations.SerializedName

data class ResponseReview(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null,

	@field:SerializedName("total_pages")
	val totalPages: Int? = null,

	@field:SerializedName("results")
	val results: MutableList<ResultsItem>,

	@field:SerializedName("total_results")
	val totalResults: Int? = null
)