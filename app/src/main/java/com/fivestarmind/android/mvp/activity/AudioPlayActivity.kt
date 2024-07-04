package com.fivestarmind.android.mvp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.*
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.GetProductFileResponse
import com.fivestarmind.android.mvp.presenter.VideoPlayerPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_audio_play.audioPlayerView
import kotlinx.android.synthetic.main.activity_audio_play.ivAudio
import kotlinx.android.synthetic.main.activity_audio_play.ivBack
import kotlinx.android.synthetic.main.activity_audio_play.ivBookmark
import kotlinx.android.synthetic.main.activity_audio_play.pbAudio
import kotlinx.android.synthetic.main.activity_audio_play.tvAudioTitleName
import kotlinx.android.synthetic.main.activity_audio_play.tvHeading
import java.util.concurrent.ExecutionException


class AudioPlayActivity : BaseActivity(), VideoPlayerPresenter.ResponseCallBack,
    DialogSesstionExpiredListener {
    private var presenter: VideoPlayerPresenter? = null
    private var audioDuration = "0"
    private var fromNotification: String? = null
    private var thumbPath: String? = null
    private var isFavourite: Boolean? = null
    private var activityHeaderName: String? = null
    private var specialContent: String? = null
    private var ScreenType: String? = null
    private var audioLink: String? = null
    private var audioId: Int? = null
    private var position: Int? = null
    private var audioTitle: String? = null
    private var duration: String? = null
    private var audioThumbPathLink: String? = null
    private var isAudioEnd = false
    private var mPlayer: Player? = null
    private var addFavouriteRequest = AddFavouriteRequestModel()
    private var addFav: Int = 0
    private var mAudioList: ArrayList<String>? = null

    private var mPosition: Int = 0
    val mediaItems = mutableListOf<MediaItem>()
    private var pendingIntent: PendingIntent? = null
    private var favCall: Boolean = false

    var trackImage: Bitmap? = null
    private var databaseHelper = DatabaseHelper(this)
    var dialogSessionExpired: DialogSessionExpired? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_play)

        if (AppController.playerNotificationManager != null) {
            AppController.playerNotificationManager!!.setPlayer(null)
        }

        askNotificationPermission()

    }


    private fun callAudioMethods() {
        registerBroadCast()
        initPresenter()
        getDataFromIntent()
        clickListener()

    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                callAudioMethods()
//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            callAudioMethods()

        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            /*  Toast.makeText(this, "Notifications Permission Granted", Toast.LENGTH_SHORT)
                  .show()*/

            callAudioMethods()
        } else {
//            Toast.makeText(
//                this, "${getString(R.string.app_name)} can't post notifications without Notification permission",
//                Toast.LENGTH_LONG
//            ).show()

            Snackbar.make(
                ivBack,
                String.format(
                    String.format(
                        getString(R.string.txt_error_post_notification),
                        getString(R.string.app_name)
                    )
                ),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(getString(R.string.goto_settings)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivityForResult(
                        settingsIntent,
                        Constants.App.RequestCode.notificationRequestCode
                    )
                }
            }.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.App.RequestCode.notificationRequestCode) {
            callAudioMethods()
        }
    }


    private fun registerBroadCast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("closeAudioActivity")
        )
    }

    /**
     * Here is initialing the presenter
     */
    private fun initPresenter() {
        AppController.activityName = "AudioPlayActivity"
        mAudioList = ArrayList()
        presenter = VideoPlayerPresenter(this)
    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.AUDIO_LINK) && null != intent.getStringExtra(Constants.App.AUDIO_LINK)) {
            audioLink = intent.getStringExtra(Constants.App.AUDIO_LINK)
            audioId = intent.getIntExtra(Constants.App.AUDIO_ID, 0)
            //    duration = intent.getStringExtra(Constants.App.AUDIO_DURATION)
            audioTitle = intent.getStringExtra(Constants.App.AUDIO_TITLE)
            position = intent.getIntExtra(Constants.App.POSITION, 0)
            activityHeaderName = intent.getStringExtra(Constants.App.AUDIO_ACTIVITY_NAME)
            fromNotification = intent.getStringExtra(Constants.App.FROM_NOTIFICATION)
            thumbPath = intent.getStringExtra(Constants.App.AUDIO_THUMPATH)
            isFavourite = intent.getBooleanExtra(Constants.App.IS_FAVROITE, false)
            specialContent = intent.getStringExtra(Constants.App.SPECIAL_CONTENT)
            ScreenType = intent.getStringExtra(Constants.App.SCREEN_TYPE)

            Log.d(TAG, "audioSongsList__" + AppController.audioList!!.size)
           // tvHeading.text = activityHeaderName
            tvHeading.text = activityHeaderName


            if (ScreenType.equals("PlayList")) {
                AppController.fromHome = ""
            }

            if (specialContent != null && specialContent.equals("specialContent")) {
                ivBookmark.visibility = View.GONE

            } else {

                if (AppController.fromHome != null && AppController.fromHome.equals("Home")) {
                    ivBookmark.visibility = View.GONE
                } else {
                    ivBookmark.visibility = View.VISIBLE

                }
                if (isFavourite!!) {
                    addFav = 0
                    ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))

                } else {
                    addFav = 1
                    ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))

                }

            }

            if (thumbPath != null) {

                AppController.audioImge = thumbPath
                pbAudio.visibility = View.VISIBLE

                Picasso.get().load(ApiClient.BASE_URL_MEDIA + thumbPath)
                    .into(ivAudio, object : Callback {
                        override fun onSuccess() {
                            pbAudio.visibility = View.GONE
                        }

                        override fun onError(e: Exception) {
                            pbAudio.visibility = View.GONE
                        }
                    })
            } else {
                ivAudio.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_mp3)
                )
            }

            if (!SharedPreferencesHelper.getAudioId()
                    .isEmpty() && SharedPreferencesHelper.getAudioId().equals(audioId.toString())
            ) {

                initializePlayer()

            } else {
                SharedPreferencesHelper.saveAudioId(audioId.toString())
                AppController.simpleExoplayer!!.currentPosition.toInt() == 0
                if (ScreenType != null) {

                    if (ScreenType.equals("Home")) {

                        AppController.controllerMediaItems.clear()

                        AppController.controllerMediaItems.add(
                            MediaItem.Builder()
                                .setMediaId(audioId.toString())
                                .setUri(Uri.parse(ApiClient.BASE_URL_MEDIA + audioLink))
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(audioTitle)
                                        .setAlbumArtist(ApiClient.BASE_URL_MEDIA + thumbPath)
                                        .setIsBrowsable(isFavourite).setArtist(activityHeaderName)
                                        .build()
                                )
                                .build()
                        )

                        AppController.simpleExoplayer!!.setMediaItems(AppController.controllerMediaItems)


                    } else if (ScreenType.equals("PlayList")) {
                        AppController.fromHome = ""

                        if (AppController.audioList != null && AppController.audioList!!.size > 0) {
                            AppController.controllerMediaItems.clear()
                            for (i in 0 until AppController.audioList!!.size) {
                                AppController.controllerMediaItems.add(
                                    MediaItem.Builder()
                                        .setMediaId(AppController.audioList!!.get(i).id!!.toString())
                                        .setUri(
                                            Uri.parse(
                                                ApiClient.BASE_URL_MEDIA + AppController.audioList!!.get(
                                                    i
                                                ).tempPath
                                            )
                                        ).setMediaMetadata(
                                            MediaMetadata.Builder()
                                                .setTitle(AppController.audioList!!.get(i).title!!)
                                                .setAlbumArtist(
                                                    ApiClient.BASE_URL_MEDIA + AppController.audioList!!.get(
                                                        i
                                                    ).thumbpath
                                                )
                                                .setIsBrowsable(AppController.audioList!!.get(i).isFavourite)
                                                .setArtist(
                                                    AppController.audioList!!.get(
                                                        i
                                                    ).product?.name
                                                )
                                                .build()
                                        )
                                        .build()
                                )
                                AppController.simpleExoplayer!!.setMediaItems(AppController.controllerMediaItems)

                            }

                        }
                    } else if (ScreenType.equals("BaseActivity")) {
                        AppController.playerNotificationManager!!.setPlayer(null)

                        favCall =
                            !(AppController.fromHome != null && AppController.fromHome.equals("Home"))


                    } else if (ScreenType.equals("notification")) {
                        AppController.fromHome = ""

                        Log.d(TAG, "notification_playerNotificationManager")

                    }
                }

                initializePlayer()

            }

        } else {

            initializePlayer()

            favCall = !(AppController.fromHome != null && AppController.fromHome.equals("Home"))


            //favCall = true


        }
    }


    /**
     * PlayList Song Change Listner
     */
    private fun playerListner() {
        AppController.simpleExoplayer!!.addListener(object : Player.Listener {
            override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, reason: Int) {
                Log.e("playerListner: ", "onMediaItemTransition")
                if (mediaItem != null) {
                    // Update the song title TextView with the new media item's title

                    if (AppController.simpleExoplayer?.isPlaying == true && SharedPreferencesHelper.getAudioId()
                            .equals(audioId.toString())
                    ) {
                        audioId = mediaItem.mediaId.toInt()
                        val songTitle = mediaItem.mediaMetadata.title
                        AppController.audioTitle = songTitle.toString()
                    } else {
                        audioId = mediaItem.mediaId.toInt()


                        val songTitle = mediaItem.mediaMetadata.title
                        AppController.audioTitle = songTitle.toString()
                        AppController.audioImge = mediaItem.mediaMetadata.albumArtist.toString()

                        Log.d(
                            TAG,
                            "mediaItem_Image: " + mediaItem.mediaMetadata.albumArtist.toString()
                        )
                        tvAudioTitleName.text = songTitle

                        //added image in view
                        pbAudio.visibility = View.VISIBLE
                        Picasso.get()
                            .load(mediaItem.mediaMetadata.albumArtist.toString())
                            .placeholder(R.drawable.ic_mp3)
                            .into(ivAudio, object : Callback {
                                override fun onSuccess() {
                                    pbAudio.visibility = View.GONE
                                }

                                override fun onError(e: Exception) {
                                    pbAudio.visibility = View.GONE
                                }
                            })


                        if (mediaItem.mediaMetadata.isBrowsable != null) {
                            if (mediaItem.mediaMetadata.isBrowsable!!) {
                                isFavourite = true
                                addFav = 0
                                AppController.isFavourite = false
                                ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))
                            } else {
                                isFavourite = false
                                addFav = 1
                                AppController.isFavourite = true

                                ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))
                            }
                        } else {
                            isFavourite = true
                            addFav = 1
                            ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))

                        }

                    }


                }
            }

        })

    }


    /**
     * Here is initializing video player
     */
    @SuppressLint("ResourceType")
    private fun initializePlayer() {

        if (null != baseContext && !isFinishing) {
            runOnUiThread {

                audioPlayerView.player = AppController.simpleExoplayer
                audioPlayerView.showController()

                AppController.audioTitle = audioTitle
                AppController.audioImge = thumbPath
                tvAudioTitleName.text = AppController.audioTitle

                if (position != null) {
                    if (position != 0) {

                        AppController.simpleExoplayer!!.seekTo(
                            position!!,
                            AppController.simpleExoplayer!!.currentPosition
                        )

                    }
                }
                AppController.simpleExoplayer!!.playWhenReady = true
                AppController.simpleExoplayer!!.prepare()
                AppController.simpleExoplayer!!.play()
                // PlayList Song Change Listner

                playerListner()

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = getSystemService(NotificationManager::class.java)
                val channelId = "media_channel_id"
                val channelName = "Media Playback"
                val importance =
                    NotificationManager.IMPORTANCE_LOW // Adjust the importance as needed
                val notificationChannel = NotificationChannel(channelId, channelName, importance)
                notificationChannel.setShowBadge(false)

                notificationManager.createNotificationChannel(notificationChannel)
            }




            AppController.playerNotificationManager = PlayerNotificationManager.Builder(
                applicationContext,
                1,
                "media_channel_id", // Use the same channel ID as you created earlier
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        // Define an intent that should be launched when the notification is clicked
                        //  val intent = Intent(applicationContext, AudioPlayActivity::class.java)
                        //   return PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                        return createCurrentIntent(player)
                    }

                    override fun getCurrentContentText(player: Player): CharSequence {
                        // Return the content text for the notification

                        if (player.mediaMetadata.title != null) {
                            return player.mediaMetadata.title!!
                        } else {
                            return ""
                        }
                    }

                    override fun getCurrentContentTitle(player: Player): CharSequence {
                        if (player.currentMediaItem != null) {
                            AppController.audioTitle = player.mediaMetadata.title.toString()
                            AppController.audioImge = player.mediaMetadata.albumArtist.toString()

                            if (player.currentMediaItem!!.mediaId != null) {
                                SharedPreferencesHelper.saveAudioId(player.currentMediaItem!!.mediaId)
                            }


                            Glide.with(baseContext)
                                .load(player.mediaMetadata.albumArtist!!.toString())
                                .placeholder(R.drawable.ic_mp3).diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                )
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        ivAudio.setImageResource(R.drawable.ic_mp3)
                                        pbAudio.visibility = View.GONE
                                        return false

                                    }


                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        pbAudio.visibility = View.GONE
                                        return false
                                    }

                                }).diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                )
                                .into(ivAudio)


                            Log.e(
                                "MEdiametaData",
                                "mediaId==" + AppController.simpleExoplayer!!.currentMediaItem!!.mediaId
                            )

                            audioId =
                                AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt()
                            if (favCall) {
                                if (audioId != 0) {
                                    getBookmark(audioId!!)
                                } else {
                                    ivBookmark.visibility = View.GONE
                                }
                            }

                            tvHeading.text = player.mediaMetadata.artist!!
                            tvAudioTitleName.text = player.mediaMetadata.title!!
                            audioId = player.currentMediaItem!!.mediaId.toInt()


                            return player.mediaMetadata.artist.toString()

                        } else {

                            return ""

                        }

                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {

                        var bitmap: Bitmap? = null

                        if (!player.mediaMetadata.albumArtist.toString().contains("null")) {
                            Log.d(
                                TAG,
                                "Bitmap>>: " + player.mediaMetadata.albumArtist.toString()
                            )

                            Glide.with(baseContext).asBitmap()
                                .load(player.mediaMetadata.albumArtist.toString())
                                .into(object : CustomTarget<Bitmap?>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                                    ) {
                                        callback.onBitmap(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        Log.d(
                                            TAG,
                                            "onLoadCleared: "
                                        )

                                        callback.onBitmap(
                                            BitmapFactory.decodeResource(
                                                applicationContext.resources,
                                                R.drawable.ic_mp3
                                            )
                                        )
                                    }
                                })


                        }else {
                            bitmap = BitmapFactory.decodeResource(
                                applicationContext.resources,
                                R.drawable.ic_mp3
                            )


                        }
                        Log.d(TAG, "Bitmap: " + bitmap)
                        return bitmap

                    }
                }

            ).build()

            AppController.playerNotificationManager!!.setUseStopAction(false) // Add a stop action

            AppController.playerNotificationManager!!.setUseChronometer(true) // Use elapsed time instead of chronometer

            AppController.playerNotificationManager!!.setUseFastForwardAction(false) // Set custom fast-forward time

            AppController.playerNotificationManager!!.setUseRewindAction(false) // Set custom rewind time

            //   AppController.playerNotificationManager!!.setSmallIcon(R.drawable.ic_app_logo)

            AppController.playerNotificationManager!!.setSmallIcon(R.drawable.ic_app_logo)

            AppController.playerNotificationManager!!.setUsePlayPauseActions(true)

            // playerNotificationManager.setUseStopAction(true)

            AppController.playerNotificationManager!!.setPriority(NotificationCompat.PRIORITY_LOW)


            // Set the media session
            AppController.playerNotificationManager!!.setPlayer(AppController.simpleExoplayer)


        }
    }

    fun createCurrentIntent(player: Player?): PendingIntent? {

        mPlayer = player

        Log.e("isplaying__", "" + mPlayer?.isPlaying)

        //   val intent = Intent(applicationContext, AudioPlayActivity::class.java)

        //  val intent = Intent(applicationContext, HomeActivity::class.java).putExtra(
        val intent = Intent(applicationContext, HomeTabsActivity::class.java).putExtra(
            "fromNotification",
            "audioNotification"
        )


        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                baseContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            pendingIntent = PendingIntent.getActivity(
                baseContext,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        }

        return pendingIntent


    }


    /**
     * Click event for views
     */
    private fun clickListener() {
        ivBack.setOnClickListener(clickListener)
        ivBookmark.setOnClickListener(clickListener)
        AppController.simpleExoplayer?.addListener(listener)

    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> onBackPressed()
                R.id.ivBookmark -> callAddOrRemoveFavouriteApi()
            }
    }

    private var listener: Player.Listener = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val isActivityInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

            if (isActivityInForeground) {
                Log.d(TAG, "isActivityInForeground")
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.d(TAG, "audio is playing")
                    pbAudio.visibility = View.GONE
                }
                if (playWhenReady && playbackState == Player.STATE_BUFFERING) {
                    Log.d(TAG, "audio is playing")
                    pbAudio.visibility = View.VISIBLE

                }
                //                else if (playWhenReady) {
                //                    // might be idle (plays after prepare()),
                //                    // buffering (plays when data available)
                //                    Log.d(TAG, "video is loading ")
                //                    // or ended (plays when seek away from end)
                //                }
                else if (playbackState == Player.STATE_ENDED) {
                    Log.d(TAG, "audio is ended ")
                    isAudioEnd = true
                    pbAudio.visibility = View.GONE

                    getAudioDuration()

                } else {
                    Log.d(TAG, "audio is paused ")
                    pbAudio.visibility = View.GONE
                    getAudioDuration()

                }
            }
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            val isActivityInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (isActivityInForeground) {
                Log.d(TAG, "isActivityInForeground")
                Log.d(TAG, "onTracksChanged= " + tracks)
                getAudioDuration()
            }
        }


    }

    override fun onPause() {
        super.onPause()
        if (dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
    }


    /**
     * Here is getting video duration
     */
    private fun getAudioDuration() {

        if (audioId == 0) {

        } else {

            val duration: Long = AppController.simpleExoplayer?.currentPosition?.toLong()!!
            Log.d(TAG, "audio Duration = " + duration)

            val videoDuration = AppHelper.convertMillisecondsToSeconds(milliseconds = duration)
            this.audioDuration = videoDuration.toString()
            Log.d(TAG, "audio Duration = " + videoDuration)
            Log.d(
                TAG,
                "audio Current Position = " + AppController.simpleExoplayer!!.currentPosition
            )
            val videoPersentage =
                (AppController.simpleExoplayer!!.currentPosition * 100) / AppController.simpleExoplayer!!.duration
            Log.d(TAG, "audio total persentage = " + videoPersentage)

            //    callToSaveVideoDuration()

            callToHitApiForAudioViews()

        }

    }


    /**
     * Here connecting to server and uses view video API
     */
    private fun callToHitApiForAudioViews() {
        if (audioId != null) {
            val videoDurationRequestModel = VideoDurationRequestModel().apply {
                id = audioId!!
                duration = audioDuration.toInt()
            }

            if (isInternetConnected(shouldShowToast = true)) {
                presenter!!.hitApiForVideoView(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    videoId = audioId!!,
                    videoDurationRequestModel = videoDurationRequestModel
                )


            } else {
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }
    }

    override fun onViewsSubmittedSuccess(response: CommonResponse) {
        if (this@AudioPlayActivity.baseContext != null) {
            runOnUiThread {
                if (this@AudioPlayActivity.baseContext != null) {
                    // afterVideoComplete()
                }
            }
        }
    }

    override fun onAddFavResponseSuccess(response: CommonResponse) {
        if (this@AudioPlayActivity.baseContext != null) {
            runOnUiThread {
                if (this@AudioPlayActivity.baseContext != null) {
                    showToast(response.message)
                }
            }
        }
    }

    override fun onGetFavResponseSuccess(getProductFileResponse: GetProductFileResponse) {
        if (this@AudioPlayActivity.baseContext != null) {
            runOnUiThread {
                if (this@AudioPlayActivity.baseContext != null) {

                    if (getProductFileResponse.data != null) {
                        favCall = false
                        ivBookmark.visibility = View.VISIBLE
                        isFavourite = getProductFileResponse.data.isFavourite
                        if (isFavourite!!) {

                            ivBookmark.setImageResource(R.drawable.ic_select_bookmark)

                        } else {
                            ivBookmark.setImageResource(R.drawable.ic_unselected_book_mark)
                        }
                    } else {
                        ivBookmark.visibility = View.GONE

                    }


                }
            }
        }
    }

    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@AudioPlayActivity.baseContext != null) {
            runOnUiThread {
                if (this@AudioPlayActivity.baseContext != null) {
                    if (errorResponse.message.equals("Unauthorized")) {

                        dialogSessionExpired = DialogSessionExpired(
                            listener = this,
                            message = getString(R.string.session_expired),
                            context = this,

                            )

                        try {
                            if (dialogSessionExpired!!.isShowing) {
                                dialogSessionExpired!!.dismiss()
                            } else {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }


                    } else {

                        showToast(errorResponse.message)

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppController.activityName = ""
        Log.d(TAG, " onDestroy")

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)

        if (dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }

    }

    override fun onBackPressed() {
        Log.d(TAG, " onBackPressed")
        Log.d(TAG, "playbackState:  " + AppController.simpleExoplayer!!.playbackState)

        if (AppController.simpleExoplayer!!.playbackState == 2) {
            return
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)


        Log.d(TAG, " onBackPressed")
        Log.d(TAG, " onBackPressed_position: " + position)
        Log.d(
            TAG,
            " onBackPressed_contentPosition: " + AppController.simpleExoplayer!!.currentWindowIndex
        )
        AppController.activityName = ""


        getAudioDuration()


        val intent = Intent()
        intent.putExtra("audioDuration", audioDuration.toInt())
        intent.putExtra("audioPosition", position)
        setResult(RESULT_OK, intent)
        finish()

    }


    /**
     * here call add or remove favourite api
     */
    private fun callAddOrRemoveFavouriteApi() {
        if (isFavourite!!) {
            isFavourite = false
            addFav = 0
            ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))
            //   AppController.simpleExoplayer!!.currentMediaItem!!.mediaMetadata.buildUpon().setIsBrowsable(false)
        } else {
            isFavourite = true

            addFav = 1
            ivBookmark.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))

            //   AppController.simpleExoplayer!!.currentMediaItem!!.mediaMetadata.buildUpon().setIsBrowsable(true)

        }

        addFavouriteRequest.apply {
            product_video_id = audioId
            action = addFav
        }

        if (isInternetConnected(shouldShowToast = true)) {
            // notifyDataSetChanged()
            presenter!!.hitApiToAddFavourite(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                addFavouriteRequest = addFavouriteRequest
            )
        }


    }

    fun getBookmark(audioId: Int) {
        if (isInternetConnected(shouldShowToast = true)) {
            // notifyDataSetChanged()
            presenter!!.hitApiToGetFavourite(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                audioId = audioId
            )
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            //  val message = intent.getStringExtra("message")
            // Log.d("receiver", "Got message: $message")
            AppController.playerNotificationManager!!.setPlayer(null)
            AppController.simpleExoplayer!!.duration == 0.toLong()
            AppController.simpleExoplayer!!.playWhenReady = true

            onBackPressed()
        }

    }

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }

    }

    fun clearLocalDB() {
        onSessionExpiredClearLocalDB(databaseHelper)
        startActivity(
            Intent(
                this@AudioPlayActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }
}