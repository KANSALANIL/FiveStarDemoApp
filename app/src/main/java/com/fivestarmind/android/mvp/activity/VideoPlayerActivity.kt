package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.view.isVisible
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.GetProductFileResponse
import com.fivestarmind.android.mvp.model.response.VideoDurationModel
import com.fivestarmind.android.mvp.presenter.VideoPlayerPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_video_player.ivBookMark
import kotlinx.android.synthetic.main.activity_video_player.ivVideoBack
import kotlinx.android.synthetic.main.activity_video_player.playerView
import kotlinx.android.synthetic.main.activity_video_player.progressBarVideoPlayer
import kotlinx.android.synthetic.main.activity_video_player.toolbar
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.ivChat
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import kotlinx.android.synthetic.main.layout_video_custom_player.ivChangeOritation
import kotlinx.android.synthetic.main.layout_video_custom_player.videoController


class VideoPlayerActivity : BaseActivity(), VideoPlayerPresenter.ResponseCallBack,
    DialogSesstionExpiredListener,
    Player.Listener {

    override var TAG = VideoPlayerActivity::class.java.simpleName
    private var simpleExoplayer: SimpleExoPlayer? = null
    private var activtyName: String? = null
    private var specialContent: String? = null
    private var videoLink: String = ""
    private var videoThumbPathLink: String? = null
    private var videoId: Int? = null
    private var videoPosition: Int? = null
    private var videoDuration = "0"
    private var isBackPressed = false
    private var isVideoEnd = false
    private var presenter: VideoPlayerPresenter? = null
    private var databaseHelper = DatabaseHelper(this)
    var videoPersentage: Long = 0
    private var isFavourite: Boolean? = null
    private var addFav: Int = 0
    private var addFavouriteRequest = AddFavouriteRequestModel()
    var dialogSessionExpired:DialogSessionExpired? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        tvTitle.isAllCaps = false

        if (AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.stop()
            if (AppController.playerNotificationManager != null) {
                AppController.playerNotificationManager!!.setPlayer(null)
            }

        }

        getDataFromIntent()
        setToolbarTitle()

        initializePlayer()
        initPresenter()
        setListeners()


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation

        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                // playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                toolbar.visibility = View.GONE
                ivVideoBack.visibility = View.VISIBLE
                //set bookmark in view
                showAndHideBookmark(ivBookMark)
                playerView.controllerShowTimeoutMs = 2000
                playerView.controllerHideOnTouch = true
                setMarginLeft(videoController, 40)
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                //   playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                toolbar.visibility = View.VISIBLE
                ivVideoBack.visibility = View.GONE
                ivBookMark.visibility = View.GONE


                setMarginLeft(videoController, 60)
                playerView.controllerShowTimeoutMs = 0
                playerView.controllerHideOnTouch = false

            }
        }
    }

    fun setMarginLeft(v: View, bottomMargin: Int) {
        val params = v.layoutParams as MarginLayoutParams
        params.setMargins(
            params.leftMargin, params.topMargin,
            params.rightMargin, bottomMargin
        )
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        //    tvTitle.text = getString(R.string.mental_minutes)
        if (!progressBarVideoPlayer.isVisible) {
            progressBarVideoPlayer.visibility = View.GONE
        }

        /* Picasso.get()
             .load(ApiClient.BASE_URL_MEDIA + videoThumbPathLink).placeholder(R.drawable.ic_video)
             .into(ivVideoThumnail, object : Callback {
                 override fun onSuccess() {
                     progressBarVideoPlayer.visibility = View.GONE
                 }

                 override fun onError(e: Exception?) {
                     progressBarVideoPlayer.visibility = View.GONE
                 }
             })*/


    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.VIDEO_LINK) && null != intent.getStringExtra(Constants.App.VIDEO_LINK)) {
            videoLink = intent.getStringExtra(Constants.App.VIDEO_LINK)!!
            videoThumbPathLink = intent.getStringExtra(Constants.App.VIDEO_THUMB_PATH_LINK)
            videoId = intent.getIntExtra(Constants.App.VIDEO_ID, Constants.App.Number.ZERO)
            videoPosition = intent.getIntExtra(Constants.App.POSITION, Constants.App.Number.ZERO)
            activtyName = intent.getStringExtra(Constants.App.VIDEO_ACTIVITY_NAME)
            specialContent = intent.getStringExtra(Constants.App.SPECIAL_CONTENT)
            isFavourite = intent.getBooleanExtra(Constants.App.IS_FAVROITE, false)

            // isMastered = intent.getIntExtra(Constants.App.IS_MASTERED, Constants.App.Number.ZERO)

            tvTitle.text = activtyName

            showAndHideBookmark(ivChat)

        }
    }


    fun showAndHideBookmark(imageView: ImageView){
        if (specialContent != null && specialContent.equals("specialContent")) {
            imageView.visibility = View.GONE

        } else {

            if (AppController.fromHome != null && AppController.fromHome.equals("Home")) {
                imageView.visibility = View.GONE
            } else {
                imageView.visibility = View.VISIBLE

            }
            if (isFavourite!!) {

                imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))

            } else {

                imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))

            }

        }

    }



    /**
     * Here is initializing video player
     */
    private fun initializePlayer() {
        if (null != baseContext && !isFinishing) {
            runOnUiThread {
                playerView.visibility = View.VISIBLE

                simpleExoplayer = SimpleExoPlayer.Builder(this).build()


                val mediaItem: MediaItem = MediaItem.fromUri(ApiClient.BASE_URL_MEDIA + videoLink)
                //val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"))
                simpleExoplayer!!.setMediaItem(mediaItem)
                playerView.player = simpleExoplayer
                playerView.useController = true
                playerView.showController()
                playerView.controllerShowTimeoutMs = 0
                playerView.controllerHideOnTouch = false
                setMarginLeft(videoController, 60)

                simpleExoplayer!!.prepare()
                simpleExoplayer!!.play()

                //play()
            }
        }
    }

    /**
     * Here is initialing the presenter
     */
    private fun initPresenter() {
        presenter = VideoPlayerPresenter(this)
    }

    /**
     * Here is setting listeners for callback
     */
    private fun setListeners() {
        simpleExoplayer?.addListener(listener)
        /* ivPlayVideo.setOnClickListener {
             playVideo()
         }*/
        ivChangeOritation.setOnClickListener {

            when (resources.configuration.orientation) {

                Configuration.ORIENTATION_LANDSCAPE -> {
                  //  ivVideoBack.visibility = View.VISIBLE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                }

                Configuration.ORIENTATION_PORTRAIT -> {
                   // ivVideoBack.visibility = View.GONE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                }
            }

        }
        ivBackMenu.setOnClickListener {
            onBackPressed()
        }

        ivVideoBack.setOnClickListener {
            onBackPressed()
        }


        ivBookMark.setOnClickListener {
            callAddOrRemoveFavouriteApi()
        }

        ivChat.setOnClickListener {
            callAddOrRemoveFavouriteApi()
        }


    }

    private fun playVideo() {

        //simpleExoplayer!!.prepare()

        if (simpleExoplayer!!.isPlaying) {

            pause()

        } else {

            play()
        }
    }

    fun play() {

        if (isVideoEnd) {
            isVideoEnd = false
            simpleExoplayer!!.seekTo(0)
        }

        simpleExoplayer?.playWhenReady = true

        // ivPlayVideo.setImageDrawable(getDrawable(R.drawable.ic_play))
    }

    fun pause() {
        getVideoDuration()

        Log.d(TAG, "simpleExoplayer!!.bufferedPosition: " + simpleExoplayer!!.bufferedPosition)
        simpleExoplayer?.playWhenReady = false

        //ivPlayVideo.setImageDrawable(getDrawable(R.drawable.ic_pause))
    }


    private var listener: Player.Listener = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            if (playWhenReady && playbackState == Player.STATE_READY) {
                Log.d(TAG, "video is playing")

                //  playVideo()
            }
            //                else if (playWhenReady) {
            //                    // might be idle (plays after prepare()),
            //                    // buffering (plays when data available)
            //                    Log.d(TAG, "video is loading ")
            //                    // or ended (plays when seek away from end)
            //                }
            else if (playbackState == Player.STATE_ENDED) {
                isVideoEnd = true
                Log.d(TAG, "video is ended ")
                getVideoDuration()
                //     ivPlayVideo.setImageDrawable(getDrawable(R.drawable.ic_pause))

            } else {
                Log.d(TAG, "video paused ")
                getVideoDuration()
                //  pause()
            }
        }

        /*override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().message);
            //Restart the playback
           // initializePlayer()
        }*/
    }

    /**
     * Here is getting video duration
     */
    private fun getVideoDuration() {
        if (videoId == 0) {

        } else {

            val duration: Long = simpleExoplayer?.currentPosition?.toLong()!!
            val videoDuration = AppHelper.convertMillisecondsToSeconds(milliseconds = duration)
            this.videoDuration = videoDuration.toString()
            Log.d(TAG, "video Duration = " + videoDuration)
            Log.d(TAG, "video Current Position = " + simpleExoplayer!!.currentPosition)
            videoPersentage =
                (simpleExoplayer!!.currentPosition * 100) / simpleExoplayer!!.duration
            Log.d(TAG, "video total persentage = ")

            //    callToSaveVideoDuration()

            callToHitApiForVideoViews()
        }
    }

    /**
     * Here is storing video duration in database
     */
    private fun callToSaveVideoDuration() {
        Log.d(TAG, "callToSaveVideoDuration - video Id - $videoId")
        databaseHelper.addVideos(
            VideoDurationModel(
                userId = SharedPreferencesHelper.getUserId().toInt(),
                videoId = videoId!!.toInt(),
                videoDuration = this.videoDuration,
                videoSyncStatus = "true"
            )
        )

        val myResponse =
            GsonHelper.convertJavaObjectToJsonString(model = databaseHelper.getVideos())

        Log.d(TAG, "get saved videos = $myResponse")
    }

    /**
     * Here connecting to server and uses view video API
     */
    private fun callToHitApiForVideoViews() {
        val videoDurationRequestModel = VideoDurationRequestModel().apply {
            id = videoId!!
            duration = videoDuration.toInt()
        }

        if (isInternetConnected(shouldShowToast = true)) {
            presenter!!.hitApiForVideoView(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                videoId = videoId!!,
                videoDurationRequestModel = videoDurationRequestModel
            )
        } else {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

    /**
     * When successful response or data retrieved from view video api
     *
     * @param response success response
     */
    override fun onViewsSubmittedSuccess(response: CommonResponse) {
        if (this@VideoPlayerActivity.baseContext != null) {
            runOnUiThread {
                if (this@VideoPlayerActivity.baseContext != null) {
                    // afterVideoComplete()
                }
            }
        }
    }

    override fun onAddFavResponseSuccess(response: CommonResponse) {
        if (this@VideoPlayerActivity.baseContext != null) {
            runOnUiThread {
                if (this@VideoPlayerActivity.baseContext != null) {
                    showToast(response.message)
                }
            }
        }

    }

    override fun onGetFavResponseSuccess(getProductFileResponse: GetProductFileResponse) {
    }

    /**
     * When error occurred in getting successful response of
     *
     * @param errorResponse for Error message
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@VideoPlayerActivity.baseContext != null) {
            runOnUiThread {
                if (this@VideoPlayerActivity.baseContext != null) {
                    //responseFailure(errorResponse)
                    Log.d(TAG, "responseFailure_errorResponse" + errorResponse)


                    if (errorResponse.message.equals("Unauthorized")) {

                        dialogSessionExpired = DialogSessionExpired(
                            listener = this,
                            message = getString(R.string.session_expired),
                            context = this,

                            )

                        try {

                            if(dialogSessionExpired!!.isShowing){
                                dialogSessionExpired!!.dismiss()

                            }else {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }


                    } else {

                        //    responseFailure(errorResponse)
                        showToast(errorResponse.message)


                    }

                }
            }
        }
    }

    /**
     * It is called when video is ended/back
     */
    /* private fun afterVideoComplete() {
        val intent: Intent = Intent()
            .apply {
                putExtra(Constants.App.VIDEO_ID, videoId)
                putExtra(Constants.App.IS_MASTERED, isMastered)
            }

        if (!isBackPressed) setResult(Activity.RESULT_OK, intent)
        else setResult(Activity.RESULT_CANCELED, intent)

        finish()
    }
*/
    override fun onBackPressed() {
        Log.d(TAG, " onBackPressed")
        AppController.fromHome =""
        isBackPressed = true
        // getVideoDuration()
        getVideoDuration()
        val resultIntent = Intent()
        resultIntent.putExtra("videoDuration", videoDuration.toInt())
        resultIntent.putExtra("videoPosition", videoPosition)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogSessionExpired!=null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }

        getVideoDuration()
        simpleExoplayer?.removeListener(listener)
        simpleExoplayer?.release()


    }

    override fun onPause() {
        super.onPause()
        if (dialogSessionExpired!=null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
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
        startActivity(Intent(this@VideoPlayerActivity, SignInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK   or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()

    }

    /**
     * here call add or remove favourite api
     */
    private fun callAddOrRemoveFavouriteApi() {
        if (isFavourite!!) {
            isFavourite = false
            addFav = 0
            ivBookMark.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))
            ivChat.setImageDrawable(resources.getDrawable(R.drawable.ic_unselected_book_mark))
            //   AppController.simpleExoplayer!!.currentMediaItem!!.mediaMetadata.buildUpon().setIsBrowsable(false)
        } else {
            isFavourite = true

            addFav = 1
            ivBookMark.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))
            ivChat.setImageDrawable(resources.getDrawable(R.drawable.ic_select_bookmark))

            //   AppController.simpleExoplayer!!.currentMediaItem!!.mediaMetadata.buildUpon().setIsBrowsable(true)

        }

        addFavouriteRequest.apply {
            product_video_id = videoId
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
}