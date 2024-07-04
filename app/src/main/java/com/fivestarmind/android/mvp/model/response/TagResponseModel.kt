package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class TagResponseModel(

	@field:SerializedName("data")
	val data: List<TagResponseItem>? = null
)

data class TagResponseItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("description")
	val description: String? = null
)
