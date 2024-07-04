package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class SuggestionsRequestModel {

    @SerializedName("suggestion_title")
    internal var suggestionTitle = ""

    // this field is used for contact us
    @SerializedName("subject")
    internal var subject = ""

    @SerializedName("note")
    internal var note = ""
}