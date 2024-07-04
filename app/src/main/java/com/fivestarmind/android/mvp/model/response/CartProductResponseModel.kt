package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName
import java.util.*

class CartProductResponseModel {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("user_id")
    internal var userId = 0

    @SerializedName("product_id")
    internal var productId = 0

    @SerializedName("product_name")
    internal var productName = ""

    @SerializedName("duration")
    internal var duration = ""

    @SerializedName("image")
    internal var image = ""

    @SerializedName("thumbnail")
    internal var thumbnail = ""

    @SerializedName("status")
    internal var status = 0

    @SerializedName("description")
    internal var description = ""

    @SerializedName("price")
    internal var price = ""

    @SerializedName("coupon_applied")
    internal var couponApplied = false

    @SerializedName("coupon_product")
    internal var couponProduct = ArrayList<CartCouponProductResponseModel>()

    var priceAfterCouponApplied = 0.0

    var couponDiscount = ""

    var discount = ""

}