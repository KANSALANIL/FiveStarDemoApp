package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class MembershipTypePackagesResponseModel {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("IAP_id")
    internal var iapId = ""

    @SerializedName("duration")
    internal var duration = ""

    @SerializedName("membership_fee")
    internal var membershipFee = 0.0

    @SerializedName("membership_price")
    internal var membershipPrice = 0.0

    @SerializedName("platform_fee")
    internal var platformFee = 0.0

    @SerializedName("total_fee")
    internal var totalFee = 0.0

    @SerializedName("type")
    internal var type = ""

    @SerializedName("fee_type")
    internal var feeType = ""

    @SerializedName("codes")
    internal var codes = 0

    @SerializedName("isSelected")
    var isSelected: Boolean = false

    @SerializedName("discount")
    var discount: Int = 0
}
