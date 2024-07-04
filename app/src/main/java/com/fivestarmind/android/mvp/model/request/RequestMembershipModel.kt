package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName
import java.util.*

class RequestMembershipModel {

    @SerializedName("type")
    internal var type = ""

    @SerializedName("payment_mode")
    internal var paymentMode = ""

    @SerializedName("codes")
    internal var codes = 0

    @SerializedName("start_date")
    internal var startDate = 0L

    @SerializedName("end_date")
    internal var endDate = 0L

    @SerializedName("amount_paid")
    internal var amountPaid = 0.0

    @SerializedName("iap_id")
    internal var iapId = 0

    @SerializedName("receipt")
    internal var receipt = ""

    @SerializedName("token")
    internal var token = ""

    @SerializedName("purchase_date")
    internal var purchaseDate = ""

    @SerializedName("coupon_code")
    internal var couponCode = ""

    @SerializedName("time_zone")
    internal var timeZone = TimeZone.getDefault().getID()

}