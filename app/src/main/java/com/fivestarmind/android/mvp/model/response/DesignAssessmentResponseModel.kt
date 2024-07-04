package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class DesignAssessmentResponseModel {

    @SerializedName("id")
    internal var id = 0

    @SerializedName("user_id")
    internal var userId = 0

    @SerializedName("training_days")
    internal var trainingDays: Int? = null

    @SerializedName("focus")
    internal var focus = ""

    @SerializedName("goal")
    internal var goal = ""

    @SerializedName("hours_trained")
    internal var hoursTrained: Int? = null

    @SerializedName("highlights")
    internal var highlights = ""

    @SerializedName("game_days")
    internal var gameDays: Int? = null

    @SerializedName("minutes_played")
    internal var minutesPlayed: Int? = null

    @SerializedName("self_report")
    internal var selfReport = ""

    @SerializedName("message")
    internal var message = ""
}