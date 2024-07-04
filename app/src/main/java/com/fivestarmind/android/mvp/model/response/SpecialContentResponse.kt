package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class SpecialContentResponse(

    @field:SerializedName("data")
    val data: ArrayList<SpecialContentItem>? = null
)

data class SpecialContentItem(

    @field:SerializedName("file")
    val file: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("cover_image")
    val coverImage: String = "",

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("created_by")
    val createdBy: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("duration")
    val duration: String? = null,

    @field:SerializedName("file_type")
    val fileType: String = "",

    @field:SerializedName("content")
    val content: Content? = null
)

data class Content(
    @field:SerializedName("heading")
    val heading: String = ""
)

