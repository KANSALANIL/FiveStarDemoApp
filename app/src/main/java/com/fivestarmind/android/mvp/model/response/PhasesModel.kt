package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class PhasesModel {

    @SerializedName("id")
    var id = ""

    @SerializedName("product_id")
    var productId = ""

    @SerializedName("title")
    var title = ""

    @SerializedName("subtitle")
    var subtitle = ""

    @SerializedName("video_listing")
    var videoListing = ArrayList<VideoModel>()
}