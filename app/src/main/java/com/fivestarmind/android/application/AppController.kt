package com.fivestarmind.android.application

import android.app.Application
import android.content.Context
import android.util.Log
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.helper.MyLocaleManager
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.retrofit.ApiClient
import com.fivestarmind.android.socket.SocketManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.branch.referral.Branch
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


/**
 * This is the Application class used in this project.
 */
class AppController : Application() {
    val TAG = "AppController"



    companion object {

        var mInstance: AppController? = null
        var localeManager: MyLocaleManager? = null
        var simpleExoplayer: SimpleExoPlayer? = null
        var audioList: ArrayList<SeeAllDataItem>? =null
        val controllerMediaItems = mutableListOf<MediaItem>()
        var audioUriLink: String? =null
        var audioId: Int? =null
        var duration: String? =null
        var position: Int? =null
        var activityHeaderName: String? =null
        var fromNotification: String? =null
        var audioImge: String? =null
        var isFavourite: Boolean? =null
        var specialContent: String? =null
        var audioStateEnd: String = "false"

        var audioTitle: String? =null
        var activityName:String=""
        var playerNotificationManager: PlayerNotificationManager?=null

        var fromHome = ""

        var mSocketManager:SocketManager? = null
        var isChatActivityOpen = false

    }

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        mInstance = this
        audioList = ArrayList()
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()

        initializeDatabase()
       // mSocketManager = SocketManager()

        // Branch logging for debugging
        Branch.enableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)

    /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "media_player",
                "Media Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }*/
    }

    private fun initializeDatabase() {
        var databaseHelper = DatabaseHelper(this)
    }


    override fun attachBaseContext(base: Context?) {
        localeManager = MyLocaleManager(base)
        super.attachBaseContext(localeManager!!.setLocale(base))
        Log.d(TAG, "attachBaseContext")
    }

   /* override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager!!.setLocale(this)
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.language)
    }*/
}