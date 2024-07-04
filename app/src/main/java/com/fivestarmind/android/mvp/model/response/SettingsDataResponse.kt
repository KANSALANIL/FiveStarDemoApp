package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class SettingsDataResponse {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("user_id")
    internal var userId: Int? = 0

    @SerializedName("push_notifications")
    internal var pushNotifications: Int? = 0

    @SerializedName("email_notifications")
    internal var emailNotifications: Int? = 0
}