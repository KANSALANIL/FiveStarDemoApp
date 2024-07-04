package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class CartCouponProductResponseModel {

    @SerializedName("coupon_id")
    internal var couponId = 0

    @SerializedName("product_id")
    internal var productId = 0

}