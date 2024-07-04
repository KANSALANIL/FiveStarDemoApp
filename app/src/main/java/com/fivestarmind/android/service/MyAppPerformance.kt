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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.helper.SharedPreferencesHelper

class MyAppPerformance : Service() {
    var count = 0
    private val TAG: String = javaClass.simpleName

    companion object {
        val START_SERVICE = "start"
        val STOP_SERVICE = "stop"
        val FOREGROUND_SERVICE = "foreground"
        const val TAG = "MyService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val intentAction = intent?.action
        when (intentAction) {
            START_SERVICE -> {

               // Toast.makeText(baseContext, "Service started\"", Toast.LENGTH_SHORT).show()


            }
            STOP_SERVICE -> stopService()
        //    FOREGROUND_SERVICE -> doForegroundThings()
            else -> {
              //  Toast.makeText(baseContext, "Empty action intent", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onStartCommand(intent, flags, startId)


    }



    private fun doForegroundThings() {
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
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d(TAG, "onBind")
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
        Log.d(TAG, "onDestroy___")
    }

    private fun stopService() {
        Toast.makeText(baseContext, "Service stopping", Toast.LENGTH_SHORT)
            .show()

        try {
            stopForeground(true)
            //   isForeGroundService = false
            stopSelf()

        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        Log.d(TAG, "onTaskRemoved__")

        if (AppController.simpleExoplayer!!.isPlaying || !AppController.simpleExoplayer!!.isPlaying) {

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()

            SharedPreferencesHelper.saveAudioId("")
        }

    }
}