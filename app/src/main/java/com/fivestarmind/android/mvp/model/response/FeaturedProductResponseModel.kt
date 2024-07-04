package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class FeaturedProductResponseModel {

    @SerializedName("total")
    val total = ""

    @SerializedName("featuredProducts")
    val featuredProducts = ArrayList<FeaturedProductDetailResponseModel>()
}