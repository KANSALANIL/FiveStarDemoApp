package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

data class BookMarkResponseModel(
    @field:SerializedName("data")
    val data:BookMarkData? = null
    /*@field:SerializedName("data")
    val data: ArrayList<BookMarkDataItem>? = null*/
)

data class BookMarkData(

    @field:SerializedName("per_page")
    val perPage: Int? = null,

    /*@field:SerializedName("data")
    val data: List<DataItem?>? = null,*/

    @field:SerializedName("data")
    val data: ArrayList<BookMarkDataItem>? = null,

    @field:SerializedName("last_page")
    val lastPage: Int? = null,

    @field:SerializedName("next_page_url")
    val nextPageUrl: Any? = null,

    @field:SerializedName("prev_page_url")
    val prevPageUrl: Any? = null,

    @field:SerializedName("first_page_url")
    val firstPageUrl: String? = null,

    @field:SerializedName("path")
    val path: String? = null,

    @field:SerializedName("total")
    val total: Int? = null,

    @field:SerializedName("last_page_url")
    val lastPageUrl: String? = null,

    @field:SerializedName("from")
    val from: Int? = null,

    @field:SerializedName("links")
    val links: List<ProductCategoryResponseModel.LinksItem?>? = null,

    @field:SerializedName("to")
    val to: Int? = null,

    @field:SerializedName("current_page")
    val currentPage: Int? = null
)

data class BookMarkDataItem(

    @field:SerializedName("featured")
    var featured: Int? = null,

    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("pdf")
    var pdf: String? = null,

    @field:SerializedName("thumbpath")
    var thumbpath: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("id")
    var id: Int? = null,

    @field:SerializedName("is_favourite")
    var isFavourite: Boolean = false,

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("temp_path")
    var tempPath: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("duration")
    var duration: String? = null,

    @SerializedName("product")
    var product: ProductBookMarkName?= null,

    @SerializedName("video_view")
    var videoView: VideoViewModel? = null
)

data class ProductBookMarkName(
    @field:SerializedName("id")
    var ProductId: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    )

