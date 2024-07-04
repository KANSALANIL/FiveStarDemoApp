package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhasesListingResponseModel {

    @SerializedName("current_page")
    var currentPage = 0

    @SerializedName("data")
    var data: ArrayList<FavoriteResponseDataModel>? = null

    @SerializedName("from")
    var from = 0

    @SerializedName("next_page_url")
    @Expose
    val nextPageUrl: Any? = null

    @SerializedName("last_page")
    var lastPage = 0

    @SerializedName("path")
    var path = ""

    @SerializedName("to")
    var to = 0

    @SerializedName("total")
    var total = 0
}