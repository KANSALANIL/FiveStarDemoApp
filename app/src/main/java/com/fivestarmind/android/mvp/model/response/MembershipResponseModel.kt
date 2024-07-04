package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class MembershipResponseModel {

    class MembershipResponseFirstModel {
        @SerializedName("message")
        internal var message = ""

        @SerializedName("data")
        internal var data : MembershipResponseSecondModel? = null
    }

    class MembershipResponseSecondModel {
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

        @SerializedName("is_canceled")
        internal var isCanceled: Int? = null

        @SerializedName("canceled_at")
        internal var canceledAt = 0L

        @SerializedName("current_period_end")
        internal var currentPeriodEnd = 0L

        @SerializedName("current_period_start")
        internal var currentPeriodStart = 0L

        @SerializedName("pro_access")
        internal var pro_access: Int? = null
    }
}