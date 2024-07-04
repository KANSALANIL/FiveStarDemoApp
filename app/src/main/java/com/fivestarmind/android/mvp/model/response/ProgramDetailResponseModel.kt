package com.fivestarmind.android.mvp.model.response

import com.google.gson.annotations.SerializedName

class ProgramDetailResponseModel {

    @SerializedName("data")
    var data = ProgramDetailDataResponseModel()

    @SerializedName("exist_in_cart")
    var existInCart = false

    @SerializedName("already_buy")
    var alreadyBuy = false

    @SerializedName("is_summer_program_subscribed")
    var isSummerProgramSubscribed: Int = 0

    @SerializedName("pro_access")
    var pro_access: Int = 0

    @SerializedName("message")
    var message: String =""

    @SerializedName("membership")
    var membership = MembershipResponseModel.MembershipResponseSecondModel()


}