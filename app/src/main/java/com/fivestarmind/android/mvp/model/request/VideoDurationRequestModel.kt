package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class VideoDurationRequestModel {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("duration")
    internal var duration = 0
}