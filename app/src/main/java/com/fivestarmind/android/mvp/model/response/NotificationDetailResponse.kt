package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class NotificationDetailData(

	@field:SerializedName("is_viewed")
	val isViewed: Int? = null,

	@field:SerializedName("module_id")
	val moduleId: Int? = null,

	@field:SerializedName("file")
	val file: NotificationFile? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class NotificationFile(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("featured")
	val featured: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("is_favourite")
	val isFavourite: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("duration")
	val duration: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("pdf")
	val pdf: String? = null,

	@field:SerializedName("thumbpath")
	val thumbpath: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("temp_path")
	val tempPath: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("product")
	val product: NotificationProduct? = null


)

data class NotificationDetailResponse(

	@field:SerializedName("data")
	val data: NotificationDetailData? = null
)

data class NotificationProduct(
	@field:SerializedName("id")
	var ProductId: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	)
