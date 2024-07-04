package com.fivestarmind.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.request.VideosRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.VideoDurationPresenter

class AppVideosService : Service(),
    VideoDurationPresenter.ResponseCallBack {

    private val TAG: String = javaClass.simpleName

    private var videoDurationPresenter: VideoDurationPresenter? = null
    private var databaseHelper = DatabaseHelper(this)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        initPresenter()
        hitApiToSaveVideosDuration()
    }

    private fun initPresenter() {
        videoDurationPresenter = VideoDurationPresenter(this)
    }

    private fun hitApiToSaveVideosDuration() {
        val videoDurationRequestModel = arrayListOf<VideoDurationRequestModel>()

        val myResponse =
            GsonHelper.convertJavaObjectToJsonString(model = databaseHelper.getVideos())

        Log.d(TAG, "get saved videos = $myResponse")

        if (databaseHelper.getVideos().isNotEmpty()) {
            for (i in 0 until databaseHelper.getVideos().size) {
                videoDurationRequestModel.add(i, VideoDurationRequestModel().apply {
                    id = databaseHelper.getVideos()[i].videoId
                    duration = databaseHelper.getVideos()[i].videoDuration.toInt()
                })
            }

            val response =
                GsonHelper.convertJavaObjectToJsonString(model = videoDurationRequestModel)

            Log.d(TAG, "videoDurationRequestModel = $response")

            val videosRequestModel = VideosRequestModel(
            ).apply {
                videos = videoDurationRequestModel
            }

            videoDurationPresenter!!.hitApiToSaveDuration(
                SharedPreferencesHelper.getAuthToken(),
                videosRequestModel = videosRequestModel
            )
        }
    }

    override fun onSaveVideosDurationSuccess(response: CommonResponse) {
        Log.d(TAG, "stopSelf")
        stopSelf()
    }

    override fun onResponseFailure(errorResponse: ErrorResponse) {
        Log.d(TAG, "stopSelf")
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(getString(R.string.app_name), "VideosService")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = Notification.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.desc_service))
                .setAutoCancel(true)

            val notification = builder.build()
            startForeground(1, notification)
        } else {
            val builder = NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.desc_service))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val notification = builder.build()

            startForeground(1, notification)
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = ContextCompat.getColor(this, R.color.green)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind")
        // We don't provide binding, so return null
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "onRebind")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}