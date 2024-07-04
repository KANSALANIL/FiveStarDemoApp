package com.fivestarmind.android.constant

/**
 * The constants which are used regarding Date and Time are listed here
 */
interface DateTimeConst {

    object DURATION_IN_MILLISECONDS {
        const val ZERO: Long = 0
        const val ONE: Long = 1
        const val ONE_SECOND: Long = 1000
        const val NINETY_SECOND: Long = 90 * ONE_SECOND
        const val ONE_MINUTE: Long = 60 * ONE_SECOND
        const val TWO_MINUTE: Long = 2 * ONE_MINUTE
        const val FIVE_MINUTE: Long = 5 * ONE_MINUTE
        const val FIFTEEN_MINUTE: Long = 15 * ONE_MINUTE
        const val ONE_HOUR: Long = 60 * ONE_MINUTE
        const val ONE_DAY: Long = 24 * ONE_HOUR
        const val ONE_WEEK: Long = 7 * ONE_DAY

        const val SPLASH: Long = 2 * ONE_SECOND
        const val CLOSE_APP_DELAY: Long = 2 * ONE_SECOND
        const val CLICK_DELAY: Long = 400
        const val SCREEN_SWITCH_DELAY: Long = 500

        const val ANIMATION_SHORT: Long = 100
        const val ANIMATION_LONG: Long = 200

        const val DELAY_SHORT: Long = 150

        const val SEARCH_DELAY: Long = 500
        const val SEARCH_INTERVAL: Long = 100

        const val STAY_SIGN_IN_DIALOG_DELAY: Long = 5 * ONE_SECOND
    }

    object DURATION_IN_SECONDS {
        const val ZERO_SECOND: Long = 0
        const val ONE_SECOND: Long = 1
        const val THIRTY_SECOND: Long = 30
        const val FIFTY_NINE_SECOND: Long = 59

        const val ONE_MINUTE: Long = 60 * ONE_SECOND
        const val TWO_MINUTE: Long = 2 * ONE_MINUTE
        const val TEN_MINUTE: Long = 10 * ONE_MINUTE
        const val FIFTY_NINE_MINUTE: Long = 59 * ONE_MINUTE

        const val ONE_HOUR: Long = 60 * ONE_MINUTE
        const val TWENTY_FOUR_HOUR: Long = 24 * ONE_HOUR

        const val ONE_DAY: Long = 24 * ONE_HOUR
    }

    object DURATION_IN_MINUTES {
        const val ZERO_MINUTE: Long = 0
        const val ONE_MINUTE: Long = 1
        const val FIFTY_NINE_MINUTE: Long = 59
    }

    object DURATION_IN_HOURS {
        const val ZERO_HOUR: Long = 0
        const val ONE_HOUR: Long = 1
        const val TWELVE_HOUR: Long = 12
        const val TWENTY_THREE_HOUR: Long = 23
    }

    object DURATION_IN_DAYS {
        const val ONE_DAY: Long = 1
        const val ONE_WEEK: Long = 7 * ONE_DAY
        const val THIRTY_DAYS: Long = 30 * ONE_DAY
        const val SIXTY_DAYS: Long = 60 * ONE_DAY
    }

    object PATTERN {
        const val DAY_DATE_TIME: String = "EEEE, M/d 'at' h:mm a"      // Thursday, 5/28 at 12:00 PM
        const val DATE_TIME: String = "M/d 'at' h:mm a"              // 5/28 at 12:00 PM
        const val DATE_TIME_: String = "yyyy-MM-dd HH:mm:ss"              // 2021-02-16 04:35:23  with 24 hours format
        const val DATE: String = "dd, MMM yyyy"                     // 21, Mar 2018
        const val TIME: String = "hh:mma"                           // 04:32PM

        const val DAY: String = "dd"                                // 21
        const val MONTH: String = "MMM"                             // Mar
        const val YEAR: String = "yyyy"                             // 2019
        const val DAY_OF_WEEK: String = "EEE"                       // Mon
        const val DATE_CALENDAR_SELECTION: String = "dd MMM yyyy"   // 21 Mar 2018
        const val DATE_TODAY: String = "'Today', MMM yyyy"          // Today, Mar 2018
        fun getOrdinalDate(oi: String) = "d'$oi', MMM yyyy"         // 21st, Mar 2018

        const val API_ENCODED_DATE: String = "yyyy-MM-dd"           // 2018-03-21
        const val API_DECODED_DATE: String = "MM/dd/yyyy"           // 03/21/2018

        const val CALENDAR_DATE: String = "MM/dd/yyyy"              // 03/21/2018
        const val CALENDAR_TIME: String = "hh:mm a"                 // 04:32 PM
    }

    object REGEX {
        const val DATE_DD_MM_YYYY = "^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}\$"
        const val TIME_HH_MM = "^(0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]\$"
    }

}