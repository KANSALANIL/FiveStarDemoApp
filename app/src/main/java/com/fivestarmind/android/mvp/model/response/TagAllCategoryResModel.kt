package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class TagAllCategoryResModel(

	@field:SerializedName("data")
	val data: List<TagAllCategoryItem>? = null
)

data class TagAllCategoryItem(

	@field:SerializedName("files_count")
	val filesCount: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("files")
	val files: List<CategoryFilesItem>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("position")
	val position: Int? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ProdVideoTagsItem(

	@field:SerializedName("product_video_id")
	val productVideoId: Int? = null,

	@field:SerializedName("tag_id")
	val tagId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class CategoryFilesItem(

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
	val isFavourite: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("preset_1080p")
	val preset1080p: Any? = null,

	@field:SerializedName("duration")
	val duration: Any? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("product_id")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("temp_path")
	val tempPath: String? = null,

	@field:SerializedName("views")
	val views: Int? = null,

	@field:SerializedName("preset_480p")
	val preset480p: Any? = null,

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("preset_240p")
	val preset240p: Any? = null,

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("prod_video_tags")
	val prodVideoTags: List<ProdVideoTagsItem?>? = null,

	@field:SerializedName("pdf")
	val pdf: Any? = null,

	@field:SerializedName("thumbpath")
	val thumbpath: String? = null,

	@field:SerializedName("favourite_count")
	val favouriteCount: Int? = null,

	@field:SerializedName("iphone4s")
	val iphone4s: Any? = null,

	@field:SerializedName("preset_720p")
	val preset720p: Any? = null,

	@field:SerializedName("status")
	val status: String? = null
)
