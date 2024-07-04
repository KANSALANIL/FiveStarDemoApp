package com.fivestarmind.android.helper

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.widget.EditText
import com.fivestarmind.android.R
import com.fivestarmind.android.constant.DateTimeConst
import com.fivestarmind.android.interfaces.Constants
import java.text.SimpleDateFormat
import java.util.*


object AppHelper {

    private var isNetworkConnected: Boolean = false
    private var lastClickedMilliseconds: Long = 0L

    /**
     * It gives network connected status
     *
     * @return network connected status
     */
    internal fun isNetworkConnected(): Boolean {
        return isNetworkConnected
    }

    internal fun updateNetworkConnectedStatus(status: Boolean) {
        isNetworkConnected = status
    }

    private val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat(DateTimeConst.PATTERN.DAY_DATE_TIME, Locale.US)

    /**
     * It is called to checks and returns the status whether user has clicked any view once or not
     *
     * @return user clicked status
     */
    internal fun shouldProceedClick(): Boolean {
        var status: Boolean = false

        val timeDiff = System.currentTimeMillis() - lastClickedMilliseconds

        if (timeDiff > DateTimeConst.DURATION_IN_MILLISECONDS.CLICK_DELAY || timeDiff < DateTimeConst.DURATION_IN_MILLISECONDS.ZERO) {
            lastClickedMilliseconds = System.currentTimeMillis()
            status = true
        }

        return status
    }

    /**
     * It is called to remove emoji
     */
    internal var emojiFilter: InputFilter = object : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            for (i in start until end) {
                //                LogHelper.debug(tag = TAG, msg = "type: $type")

                when (Character.getType(source[i])) {
                    Character.SURROGATE.toInt(),
                    Character.OTHER_SYMBOL.toInt(),
                    Character.ENCLOSING_MARK.toInt() -> return ""
                }
            }

            return null
        }
    }

    /**
     * It gives user loggedIn status
     *
     * @return user loggedIn status
     */
    internal fun isUserLoggedIn(): Boolean =
        SharedPreferencesHelper.getUserId().isNotBlank()
        /*if (!SharedPreferencesHelper.getUserId().equals(" ") && SharedPreferencesHelper.getUserId().length>0) {
          return true
        }else{
            return false
        }*/


    /**
     * To check email validation
     *
     * @param email email id which is checked for validation
     *
     * @return email validation defaultStatus as boolean
     */
    internal fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
//        Pattern.compile(AppConst.REGEX.EMAIL).matcher(email).matches()

    /**
     * It gives max image size video original
     *
     * @return max image size video original
     */
    internal fun getMaxImageSizeVideoOriginal(): Int =
        Constants.App.DefaultValue.MAX_IMAGE_SIZE_VIDEO_ORIGINAL

    /**
     * It gives image compression quality video original
     *
     * @return image compression quality video original
     */
    internal fun getImageCompressionQualityVideoOriginal(): Int =
        Constants.App.DefaultValue.IMAGE_COMPRESSION_QUALITY_VIDEO_ORIGINAL

    internal fun formatVideoLeft(context: Context, video: Int?): String {
        return when {
            video == Constants.App.Number.ZERO || video == null -> String.format(
                context.getString(
                    R.string.format_video,
                    Constants.App.Number.ZERO.toString()
                )
            )
            video == Constants.App.Number.ONE -> String.format(
                context.getString(
                    R.string.format_video,
                    Constants.App.Number.ONE.toString()
                )
            )
            video > Constants.App.Number.ONE -> String.format(
                context.getString(
                    R.string.format_videos,
                    video.toString()
                )
            )
            else -> String.format(context.getString(R.string.format_videos, video.toString()))
        }
    }

    internal fun formatVideoCount(context: Context, video: Int?): String {
        return when {
            video == Constants.App.Number.ZERO || video == null -> String.format(
                context.getString(
                    R.string.format_video,
                    Constants.App.Number.ZERO.toString()
                )
            )
            video == Constants.App.Number.ONE -> String.format(
                context.getString(
                    R.string.format_video,
                    Constants.App.Number.ONE.toString()
                )
            )
            video > Constants.App.Number.ONE -> String.format(
                context.getString(
                    R.string.format_videos,
                    video.toString()
                )
            )
            else -> String.format(context.getString(R.string.format_videos, video.toString()))
        }
    }

    internal fun formatPdfCount(context: Context, pdfCount: Int?): String {
        return String.format(
                context.getString(
                    R.string.format_pdf,
                        pdfCount.toString()
                )
            )
    }

    internal fun convertMillisecondsToSeconds(milliseconds: Long): Long =
        milliseconds / DateTimeConst.DURATION_IN_MILLISECONDS.ONE_SECOND

    internal fun convertSecondsToMilliseconds(seconds: Long): Long =
        seconds * DateTimeConst.DURATION_IN_MILLISECONDS.ONE_SECOND

    private fun getCurrentTimestampInMilliseconds(): Long = System.currentTimeMillis()

    internal fun getCurrentTimestampInSeconds(): Long =
        getCurrentTimestampInMilliseconds() / Constants.App.Number.THOUSAND

    internal fun getTimeStampOfComingOneMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val sixMonthsTime = calendar.time
        return getTimestampForDate(sixMonthsTime)
    }

    internal fun getTimeStampOfComingThreeMonths(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 90)
        val sixMonthsTime = calendar.time
        return getTimestampForDate(sixMonthsTime)
    }

    internal fun getTimeStampOfComingSixMonths(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 180)
        val sixMonthsTime = calendar.time
        return getTimestampForDate(sixMonthsTime)
    }

    internal fun getTimeStampOfComingTwelveMonths(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 365)
        val sixMonthsTime = calendar.time
        return getTimestampForDate(sixMonthsTime)
    }

    private fun getTimestampForDateWithUpdatedTime(
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

    private fun getTimestampForDate(date: Date): Long =
        getTimestampForDateWithUpdatedTime(
            date = date,
            hour = DateTimeConst.DURATION_IN_HOURS.ZERO_HOUR,
            min = DateTimeConst.DURATION_IN_MINUTES.ZERO_MINUTE,
            sec = DateTimeConst.DURATION_IN_SECONDS.ZERO_SECOND
        )

    internal fun formatCalendarDate(milliseconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.CALENDAR_DATE)
        }.format(Date(milliseconds))
    }

    internal fun formatCalendarDateForSeconds(seconds: Long): String {
        return simpleDateFormat.apply {
            applyPattern(DateTimeConst.PATTERN.CALENDAR_DATE)
                timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(convertSecondsToMilliseconds(seconds = seconds)))
    }

    /**
     * Here convert all the characters to uppercase
     */
    internal class UppercaseTextWatcher(private val editText: EditText) : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            if (isUpdating) return

            // Set flag to indicate that we are updating the Editable.
            isUpdating = true

            editText.apply {
                setText(s.toString().uppercase())
                setSelection(length())
            }

            isUpdating = false
        }
    }

    internal fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    /**
     * set time format in UI
     * @param duration
     * @return
     */
    internal fun getTimeFromat(duration: String): String? {
        val time: String
        val mTime = duration.split(":").toTypedArray()
        time = if (mTime[0] == "00") {
            mTime[1] + ":" + mTime[2]
        } else {
            duration
        }
        return time
    }


}