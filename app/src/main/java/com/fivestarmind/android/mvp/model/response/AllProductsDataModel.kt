package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class AllProductsDataModel {

    @SerializedName("id")
    var id = ""

    @SerializedName("product_name")
    var productName = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("image")
    var image = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("description")
    var description = ""

    @SerializedName("phases")
    var phases = ArrayList<PhaseVideoModel>()

    @SerializedName("totalVideos")
    var totalVideos = 0

    @SerializedName("totalPdfs")
    var totalPdfs = 0
}