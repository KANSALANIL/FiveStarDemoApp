package com.fivestarmind.android.helper

import android.os.SystemClock
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.constant.DateTimeConst
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * This is the helper class which contains all the methods or functions regarding Date and Time.
 */
object DateTimeHelper {
    private var TAG = javaClass.simpleName

    private val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat(DateTimeConst.PATTERN.DAY_DATE_TIME, Locale.US)

    private val simpleDateTimeFormat: SimpleDateFormat =
        SimpleDateFormat(DateTimeConst.PATTERN.DATE_TIME_, Locale.US)

    internal fun getCurrentTimestampInMilliseconds(): Long = System.currentTimeMillis()

    internal fun getServerCurrentTimestampInMilliseconds(): Long {
//        val storedSystemElapsedTime: Long? =
//            SharedPreferencesHelper.getSystemElapsedTimeInMilliseconds()
//
//        return if (null != storedSystemElapsedTime)
//            SharedPreferencesHelper.getServerTimestampInMilliseconds() + (getSystemElapsedTime() - storedSystemElapsedTime)
//        else
//            getCurrentTimestampInMilliseconds()

        return getCurrentTimestampInMilliseconds()
    }

    internal fun getServerCurrentTimestampInSeconds(): Long =
        getServerCurrentTimestampInMilliseconds() / AppConst.NUMBER.THOUSAND

    internal fun getSystemElapsedTime(): Long = SystemClock.elapsedRealtime()

    internal fun getCalendarInstance(): Calendar =
        Calendar.getInstance().apply { timeInMillis = getServerCurrentTimestampInMilliseconds() }

    private fun getNewCalendar(date: Date): Calendar =
        getCalendarInstance().apply {
            time = date
        }

    internal fun getCurrentTimeZone(): String =
        TimeZone.getDefault().id

    internal fun formatDayDateTime(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DAY_DATE_TIME)
        }.format(Date(milliseconds))
    }

    internal fun formatDateTime(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DATE_TIME)
        }.format(Date(milliseconds))
    }

    internal fun formatDate(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DATE)
        }.format(Date(milliseconds))
    }

    internal fun formatTime(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.TIME)
        }.format(Date(milliseconds))
    }

    internal fun formatCalendarDate(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.CALENDAR_DATE)
        }.format(Date(milliseconds))
    }

    internal fun formatCalendarTime(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.CALENDAR_TIME)
        }.format(Date(milliseconds))
    }

    internal fun formatDayOfWeek(date: Date): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DAY_OF_WEEK)
        }.format(date)
    }

    internal fun formatDateForCalendarSelection(date: Date): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DATE_CALENDAR_SELECTION)
        }.format(date)
    }

    internal fun formatDateForToday(date: Date): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DATE_TODAY)
        }.format(date)
    }

    internal fun formatDateWithOrdinal(date: Date): String =
        simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.getOrdinalDate(oi = getOrdinalIndicator(date)))
        }.format(date)

    internal fun formatDay(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.DAY)
        }.format(Date(milliseconds))
    }

    internal fun formatMonth(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.MONTH)
        }.format(Date(milliseconds))
    }

    internal fun formatYear(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.YEAR)
        }.format(Date(milliseconds))
    }

    internal fun convertSecondsToMilliseconds(seconds: Long): Long =
        seconds * DateTimeConst.DURATION_IN_MILLISECONDS.ONE_SECOND

    internal fun convertMillisecondsToSeconds(milliseconds: Long): Long =
        milliseconds / DateTimeConst.DURATION_IN_MILLISECONDS.ONE_SECOND

    internal fun isFutureTimeStamp(milliseconds: Long): Boolean =
        milliseconds > getServerCurrentTimestampInMilliseconds()

    internal fun isFutureTimeStampForSeconds(seconds: Long): Boolean =
        convertSecondsToMilliseconds(seconds) > getServerCurrentTimestampInMilliseconds()

    internal fun isPastTimeStamp(milliseconds: Long): Boolean =
        milliseconds < getServerCurrentTimestampInMilliseconds()

    internal fun isPastTimeStampForSeconds(seconds: Long): Boolean =
        convertSecondsToMilliseconds(seconds) < getServerCurrentTimestampInMilliseconds()

    internal fun getTimeLeftInMillisecondsForMilliseconds(milliseconds: Long): Long =
        milliseconds - getServerCurrentTimestampInMilliseconds()

    internal fun getTimeLeftInMillisecondsForSeconds(seconds: Long): Long =
        convertSecondsToMilliseconds(seconds = seconds) - getServerCurrentTimestampInMilliseconds()

    internal fun getTimeLeftInSecondsForSeconds(seconds: Long): Long =
        seconds - getServerCurrentTimestampInSeconds()

    internal fun getTimeElapsedInMillisecondsForMilliseconds(milliseconds: Long): Long =
        getServerCurrentTimestampInMilliseconds() - milliseconds

    internal fun getTimeElapsedInMillisecondsForSeconds(seconds: Long): Long =
        getServerCurrentTimestampInMilliseconds() - convertSecondsToMilliseconds(seconds = seconds)

    private fun getOrdinalIndicator(date: Date): String {
        val day = getNewCalendar(date).get(Calendar.DAY_OF_MONTH)

        when (day) {
            11, 12, 13 -> return "th"
        }

        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

    internal fun formatTimeDurationInMSS(milliseconds: Long): String {
        var min = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        min %= 10

        return String.format("%d:%02d", min, sec)
    }

    internal fun formatTimeDuration(milliseconds: Long): String {
        val hr = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val min = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        val ms = TimeUnit.MILLISECONDS.toMillis(milliseconds) % 1000

        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    internal fun formatSecondsToStringForResendOtp(seconds: Long): String =
        String.format("%02d:%02d", seconds / 60, seconds % 60)

    internal fun isPatternMatches(regex: String, inputString: String): Boolean {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(inputString)

        return matcher.matches()
    }

    internal fun formatToApiEncodedDate(decodedDate: String): String {
        val date = simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.API_DECODED_DATE)
        }.parse(decodedDate)

        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.API_ENCODED_DATE)
        }.format(date)
    }

    internal fun formatToApiDecodedDate(encodedDate: String): String {
        val date = simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.API_ENCODED_DATE)
        }.parse(encodedDate)

        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.API_DECODED_DATE)
        }.format(date)
    }

    internal fun getCurrentTimeDateTime():String = simpleDateTimeFormat.format(Date())

    internal fun getTimestampForDateWithUpdatedTime(
        date: Date,
        hour: Long,
        min: Long,
        sec: Long
    ): Long =
        date.apply {
            hours = hour.toInt()
            minutes = min.toInt()
            seconds = sec.toInt()
        }.time

    internal fun getStartTimestampForDate(date: Date): Long =
        getTimestampForDateWithUpdatedTime(
            date = date,
            hour = DateTimeConst.DURATION_IN_HOURS.ZERO_HOUR,
            min = DateTimeConst.DURATION_IN_MINUTES.ZERO_MINUTE,
            sec = DateTimeConst.DURATION_IN_SECONDS.ZERO_SECOND
        )

    internal fun getEndTimestampForDate(date: Date): Long =
        getTimestampForDateWithUpdatedTime(
            date = date,
            hour = DateTimeConst.DURATION_IN_HOURS.TWENTY_THREE_HOUR,
            min = DateTimeConst.DURATION_IN_MINUTES.FIFTY_NINE_MINUTE,
            sec = DateTimeConst.DURATION_IN_SECONDS.FIFTY_NINE_SECOND
        )

    internal fun getCurrentYear(): Int =
        getCalendarInstance().get(Calendar.YEAR)

    internal fun getCurrentMonth(): Int =
        getCalendarInstance().get(Calendar.MONTH)

    internal fun getCurrentDay(): Int =
        getCalendarInstance().get(Calendar.DAY_OF_MONTH)

    internal fun getCurrentHour(): Int =
        getCalendarInstance().get(Calendar.HOUR)

    internal fun getCurrentMinute(): Int =
        getCalendarInstance().get(Calendar.MINUTE)

    internal fun getTimestampForDob(): Long =
        getCalendarInstance().apply {
            add(Calendar.YEAR, -AppConst.NUMBER.TWELVE)
        }.timeInMillis

    internal fun updateDateInCalendarInstance(
        calendar: Calendar,
        day: Int,
        month: Int,
        year: Int
    ): Calendar =
        calendar.apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }

    internal fun updateTimeInCalendarInstance(
        calendar: Calendar,
        hourOfDay: Int,
        min: Int,
        sec: Int,
        milliSec: Int
    ): Calendar =
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, sec)
            set(Calendar.MILLISECOND, milliSec)
        }

    internal fun getOptimizedFormattedDayDateTime(milliseconds: Long): String =
        formatDayDateTime(milliseconds = milliseconds).replace(":00", "")

    internal fun getOptimizedFormattedDateTime(milliseconds: Long): String =
        formatDateTime(milliseconds = milliseconds).replace(":00", "")

    internal fun getMonthsBetweenDatesForSeconds(startSeconds: Long, endSeconds: Long): Long {
        val startCal = getNewCalendar(Date(convertSecondsToMilliseconds(seconds = startSeconds)))
        val endCal = getNewCalendar(Date(convertSecondsToMilliseconds(seconds = endSeconds)))

        var monthsBetween = 0
        var dateDiff: Int = endCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH)

        if (dateDiff < 0) {
            val borrow: Int = endCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            dateDiff =
                endCal.get(Calendar.DAY_OF_MONTH) + borrow - startCal.get(Calendar.DAY_OF_MONTH)
            monthsBetween--

            if (dateDiff > 0)
                monthsBetween++
        } else {
            monthsBetween++
        }

        monthsBetween += endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)
        monthsBetween += (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12

        return monthsBetween.toLong()
    }
}