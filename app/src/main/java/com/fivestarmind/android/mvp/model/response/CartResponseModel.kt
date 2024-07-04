package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName
import java.util.*

class CartResponseModel {

    @SerializedName("product")
    internal var product = ArrayList<CartProductResponseModel>()

    @SerializedName("total_amount")
    internal var totalAmount = ""

    @SerializedName("total_discount")
    internal var totalDiscount = ""

    @SerializedName("total_after_discount")
    internal var totalAfterDiscount = ""
}