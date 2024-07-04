package com.fivestarmind.android.enum

import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.interfaces.Constants

enum class ItemClickType constructor(override val id: Int, private val value: String) :
    IdentifierType<Int> {

    LIKE_UNLIKE(Constants.App.Number.ONE, "LIKE_UNLIKE"),
    MASTER(Constants.App.Number.TWO, "MASTER"),
    PLAY_VIDEO(Constants.App.Number.THREE, "PLAY_VIDEO"),
    DETAIL(Constants.App.Number.FOUR, "DETAIL"),
    SUBSCRIPTION(Constants.App.Number.FIVE, "SUBSCRIPTION"),
    BUY(Constants.App.Number.SIX, "BUY"),
    SELECT_MEMBERSHIP(Constants.App.Number.SEVEN, "SELECT_MEMBERSHIP"),
    PLAY_WITHOUT_PHASE_VIDEO(Constants.App.Number.EIGHT, "PLAY_WITHOUT_PHASE_VIDEO"),
    PLAY_PHASE_VIDEO(Constants.App.Number.NINE, "PLAY_PHASE_VIDEO"),
    PDF(Constants.App.Number.TEN, "PDF"),
    APPLY(Constants.App.Number.ELEVEN, "APPLY"),
    UPDATE_MODEL(Constants.App.Number.TWELVE, "UPDATE_MODEL"),
    OPEN_PDF(Constants.App.Number.THIRTEEN, "OPEN_PDF"),
    PLAY_AUDIO(Constants.App.Number.FOURTEEN, "PLAY_AUDIO"),
    OPEN_IMAGE(Constants.App.Number.FIFTEEN, "OPEN_IMAGE"),
    OPEN_NOTIFICATION(Constants.App.Number.SIXTEEN, "OPEN_NOTIFICATION"),


    //
    SELECT_BOTTOMSHEET_ITEM(AppConst.NUMBER.THIRTEEN, "SELECT_BOTTOMSHEET_ITEM");


    override fun toString(): String = this.value

    companion object {
        fun valueOf(id: Int): ItemClickType? =
            EnumHelper.INSTANCE.valueOf(id, values())
    }
}
