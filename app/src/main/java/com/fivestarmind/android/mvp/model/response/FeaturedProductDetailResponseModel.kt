package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class FeaturedProductDetailResponseModel {

    @SerializedName("id")
    var productId = ""

    @SerializedName("product_name")
    var productName = ""

    @SerializedName("duration")
    var duration = ""

    @SerializedName("image")
    var image = ""

    @SerializedName("thumbnail")
    var thumbnail = ""

    @SerializedName("price")
    var price = ""

    @SerializedName("totalVideos")
    var totalVideos = 0

    @SerializedName("totalPdfs")
    var totalPdfs = 0
}