package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class SignUpRequestModel {

    @SerializedName("name")
    internal var name = ""

    @SerializedName("email")
    internal var email = ""

    @SerializedName("password")
    internal var password = ""

    @SerializedName("deviceType")
    internal var deviceType = "ANDROID"

    @SerializedName("deviceToken")
    internal var deviceToken = "404"

    @SerializedName("accept_term")
    internal var acceptTerm = "1"

    @SerializedName("club_college")
    internal var clubCollege = ""

    @SerializedName("state")
    internal var state = ""

    @SerializedName("code")
    internal var referralCode = ""
}