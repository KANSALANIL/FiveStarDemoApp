package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class RequestChangePassword {

    @SerializedName("old_password")
    internal var oldPassword = ""

    @SerializedName("password")
    internal var password = ""

    @SerializedName("password_confirmation")
    internal var passwordConfirmation = ""
}