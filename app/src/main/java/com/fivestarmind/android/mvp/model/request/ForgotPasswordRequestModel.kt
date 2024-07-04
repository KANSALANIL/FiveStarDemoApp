package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class ForgotPasswordRequestModel {

    @SerializedName("email")
    internal var email = ""
}