package com.fivestarmind.android.mvp.model.response

import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.google.gson.annotations.SerializedName

data class GetProductFileResponse(

	@field:SerializedName("data")
	val data:  SeeAllDataItem? = null
)

