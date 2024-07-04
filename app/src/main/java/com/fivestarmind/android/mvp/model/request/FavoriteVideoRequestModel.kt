package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FavoriteVideoRequestModel(
    @SerializedName("product_video_id") val productVideoId: Int,
    @SerializedName("action") val action: Int
) : Serializable