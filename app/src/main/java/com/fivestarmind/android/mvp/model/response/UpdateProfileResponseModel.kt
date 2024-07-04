package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class UpdateProfileResponseModel {

    @SerializedName("user")
    var user = ProfileResponseModel()

    @SerializedName("message")
    var message = ""

}