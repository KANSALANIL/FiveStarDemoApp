package com.fivestarmind.android.enum

import com.fivestarmind.android.interfaces.Constants


enum class DialogEventType constructor(override val id: Int, private val value: String) :
    IdentifierType<Int> {

    POSITIVE(Constants.App.Number.ONE, "POSITIVE"),
    NEGATIVE(Constants.App.Number.TWO, "NEGATIVE"),

    MASTERED(Constants.App.Number.THREE, "MASTERED"),
    CANCEL(Constants.App.Number.FOUR, "CANCEL"),
    ADD_TO_CART(Constants.App.Number.FIVE, "ADD_TO_CART"),
    VIEW_CART(Constants.App.Number.SIX, "VIEW_CART"),
    UPGRADE_MEMBERSHIP(Constants.App.Number.SEVEN, "UPGRADE_MEMBERSHIP");

    override fun toString(): String = this.value

    companion object {
        fun valueOf(id: Int): DialogEventType? =
            EnumHelper.INSTANCE.valueOf(id, values())
    }
}
