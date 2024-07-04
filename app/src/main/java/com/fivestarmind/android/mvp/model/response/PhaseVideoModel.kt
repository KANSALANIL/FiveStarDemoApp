package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhaseVideoModel {

    @SerializedName("id")
    var id = ""

    @SerializedName("product_id")
    var productId = ""

    @SerializedName("title")
    var title = ""

    @SerializedName("subtitle")
    var subtitle = ""

    @SerializedName("list_type")
    var listType = ""

    @SerializedName("totalPdfs")
    var totalPdfs = 0

    @SerializedName("video_count")
    var videoCount = 0

    @SerializedName("pdf_count")
    var pdfCount = 0

    @SerializedName("type")
    var type = ""

    @SerializedName("video_listing")
    var videoListing = ArrayList<PhaseVideoModel>()

    @SerializedName("total")
    var total = ""

    @SerializedName("productImage")
    var productImage = ""

    @SerializedName("thumbnail")
    var thumbnail = ""

    @SerializedName("thumbpath")
    var thumbpath : String? = null

    @SerializedName("pdf")
    var pdf = ""

    @SerializedName("preset_1080p")
    @Expose
    val preset1080p: String = ""

    @SerializedName("preset_720p")
    @Expose
    val preset720p: String = ""

    @SerializedName("preset_480p")
    @Expose
    val preset480p: String = ""

    @SerializedName("preset_360p")
    @Expose
    val preset360p: String = ""

    @SerializedName("preset_240p")
    @Expose
    val preset240p: String = ""

    @SerializedName("iphone4s")
    @Expose
    val iphone4s: String = ""

    @SerializedName("duration")
    @Expose
    var duration: String? = ""

    // This fields are used when user like/unlike video
    @SerializedName("is_my_favorite")
    var isMyFav: Int? = null

    @SerializedName("is_mastered_by_me")
    var isMasteredByMe = 0

    @SerializedName("video_view")
    var videoView : VideoViewModel? = null
}