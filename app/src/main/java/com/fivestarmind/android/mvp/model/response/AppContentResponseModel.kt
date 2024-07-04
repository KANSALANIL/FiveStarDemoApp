package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class AppContentResponseModel{

	@SerializedName("data")
	val data : AppContentDataModel? = null

	@SerializedName("success")
	val success : Boolean = false

}