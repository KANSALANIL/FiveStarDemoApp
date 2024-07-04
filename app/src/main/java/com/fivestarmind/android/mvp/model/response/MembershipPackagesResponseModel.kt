package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class MembershipPackagesResponseModel {

    @SerializedName("type")
    internal var membershipType = ""

    @SerializedName("data")
    internal var data: ArrayList<MembershipTypePackagesResponseModel>? = null

    @SerializedName("isSelected")
    var isSelected: Boolean = false

    @SerializedName("coupon")
    var coupon: String = ""

    @SerializedName("discount")
    var discount: Int = 0

    @SerializedName("pro_access")
    var proAccess: Int = 0
}