package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class UserRoleResponseModel(

/*	@field:SerializedName("data")
	val data: ArrayList<UserRoleDataItem?>? = null*/
	@SerializedName("data")
	val data: ArrayList<ProductCategoryDataModel>? = null
)
