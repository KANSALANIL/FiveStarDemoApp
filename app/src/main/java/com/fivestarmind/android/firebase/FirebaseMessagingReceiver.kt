package com.fivestarmind.android.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.mvp.activity.HomeTabsActivity
import com.fivestarmind.android.mvp.activity.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import java.util.Date


class FirebaseMessagingReceiver : FirebaseMessagingService() {
    var count: Int = 0
    var intent:Intent?=null
    var notification_id:String?=null
    var type:String?=null
    var userSenderId:String?=null
    var notificationID:Int = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        //notificationID = 1 ++

        if (remoteMessage.data.size > 0) {
            Log.d(
                "hanfle_msg",
                "getData: " + remoteMessage.data
            )
            val jsonObject = JSONObject(remoteMessage.data.toMap())
            val title:String=jsonObject.getString("title")
            val message:String=jsonObject.getString("message")
            notification_id = jsonObject.getString("notification_id")
            userSenderId = jsonObject.getString("sender_id")
            type = jsonObject.getString("type")

            if (!AppController.isChatActivityOpen) {
                showNotification(title, message)
            }

            Log.e("hanfle_msg", "" + jsonObject)

        }


        if (remoteMessage.notification != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.e("hanfle_msg", "notification: " + remoteMessage.notification!!.title)
                if (!AppController.isChatActivityOpen) {
                    showNotification(
                        remoteMessage.notification!!.title,
                        remoteMessage.notification!!.body
                    )
                }
                //  showNotification(remoteMessage?.notification?.title.toString())
            }
        }
    }

    override fun handleIntent(intent: Intent?) {

        count = SharedPreferencesHelper.getBadgeCount()
        count = count + 1
        SharedPreferencesHelper.saveBadgeCount(count)
        ShortcutBadger.applyCount(baseContext, count)
     //   Log.e("hanfle", "" + intent?.data.toString())

        super.handleIntent(intent)

    }


    fun showNotification(title: String?,message:String?) {
        // Pass the intent to switch to the MainActivity

        if (message!!.contains("New content has been uploaded for you") || type!!.contains("THREAD_SINGLE_MESSAGE")){
            intent = Intent(this, SplashActivity::class.java).putExtra("title",title).putExtra("message",message).putExtra("notification_id",notification_id).putExtra("type",type).putExtra("sender_id",userSenderId)

        }else {
            intent = Intent(this, HomeTabsActivity::class.java)
        }
        //FLAG_ACTIVITY_MULTIPLE_TASK
            // Assign channel ID
            val channel_id = "111"
            intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)


            val pendingIntent: PendingIntent
            pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or  PendingIntent.FLAG_ONE_SHOT
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT or  PendingIntent.FLAG_ONE_SHOT
                )
            }


        val builder = NotificationCompat.Builder(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_app_logo)
            builder.color = resources.getColor(R.color.black)
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
        }

        builder.setContentTitle(title)
        builder.setContentText(message)
        // builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setAutoCancel(true)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.priority = Notification.PRIORITY_MAX
        builder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val importance = NotificationManager.IMPORTANCE_HIGH
                val NOTIFICATION_CHANNEL_ID = channel_id
                val NOTIFICATION_NAME = "five_star_mind"
                val notificationChannel = NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_NAME, importance
                )
              //  notificationChannel.setShowBadge(true)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.WHITE
                builder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(notificationChannel)

            }
            val m = (Date().getTime() / 1000L % Int.MAX_VALUE).toInt()
            notificationManager.notify(m /* ID of notification */, builder.build())

        }
    }
}