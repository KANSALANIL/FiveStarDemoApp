package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*


class MyProgramsResponseDataModel {

    @SerializedName("id")
    @Expose
    val id: Int = 0

    @SerializedName("payment_id")
    @Expose
    val paymentId: Int = 0

    @SerializedName("user_id")
    @Expose
    val userId: Int = 0

    @SerializedName("product_id")
    @Expose
    val productId: Int = 0

    @SerializedName("product_name")
    @Expose
    val productName: String = ""

    @SerializedName("duration")
    @Expose
    val duration: String = ""

    @SerializedName("image")
    @Expose
    val image: String = ""

    @SerializedName("thumbnail")
    @Expose
    val thumbnail: String = ""

    @SerializedName("status")
    @Expose
    val status: Int = 0

    @SerializedName("description")
    @Expose
    val description: String = ""

    @SerializedName("totalVideos")
    var videoCount = 0
}