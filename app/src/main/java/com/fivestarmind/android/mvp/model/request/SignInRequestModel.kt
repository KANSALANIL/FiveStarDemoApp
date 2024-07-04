package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class SignInRequestModel {

    @SerializedName("role")
    internal var role = ""

    @SerializedName("password")
    internal var password = ""

    @SerializedName("device_type")
    internal var deviceType = "android"

   /* @SerializedName("deviceType")
    internal var deviceType = "ANDROID"

    @SerializedName("deviceToken")
    internal var deviceToken = "404"*/

    @SerializedName("remember_me")
    internal var rememberMe = false

    @SerializedName("device_token")
    internal var deviceToken = ""

}