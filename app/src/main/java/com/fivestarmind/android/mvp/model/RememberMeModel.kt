package com.fivestarmind.android.mvp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RememberMeModel(
    @SerializedName("rememberMeStatus") val rememberMeStatus: Boolean,
    @SerializedName("termsAndPrivacyPolicy") val termsAndPrivacyPolicy: Boolean,
    @SerializedName("email") val email: String?,
    @SerializedName("paswrd") val paswrd: String?
) : Serializable