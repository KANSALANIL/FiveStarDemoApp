package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class NotificationResponse(

	@field:SerializedName("data")
	val data: ArrayList<NotificationData>? = null
)

data class NotificationData(

	@field:SerializedName("is_viewed")
	val isViewed: Int? = null,

	@field:SerializedName("module_id")
	val moduleId: Int? = null,

	@field:SerializedName("file")
	val file: NotificationFile? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
