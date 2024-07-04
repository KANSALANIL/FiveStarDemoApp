package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class VideosRequestModel {

    @SerializedName("videos")
    internal var videos = ArrayList<VideoDurationRequestModel>()
}