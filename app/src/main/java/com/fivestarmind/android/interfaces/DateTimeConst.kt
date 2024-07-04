package com.fivestarmind.android.interfaces

/**
 * The constants which are used regarding Date and Time are listed here
 */
interface DateTimeConst {

    object DURATION_IN_MILLISECONDS {

        const val ZERO: Long = 0
        const val ONE_SECOND: Long = 1000
        const val ANIMATION_LONG: Long = 200
    }

    object DURATION_IN_HOURS {
        const val ZERO_HOUR: Long = 0
        const val ONE_HOUR: Long = 1
    }

    object DURATION_IN_SECONDS {
        const val ZERO_SECOND: Long = 0
        const val ONE_SECOND: Long = 1

        const val ONE_MINUTE: Long = 60 * ONE_SECOND
        const val TWO_MINUTE: Long = 2 * ONE_MINUTE
        const val TEN_MINUTE: Long = 10 * ONE_MINUTE

        const val ONE_HOUR: Long = 60 * ONE_MINUTE
    }

    object DURATION_IN_MINUTES {
        const val ZERO_MINUTE: Long = 0
        const val ONE_MINUTE: Long = 1
    }

    object PATTERN {
        const val DAY_DATE_TIME: String = "EEEE, M/d 'at' h:mm a"      // Thursday, 5/28 at 12:00 PM
        const val CALENDAR_DATE: String = "dd/MM/yyyy"              // 21/03/2018
    }
}