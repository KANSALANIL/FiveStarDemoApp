package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class CommonResponse {

    @SerializedName("message")
    internal var message = ""

    @SerializedName("status")
    internal var status = ""

    @SerializedName("data")
    internal var data = FavoriteResponseModel()
}