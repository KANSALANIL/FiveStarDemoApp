package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class UserPaymentResponseModel {

    @SerializedName("message")
    internal var message = ""

    @SerializedName("transaction_id")
    internal var transactionId = ""

    @SerializedName("amount")
    internal var amount = 0
}