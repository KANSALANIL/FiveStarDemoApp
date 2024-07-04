package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TransactionsPurchasedProductResponseModel {

    @SerializedName("id")
    @Expose
    val id: Int = 0

    @SerializedName("transaction_id")
    @Expose
    val transactionId: Int = 0

    @SerializedName("user_id")
    @Expose
    val userId: Int = 0

    @SerializedName("product_id")
    @Expose
    val productId: Int = 0

    @SerializedName("product_name")
    @Expose
    val productName: String = ""

    @SerializedName("paid_amount")
    @Expose
    val paidAmount: String = ""

    @SerializedName("currency_code")
    @Expose
    val currencyCode: String = ""

    @SerializedName("created_at")
    @Expose
    val createdAt: String = ""

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String = ""

    @SerializedName("image")
    @Expose
    val image: String = ""

    @SerializedName("thumbnail")
    @Expose
    val thumbnail: String = ""
}