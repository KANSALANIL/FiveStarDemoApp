package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class ProductRequestModel {

    @SerializedName("categoryId")
    internal var categoryId = ""

    @SerializedName("limit")
    internal var limit = 0

    @SerializedName("page")
    internal var page = 0
}