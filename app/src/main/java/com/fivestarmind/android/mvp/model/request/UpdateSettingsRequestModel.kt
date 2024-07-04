package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class UpdateSettingsRequestModel {

    @SerializedName("type")
    internal var type = ""

    @SerializedName("action")
    internal var action = 0
}