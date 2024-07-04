package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class ProductDetailResponseModel {

        @SerializedName("id")
        var id = 0

        @SerializedName("product_name")
        var productName = ""

        @SerializedName("purchase_type")
        var purchaseType: Int? = null

        @SerializedName("is_summer_program_subscribed")
        var isSummerProgramSubscribed: Int = 0
}