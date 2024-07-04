package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class ProfileResponseModel {

    @SerializedName("name")
    var name = ""

    @SerializedName("email")
    var email = ""

    @SerializedName("avatar")
    var image = ""

    @SerializedName("userPhone")
    var userPhone = ""

    @SerializedName("club_college")
    var clubCollege = ""

    @SerializedName("state")
    var state = ""

}