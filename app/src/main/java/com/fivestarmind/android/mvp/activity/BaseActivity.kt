package com.fivestarmind.android.mvp.activity


import android.app.NotificationManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.InputFilter
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fivestarmind.android.*
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.dialog.ProgressDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.receiver.NetworkConnectivityReceiver
import com.fivestarmind.android.retrofit.ApiClient
import com.fivestarmind.android.service.MyAppPerformance
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import java.math.BigDecimal


open class BaseActivity : AppCompatActivity(),
    NetworkConnectivityReceiver.ConnectivityReceiverListener, DialogListener {

    internal open val TAG: String = this::class.java.simpleName
    private var progressDialog: ProgressDialog? = null
    private var lastClickedMilliseconds: Long = 0L

    private val networkConnectivityReceiver = NetworkConnectivityReceiver()

    private var serviceIntent: Intent? = null

    var socket: Socket? = null


    override fun onResume() {
        super.onResume()
        //mini()
        callToAppStartService()
        callRegisterNetworkConnectivityReceiver()
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchevent")
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    internal fun formatPrice(context: Context, price: BigDecimal): String =
        String.format(
            context.getString(R.string.format_price),
            price.setScale(Constants.App.Number.TWO, BigDecimal.ROUND_HALF_EVEN).toString()
        )

    fun showProgressDialog(message: String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this, message = message)

            try {
                progressDialog!!.show()

            } catch (e: WindowManager.BadTokenException) {

                Log.d(TAG, "BadTokenException: " + e.message!!)


            }
        }
    }

    fun hideProgressDialog() {
        if (null != progressDialog) {
            if (progressDialog!!.isShowing) {

                progressDialog!!.dismiss()

            }

            progressDialog = null
        }
    }


    fun showSestionExpiredtDialog(
        message: String,
        listener: DialogSesstionExpiredListener,
        baseContext: BaseActivity
    ) {
        val dialogSessionExpired = DialogSessionExpired(
            listener = listener,
            message = message,
            context = baseContext,

            )

        dialogSessionExpired.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    fun responseFailure(errorResponse: ErrorResponse) {
        if (errorResponse.message.equals("Unauthorized") || errorResponse.message.equals("Unauthenticated.")) {

            showToast(getString(R.string.session_expired))

        } else {

            showToast(errorResponse.message)

        }

    }


    /**
     * This method is responsible to register network connectivity receiver.
     */
    private fun callRegisterNetworkConnectivityReceiver() {
        registerReceiver(
            networkConnectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        NetworkConnectivityReceiver.connectivityReceiverListener = this
    }

    /**
     * This method is responsible to unregister network connectivity receiver.
     */
    private fun callUnregisterNetworkConnectivityReceiver() {
        NetworkConnectivityReceiver.connectivityReceiverListener = null

        unregisterReceiver(networkConnectivityReceiver)
    }

    /**
     * This callback will be called when there is change in network connection change.
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        AppHelper.updateNetworkConnectedStatus(status = isConnected)

        if (!isConnected)
            showToast(getString(R.string.check_your_network_connection))
    }

    /**
     * It is called to check whether Internet is Connected or not
     */
    internal fun isInternetConnected(shouldShowToast: Boolean): Boolean {
        return if (AppHelper.isNetworkConnected()) {
            true
        } else {
            if (shouldShowToast)
                showToast(
                    message = getString(R.string.check_your_network_connection)
                )

            false
        }
    }

    fun showToast(message: String) =
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()

    internal var emojiFilter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        for (index in start until end) {
            val type = Character.getType(source[index])

            if (type == Character.SURROGATE.toInt())
                return@InputFilter ""
        }
        null
    }

    internal fun shouldProceedClick(): Boolean {
        var status = false

        if (System.currentTimeMillis() - lastClickedMilliseconds > Constants.App.Duration.TIME) {
            lastClickedMilliseconds = System.currentTimeMillis()
            status = true
        }

        return status
    }

    /**
     * Here is call to show alert when session of user is expire
     */
    private fun showAlert(message: String) {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@BaseActivity,
            requestCode = Constants.App.RequestCode.FORCE_LOGOUT,
            message = message,
            positiveButtonText = getString(R.string.ok),
            listener = this@BaseActivity
        )

        positiveAlertDialog.show()
    }

    private fun callForForceLogout() {
        SharedPreferencesHelper.removeAllDetails()
        startActivity(
            Intent(
                this,
                SplashActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    fun getRealPath(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context,
                uri
            )
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }

                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /* private fun callToStartService() {
         Log.d(TAG, "callToStartService")
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             startForegroundService(Intent(this@BaseActivity, AppVideosService::class.java))
         } else {
             if (null == serviceIntent)
                 serviceIntent = Intent(this@BaseActivity, AppVideosService::class.java)

             startService(serviceIntent)
         }
     }
 */
    fun callToAppStartService() {
        Log.d(TAG, "callToAppStartService")
        if (null == serviceIntent) {
            serviceIntent = Intent(this@BaseActivity, MyAppPerformance::class.java)
            serviceIntent!!.action = MyAppPerformance.START_SERVICE
            baseContext.startService(serviceIntent)
        }
    }

    fun calltoStopService() {
        val intent = Intent(this@BaseActivity, MyAppPerformance::class.java)
        intent.action = MyAppPerformance.STOP_SERVICE
        startService(intent)
    }


    /**
     * This method is called to initially hide the password for edit text.
     *
     *@param etPaswrd editText to which transformationMethod is removed to hide paswrd
     * @param ivPaswrdVisibility imageView on which hide status is shown
     */
    internal fun initPaswrdVisibilityForEditText(
        etPassword: EditText,
        ivPaswrdVisibility: ImageView
    ) =
        showHidePaswrdVisibilityForEditText(
            etPassword = etPassword,
            ivPaswrdVisibility = ivPaswrdVisibility,
            status = true
        )

    /**
     * This method is called to hide or show the password for edit text.
     *
     *@param etPaswrd editText to which transformationMethod is removed or set to hide or show paswrd respectively
     * @param ivPaswrdVisibility imageView on which hide or show status is shown
     */
    internal fun callShowHidePaswrdVisibilityForEditText(
        etPassword: EditText,
        ivPaswrdVisibility: ImageView
    ) {
        val status: Boolean = !ivPaswrdVisibility.isSelected

        showHidePaswrdVisibilityForEditText(
            etPassword = etPassword,
            ivPaswrdVisibility = ivPaswrdVisibility,
            status = status
        )
    }

    /**
     * This method is called to hide or show the password for edit text.
     *
     *@param etPaswrd editText to which transformationMethod is removed or set to hide or show paswrd respectively
     * @param ivPaswrdVisibility imageView on which hide or show status is shown
     * @param status boolean value which is used for hide/show status
     */
    private fun showHidePaswrdVisibilityForEditText(
        etPassword: EditText,
        ivPaswrdVisibility: ImageView,
        status: Boolean
    ) {
        etPassword.apply {
            transformationMethod = if (status) PasswordTransformationMethod() else null
            setSelection(length())
        }

        ivPaswrdVisibility.apply {
            isSelected = status
        }
    }

    override fun onDialogEventListener(
        dialogEventType: DialogEventType,
        requestCode: Int,
        model: Any?
    ) {
        when (dialogEventType) {
            DialogEventType.POSITIVE -> onDialogPositiveEvent(
                requestCode = requestCode,
                model = model
            )

            else -> {}
        }
    }

    protected open fun onDialogPositiveEvent(requestCode: Int, model: Any?) {
        Log.d(TAG, "onDialogPositiveEvent: requestCode- $requestCode")

        when (requestCode) {
            Constants.App.RequestCode.FORCE_LOGOUT ->
                callForForceLogout()
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        callUnregisterNetworkConnectivityReceiver()
    }

    protected fun hideSoftKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    internal fun hideKeyboardAndBottomSheet(
        event: MotionEvent,
        bottomSheetBehavior: BottomSheetBehavior<*>?,
        bottomSheet: ConstraintLayout
    ) {
        hideSoftKeyboard()

        if (event.action === MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior?.state === BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()
                bottomSheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(
                        event.rawX.toInt(),
                        event.rawY.toInt()
                    )
                ) bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    fun initializeMiniAudioPlayer(
        audioPlayerView: PlayerView,
        titleName: TextView,
        exoImage: ImageView,
        progressBar: ProgressBar
    ) {
        runOnUiThread {
            if (AppController.simpleExoplayer!!.playbackState == 3) {
                audioPlayerView.visibility = View.VISIBLE
                audioPlayerView.player = AppController.simpleExoplayer
                audioPlayerView.showController()

                titleName.text = AppController.audioTitle


                Log.e("ListnerBaseActivity: ", "Image==" + AppController.audioImge)

                if (AppController.audioImge != null) {


                    progressBar.visibility = View.VISIBLE


                    if (AppController.audioImge!!.contains("http")) {

                        //       Picasso.get().load(AppController.audioImge).placeholder(R.drawable.ic_audio)
                        Picasso.get().load(AppController.audioImge).placeholder(R.drawable.ic_mp3)
                            .into(exoImage, object : Callback {
                                override fun onSuccess() {
                                    progressBar.visibility = View.GONE
                                }

                                override fun onError(e: Exception) {
                                    progressBar.visibility = View.GONE
                                    exoImage.setImageResource(R.drawable.ic_mp3)

                                }
                            })

                    } else {

                        Picasso.get().load(ApiClient.BASE_URL_MEDIA + AppController.audioImge)
                            .into(exoImage, object : Callback {
                                override fun onSuccess() {
                                    progressBar.visibility = View.GONE
                                }

                                override fun onError(e: Exception) {
                                    progressBar.visibility = View.GONE
                                    exoImage.setImageResource(R.drawable.ic_mp3)

                                }
                            })

                    }
                } else {
                    exoImage.setImageResource(R.drawable.ic_mp3)

                }
            } else if (AppController.simpleExoplayer!!.playbackState == 4) {

                audioPlayerView.visibility = View.GONE
            } else if (AppController.simpleExoplayer!!.playbackState == 1) {

                audioPlayerView.visibility = View.GONE

            }

            AppController.simpleExoplayer!!.addListener(object : Player.Listener {
                override fun onMediaItemTransition(@Nullable mediaItem: MediaItem?, reason: Int) {

                    Log.e("playerListner: ", "Base1_onMediaItemTransition")

                    if (mediaItem != null) {
                        Log.e("playerListner: ", "Base2_onMediaItemTransition")

                        runOnUiThread {


                            audioPlayerView.visibility = View.VISIBLE
                            val songTitle = mediaItem.mediaMetadata.title
                            //  AppController.audioImge = mediaItem.mediaMetadata.albumArtist.toString()
                            AppController.audioTitle = songTitle.toString()
                            titleName.text = songTitle

                            progressBar.visibility = View.VISIBLE


                            Picasso.get()
                                .load(mediaItem.mediaMetadata.albumArtist.toString())
                                .placeholder(R.drawable.ic_mp3)
                                .into(exoImage, object : Callback {
                                    override fun onSuccess() {
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onError(e: Exception) {
                                        Log.e("ListnerBaseActivity: ", "Exception==" + e.message)

                                        progressBar.visibility = View.GONE
                                        exoImage.setImageResource(R.drawable.ic_mp3)
                                    }
                                })
                        }

                        //      }


                    }
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        Log.d(TAG, "STATE_READY= audio is playing")
                        audioPlayerView.visibility = View.VISIBLE
                        //  playVideo()

                    } else if (playWhenReady && playbackState == Player.STATE_IDLE) {

                        Log.d(TAG, "onPlayerStateChanged= audio is STATE_IDLE")


                    }

                    //                else if (playWhenReady) {
                    //                    // might be idle (plays after prepare()),
                    //                    // buffering (plays when data available)
                    //                    Log.d(TAG, "video is loading ")
                    //                    // or ended (plays when seek away from end)
                    //                }

                    else if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "STATE_ENDED")

                        // AppController.simpleExoplayer!!.seekTo(0)
                        AppController.simpleExoplayer!!.playWhenReady = false
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(1)

                        audioPlayerView.visibility = View.GONE
                        //SharedPreferencesHelper.saveAudioId("")


                        if (AppController.activityName.equals("AudioPlayActivity")) {
                            if (!SharedPreferencesHelper.getAudioId().isEmpty()) {
                                if (AppController.audioStateEnd.equals("false")) {
                                    sendMessage()

                                } else {
                                    AppController.audioStateEnd = "false"

                                }


                            }
                        } else {

                            audioPlayerView.visibility = View.GONE
                            AppController.playerNotificationManager!!.setPlayer(null)
                            // AppController.simpleExoplayer!!.playWhenReady = false


                        }

                    }
                }

            })
        }

        /*  AppController.simpleExoplayer!!.addListener(object : Player.Listener {

              override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                  if (playWhenReady && playbackState == Player.STATE_READY) {
                      Log.d(TAG, "STATE_READY= audio is playing")
                      audioPlayerView.visibility = View.VISIBLE
                      //  playVideo()

                  } else if (playWhenReady && playbackState == Player.STATE_IDLE) {

                      Log.d(TAG, "onPlayerStateChanged= audio is STATE_IDLE")


                  }

                  //                else if (playWhenReady) {
                  //                    // might be idle (plays after prepare()),
                  //                    // buffering (plays when data available)
                  //                    Log.d(TAG, "video is loading ")
                  //                    // or ended (plays when seek away from end)
                  //                }

                  else if (playbackState == Player.STATE_ENDED) {
                      Log.d(TAG, "STATE_ENDED")

                      // AppController.simpleExoplayer!!.seekTo(0)
                      AppController.simpleExoplayer!!.playWhenReady = false
                      val notificationManager =
                          getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                      notificationManager.cancel(1)

                      audioPlayerView.visibility = View.GONE
                      //SharedPreferencesHelper.saveAudioId("")


                      if (AppController.activityName.equals("AudioPlayActivity")) {
                          if (!SharedPreferencesHelper.getAudioId().isEmpty()) {
                              if (AppController.audioStateEnd.equals("false")) {
                                  sendMessage()
                              } else {
                                  AppController.audioStateEnd = "false"

                              }


                          }
                      } else {

                          audioPlayerView.visibility = View.GONE
                          AppController.playerNotificationManager!!.setPlayer(null)


                      }

                  }
              }

              *//*override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().message);
                //Restart the playback
               // initializePlayer()
            }*//*
        })*/


        audioPlayerView.setOnClickListener {
            startActivity(
                Intent(this@BaseActivity, AudioPlayActivity::class.java)
                    .putExtra(
                        Constants.App.AUDIO_LINK,
                        AppController.simpleExoplayer!!.currentMediaItem?.playbackProperties?.uri.toString()
                    )
                    .putExtra(
                        Constants.App.POSITION, AppController.position
                    )
                    .putExtra(
                        Constants.App.AUDIO_ID, AppController.audioId
                    ).putExtra(
                        Constants.App.AUDIO_TITLE, AppController.audioTitle
                    ).putExtra(
                        Constants.App.AUDIO_DURATION, AppController.duration
                    ).putExtra(
                        Constants.App.AUDIO_ACTIVITY_NAME, AppController.activityHeaderName
                    ).putExtra(
                        Constants.App.IS_FAVROITE, AppController.isFavourite
                    ).putExtra(
                        Constants.App.AUDIO_THUMPATH, AppController.audioImge
                    ).putExtra(
                        Constants.App.SCREEN_TYPE, "BaseActivity"
                    )
            )


        }


    }


    // Send an Intent with an action named "custom-event-name". The Intent sent should
    // be received by the ReceiverActivity.
    private fun sendMessage() {
        AppController.controllerMediaItems.clear()
        SharedPreferencesHelper.saveAudioId("")

        Log.d("sender", "Broadcasting message")
        val intent = Intent("closeAudioActivity")
        // You can also include some extra data.
        intent.putExtra("message", "close")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    protected fun getViewAudioDuration(): String {
        val duration: Long = AppController.simpleExoplayer?.currentPosition?.toLong()!!
        val videoDuration = AppHelper.convertMillisecondsToSeconds(milliseconds = duration)
        return videoDuration.toString()


    }


    //
    open fun socketUpdate() {
        val opts = IO.Options()
        opts.reconnection = true
        opts.path = "/socket.io"
        opts.forceNew = true
        opts.transports = arrayOf("websocket")
        opts.upgrade = false
        try {

            //AppController app = (AppController) getContext();
            // socket = app.getSocket();
            //  socket = IO.socket(UrlHelper.SOCKET_BASE_URL, opts)
            socket = IO.socket(ApiClient.SOCKET_BASE_URL, opts)

            Log.d(TAG, "socketConnect: Socket init")
            socket!!.connect()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d("SOCKET.IO ", e.message!!)
        }
        try {


            /*  socket!!.on(Manager.EVENT_TRANSPORT) { arg ->
                  val transport: Transport = arg[0] as Transport
                  transport.on(Transport.EVENT_REQUEST_HEADERS, Emitter.Listener { args ->
                      val headers = args[0] as MutableMap<String, List<String>>
                      val bearer = "bearer " + SharedPreferencesHelper.getAuthToken()
                      headers["Authorization"] = Arrays.asList(bearer)
                  }).on(Transport.EVENT_RESPONSE_HEADERS, Emitter.Listener { })


              }
  */
            socket!!.on(Socket.EVENT_CONNECT, Emitter.Listener { args ->
                Log.e(TAG, "args: " + args.size)
                Log.d(TAG, "socketConnect4: Socket Connected")
            })
            socket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener { args ->
                //                    Log.d(TAG, "call:" + args[0]);
                Log.d(TAG, "socketConnect1: Socket Disconnected")
                //CommonClass.toast(context, "Service disconnect");
            })
            socket!!.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
                Log.d(TAG, "call:" + args[0].toString())
                Log.d(TAG, "socketConnect1: Socket Error")
                //CommonClass.toast(context, "Service disconnect");
            })

            /*socket.on(ConstantKeys.SOCKET_LIVE_STREAMING_START, Emitter.Listener {
                Log.d(TAG, "liveStreamingStart: ")
                EventBus.getDefault().post(ConstantKeys.EVENT_BUS_KITCHEN_ONLINE)
            })*/
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Clear all data from local DB when login session expired
     */
    fun onSessionExpiredClearLocalDB(databaseHelper: DatabaseHelper) {
        AppController.mSocketManager = null
        SharedPreferencesHelper.removeAllDetails()
        databaseHelper.removeAllData()
        if (AppController.simpleExoplayer != null) {
            if (AppController.simpleExoplayer!!.isPlaying) {
                AppController.simpleExoplayer!!.stop()
                AppController.simpleExoplayer!!.release()
            }
        }

        if (AppController.playerNotificationManager != null) {
            AppController.playerNotificationManager!!.setPlayer(null)
        }
    }

    /* class Emitters(var context: Context) {
         init {
             if (socket == null) {
                 socketUpdate()
             }
         }

         */
    /**
     * here Called socket userOnline
     *
     * @param context
     * @param jsonObject
     *//*
        fun userOnline(context: Context?, jsonObject: JSONObject) {
            Log.d(BaseActivity.TAG, "userOnline__Meathod: $jsonObject")
            socket.emit(ConstantKeys.SOCKET_USER_ONLINE, jsonObject)
        }

        */
    /**
     * here Called socket userOffline
     *
     * @param context
     * @param jsonObject
     *//*
        fun userOffline(context: Context?, jsonObject: JSONObject) {
            Log.d(BaseActivity.TAG, "userOffline__Meathod:$jsonObject")
            socket.emit(ConstantKeys.SOCKET_USER_OFFLINE, jsonObject)
        }

        fun restaurantLike(context: Context?, jsonObject: JSONObject) {
            Log.d(BaseActivity.TAG, "restaurantLike__jsonObject:$jsonObject")
            socket.emit(ConstantKeys.SOCKET_RESTAURANT_LIKE, jsonObject)
        }
    }*/


}
