package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class TodayQuoteResponse(

	@field:SerializedName("data")
	val data: ArrayList<TodayQuoteItem>? = null
)

data class TodayQuoteItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("quote")
	var quote: String? = null,

	@field:SerializedName("image")
	var image: String? = null,

	@field:SerializedName("color")
	var color: String? = null,

	@field:SerializedName("type")
	var type: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("quote_by")
	val quoteBy: String? = null
)
