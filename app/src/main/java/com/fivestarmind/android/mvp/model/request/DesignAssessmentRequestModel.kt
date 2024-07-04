package com.fivestarmind.android.mvp.model.request

import com.google.gson.annotations.SerializedName

class DesignAssessmentRequestModel {

    @SerializedName("training_days")
    internal var trainingDays = 0L

    @SerializedName("focus")
    internal var focus = ""

    @SerializedName("goal")
    internal var goal = ""

    @SerializedName("hours_trained")
    internal var hoursTrained = 0L

    @SerializedName("highlights")
    internal var highlights = ""

    @SerializedName("game_days")
    internal var gameDays = 0L

    @SerializedName("minutes_played")
    internal var minutesPlayed = 0L

    @SerializedName("self_report")
    internal var selfReport = ""
}