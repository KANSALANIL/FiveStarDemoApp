package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class JournalListingResponseModel {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("user_id")
    internal var userId = 0

    @SerializedName("date")
    internal var date = ""

    @SerializedName("video")
    internal var video = ""

    @SerializedName("image")
    internal var image = ""

    @SerializedName("cover_image")
    internal var coverImage = ""

    @SerializedName("thumbnail")
    internal var thumbnail = ""

    @SerializedName("note")
    internal var note = ""

    @SerializedName("media_type")
    internal var mediaType = ""

    @SerializedName("journalDate")
    internal var jounalDate = ""
}