package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class MembershipCouponResponseModel {

    class MembershipCouponResponseFirstModel {
        @SerializedName("data")
        internal var membershipCouponResponseSecondModel = MembershipCouponResponseSecondModel()
    }

    class MembershipCouponResponseSecondModel {

        @SerializedName("id")
        internal var id = 0

        @SerializedName("customer")
        internal var customer = ""

        @SerializedName("discount_value")
        internal var discountValue = 0

        @SerializedName("discount_unit")
        internal var discountUnit = ""

        @SerializedName("coupon_code")
        internal var couponCode = ""

        @SerializedName("user_info")
        internal var userInfoModel = SignInSignUpResponseModel.SignInSignUpResponseDataModel()
    }
}