package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class LetsGoResponse(

	@field:SerializedName("data")
	val data: LetsGoDataResponse? = null
)

data class LetsGoDataResponse(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("welcome_text")
	val welcome_text: String? = null,

	@field:SerializedName("background_img")
	val backgroundImg: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("logo_image")
	val logoImage: String? = null,

	@field:SerializedName("welcome_button_text")
	val welcomeButtonText: String? = null
)
