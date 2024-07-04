package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class BookmarkAllCategoryResModel(

	@field:SerializedName("data")
	val data: ArrayList<BookmarkAllCategoryItem>? = null
)

data class BookmarkAllCategoryItem(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("files")
	val files: ArrayList<BookmarkFilesItem>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("position")
	val position: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class BookmarkFilesItem(

	@field:SerializedName("preset_360p")
	val preset360p: Any? = null,

	@field:SerializedName("featured")
	val featured: Int? = null,

	@field:SerializedName("laravel_through_key")
	val laravelThroughKey: Int? = null,

	@field:SerializedName("current_position")
	val currentPosition: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("is_favourite")
	var isFavourite: Boolean? = null,

	@field:SerializedName("title")
	var title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("preset_1080p")
	val preset1080p: Any? = null,

	@field:SerializedName("duration")
	val duration: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("temp_path")
	var tempPath: String? = null,

	@field:SerializedName("views")
	val views: Int? = null,

	@field:SerializedName("preset_480p")
	val preset480p: Any? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("preset_240p")
	val preset240p: Any? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("pdf")
	val pdf: String? = null,

	@field:SerializedName("thumbpath")
	var thumbpath: String? = null,

	@field:SerializedName("favourite_count")
	val favouriteCount: Int? = null,

	@field:SerializedName("iphone4s")
	val iphone4s: Any? = null,

	@field:SerializedName("preset_720p")
	val preset720p: Any? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@SerializedName("product")
	var product: ProductName? = null


)
