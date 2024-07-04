package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class SettingsResponse {

    @SerializedName("message")
    internal var message = ""

    @SerializedName("data")
    internal var data: SettingsDataResponse? = null
}