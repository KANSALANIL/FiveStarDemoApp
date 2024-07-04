package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class CouponResponseModel {

    class CouponResponseFirstModel {
        @SerializedName("coupon")
        internal var coupon = CouponResponseThirdModel()
    }

    class CouponResponseSecondModel {
        @SerializedName("coupons")
        internal var coupons = arrayListOf<CouponResponseThirdModel>()
    }

    class CouponResponseThirdModel {
        @SerializedName("id")
        internal var id = 0

        @SerializedName("number")
        internal var number = ""

        @SerializedName("coupon_type")
        internal var couponType = ""

        @SerializedName("discount")
        internal var discount = ""
    }
}