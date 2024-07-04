package com.fivestarmind.android.enum

import com.fivestarmind.android.interfaces.Constants

enum class WebViewType  constructor(override val id: Int, private val value: String) :
    IdentifierType<Int> {

    PRIVACY_POLICY(Constants.App.Number.ONE, "PRIVACY_POLICY"),
    TERMS_AND_CONDITIONS(Constants.App.Number.TWO, "TERMS_AND_CONDITIONS"),
    FAQ(Constants.App.Number.THREE, "FAQ");

    override fun toString(): String = this.value

    companion object {
        fun valueOf(id: Int): WebViewType? = EnumHelper.INSTANCE.valueOf(id, values())
    }
}
