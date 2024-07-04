package com.fivestarmind.android.enum

import com.fivestarmind.android.interfaces.Constants


enum class MembershipType constructor(override val id: Int, private val value: String) :
    IdentifierType<Int> {

    INDIVIDUAL(Constants.App.Number.ONE, "INDIVIDUAL"),
    TEAM(Constants.App.Number.TWO, "TEAM"),

    CLUB(Constants.App.Number.THREE, "CLUB");

    override fun toString(): String = this.value

    companion object {
        fun valueOf(id: Int): MembershipType? =
            EnumHelper.INSTANCE.valueOf(id, values())
    }
}
