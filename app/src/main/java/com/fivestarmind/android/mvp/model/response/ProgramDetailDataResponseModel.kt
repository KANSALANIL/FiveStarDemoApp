package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class ProgramDetailDataResponseModel {

        @SerializedName("id")
        var id = 0

        @SerializedName("product_name")
        var productName = ""

        @SerializedName("duration")
        var duration = ""

        @SerializedName("image")
        var image = ArrayList<ProgramDetailProductImageModel>()

        @SerializedName("thumbnail")
        var thumbnail = ""

        @SerializedName("price")
        var price = ""

        @SerializedName("status")
        var status = 0

        @SerializedName("description")
        var description = ""

        @SerializedName("skill")
        var skill = 0

        @SerializedName("tectical")
        var tectical = 0

        @SerializedName("intensity")
        var intensity = 0

        @SerializedName("video_count")
        var videoCount = 0

        @SerializedName("pdf_count")
        var pdfCount = 0

        @SerializedName("purchase_type")
        var purchaseType: Int? = null

        @SerializedName("phases")
        var phases = ArrayList<PhaseVideoModel>()

}