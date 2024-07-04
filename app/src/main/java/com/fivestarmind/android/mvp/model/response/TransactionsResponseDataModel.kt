package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TransactionsResponseDataModel {

        @SerializedName("id")
        @Expose
        val id: Int = 0

        @SerializedName("pay_id")
        @Expose
        val payId: String = ""

        @SerializedName("status")
        @Expose
        val status: String = ""

        @SerializedName("amount")
        @Expose
        val amount: String = ""

        @SerializedName("payment_type")
        @Expose
        val paymentType: String = ""

        @SerializedName("date")
        @Expose
        val date: String = ""
}