package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NewVideosModel {
    @SerializedName("id")
    var id = 0

    @SerializedName("product_id")
    var productId = 0

    @SerializedName("title")
    var title = ""

    @SerializedName("thumbnail")
    var thumbnail = ""

    @SerializedName("duration")
    var duration = ""

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

    @SerializedName("is_mastered_by_me")
    var isMasteredByMe = 0

    @SerializedName("already_buy")
    var alreadyBuy:Boolean? = null

    @SerializedName("exist_in_cart")
    var existInCart:Boolean? = null

    @SerializedName("product_info")
    var productInfo = ProductDetailResponseModel()
}