package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TransactionsResponseModel {

    @SerializedName("current_page")
    @Expose
    val currentPage: Int = 0

    @SerializedName("data")
    @Expose
    val data: ArrayList<TransactionsResponseDataModel>? = null

    @SerializedName("from")
    @Expose
    val from: Int = 0

    @SerializedName("last_page")
    @Expose
    val lastPage: Int = 0

    @SerializedName("next_page_url")
    @Expose
    val nextPageUrl: Any? = null

    @SerializedName("path")
    @Expose
    val path: String = ""

    @SerializedName("to")
    @Expose
    val to: Int = 0

    @SerializedName("total")
    @Expose
    val total: Int = 0
}