package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class UserProfilleResponseModel(

	@field:SerializedName("data")
	val data: Data? = null
)

data class Data(

	@field:SerializedName("profile_img")
	val profileImg: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
