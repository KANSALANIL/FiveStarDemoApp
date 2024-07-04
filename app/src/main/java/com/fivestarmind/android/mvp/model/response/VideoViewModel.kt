package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VideoViewModel {

    @SerializedName("id")
    @Expose
    var id: Int? = 0

    @SerializedName("user_id")
    @Expose
    var userId: Int? = 0

    @SerializedName("duration")
    @Expose
    var duration: String? = ""

    @SerializedName("product_video_id")
    @Expose
    var productVideoId: Int? = null

}