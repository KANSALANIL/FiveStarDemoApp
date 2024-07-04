package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class SignInSignUpResponseModel {

    class SignInSignUpResponseFirstModel {
        @SerializedName("message")
        internal var message = ""

        @SerializedName("data")
        internal var data = SignInSignUpResponseDataModel()
    }

    class SignInSignUpResponseDataModel {

        @SerializedName("user_id")
        internal var userId = ""

        @SerializedName("name")
        internal var name = ""

        @SerializedName("email")
        internal var email = ""

        @SerializedName("token")
        internal var token = ""

        internal var password = ""

        @SerializedName("club_college")
        internal var clubCollege = ""

        @SerializedName("state")
        internal var state = ""

        @SerializedName("code")
        internal var referralCode = ""

        @SerializedName("pro_access")
        internal var proAccess = 0
    }
}