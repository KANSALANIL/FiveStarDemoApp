package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse : Serializable {

    @SerializedName("error_description")
    internal var errorDescription = ""

    @SerializedName("error")
    internal var error = ""

    @SerializedName("message")
    internal var message = ""

    @SerializedName("errors")
    internal var errors = ErrorResponseFirstModel()

    class ErrorResponseFirstModel : Serializable {

        @SerializedName("email", alternate = ["name", "password", "deviceType", "deviceToken", "old_password", "password_confirmation"])
        internal var email = ""
    }
}