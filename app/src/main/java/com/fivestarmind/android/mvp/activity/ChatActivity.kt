package com.fivestarmind.android.mvp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.MessageEvent
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.ChatAdapter
import com.fivestarmind.android.mvp.dialog.DialogChooseImage
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.MessageResponseModel
import com.fivestarmind.android.mvp.model.response.MessagesItem
import com.fivestarmind.android.mvp.model.response.MessagesListingResponseModel
import com.fivestarmind.android.mvp.model.response.SendChatDataModel
import com.fivestarmind.android.mvp.model.response.ThreadIdResponse
import com.fivestarmind.android.mvp.presenter.MessagesListingPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.fivestarmind.android.socket.SocketManager
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.edTypeMessage
import kotlinx.android.synthetic.main.activity_chat.ivBack
import kotlinx.android.synthetic.main.activity_chat.ivCamera
import kotlinx.android.synthetic.main.activity_chat.ivProfileImage
import kotlinx.android.synthetic.main.activity_chat.ivSendMessage
import kotlinx.android.synthetic.main.activity_chat.pbChatProfile
import kotlinx.android.synthetic.main.activity_chat.pbLoadMore
import kotlinx.android.synthetic.main.activity_chat.rvChatList
import kotlinx.android.synthetic.main.activity_chat.tvChatTitle
import kotlinx.android.synthetic.main.activity_chat.tvNoDataFound
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File


class ChatActivity : BaseActivity(), RecyclerViewItemListener,
    MessagesListingPresenter.ResponseCallBack, DialogSesstionExpiredListener {
    private lateinit var presenter: MessagesListingPresenter
    private var messageUserId: String? = null
    private var messageUserName: String? = null
    private var messageUserImage: String? = null
    private var imageFile: File? = null
    private var chatAdapter: ChatAdapter? = null
    private var userChatList: ArrayList<MessagesItem>? = null

    private val sendChatDataModel = SendChatDataModel()
    var conversationId: String? = null
    var dialogSessionExpired: DialogSessionExpired? = null
    private var databaseHelper = DatabaseHelper(this)

    var mUserImage = ""
    var mUserName = ""
    var isLoadMore: Boolean = false
    var noMoreDataFound: Boolean = false
    var gotoChatlistEnd: Boolean = false

    var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        //  socketConnection()
        init()
        getDataFromIntent()
        setClickListener()
        clearNotificationFromStack()
        // initializeMiniAudioPlayer(chatAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        if (AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
        }
        addScrollListener()

    }

    override fun onResume() {
        super.onResume()
        AppController.isChatActivityOpen = true
        clearNotificationFromStack()
        //   initializeMiniAudioPlayer(chatAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        if (AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
        }

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageReceivedEvent(event: MessageEvent) {

        if (shouldProceedClick()) {
            Log.d(TAG, "onMessageReceivedEvent: ${event.message}")

            var messageResponseModel: MessageResponseModel? = null
            messageResponseModel = GsonHelper.convertJsonStringToJavaObject(
                event.message,
                MessageResponseModel::class.java
            ) as MessageResponseModel?

            val messagesItem = MessagesItem()
            messagesItem.id = messageResponseModel!!.message!!.threadId
            messagesItem.senderUserId = messageResponseModel.message!!.senderUserId
            messagesItem.content = messageResponseModel.message!!.content
            messagesItem.createdAt = messageResponseModel.message!!.createdAt

            if (userChatList!!.size > 0) {

                if (messageResponseModel.message!!.threadId.equals(userChatList!!.get(0).threadId)) {

                    if (conversationId.equals("Not Found")) {
                        //call GetThreadId api
                        //    showProgressDialog("")
                        presenter.hitGetThreadIdApi(
                            "Bearer " + SharedPreferencesHelper.getAuthToken(),
                            messageUserId!!.toInt()
                        )

                    } else {
                        Handler().postDelayed(Runnable {
                            //chatAdapter!!.insertData(messagesItem)
                            userChatList!!.add(messagesItem)
                            chatAdapter!!.notifyDataSetChanged()
                            rvChatList.scrollToPosition(userChatList!!.size - 1)
                        }, 100)

                    }
                }

            } else {

                presenter.hitGetThreadIdApi(
                    "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    messageUserId!!.toInt()
                )


            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUnauthenticated(message: String) {
        if (message.equals("Unauthorized")) {
            if (dialogSessionExpired == null) {

                dialogSessionExpired = DialogSessionExpired(
                    listener = this,
                    message = getString(R.string.session_expired),
                    context = this,

                    )
            }

            if (dialogSessionExpired!!.isShowing && dialogSessionExpired != null) {
                dialogSessionExpired!!.dismiss()
            }

            try {


                if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                    dialogSessionExpired!!.show()
                }

            } catch (e: WindowManager.BadTokenException) {
                //use a log message
                Log.d(TAG, "BadTokenException" + e.message)
            }
        }

    }


    private fun removeQuotesAndUnescape(uncleanJson: String): String {
        val noQuotes = uncleanJson.replace("".toRegex(), "")
        return noQuotes
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.MESSAGE_USER_ID) && null != intent.getStringExtra(
                Constants.App.MESSAGE_USER_ID
            )
        ) {
            messageUserId = intent.getStringExtra(Constants.App.MESSAGE_USER_ID)
            messageUserName = intent.getStringExtra(Constants.App.MESSAGE_USER_NAME)!!
            if (intent.getStringExtra(Constants.App.MESSAGE_USER_IMAGE) != null) {
                messageUserImage = intent.getStringExtra(Constants.App.MESSAGE_USER_IMAGE)
            }
            if (messageUserName != null) {
                setProfileName(messageUserName!!)
            }
            if (messageUserImage != null) {

                setProfileImage(messageUserImage!!)
            }

            showProgressDialog("")
            presenter.hitGetThreadIdApi(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                messageUserId!!.toInt()
            )

        } else if (!SharedPreferencesHelper.getMessageSenderId().isNullOrEmpty()) {
            messageUserId = SharedPreferencesHelper.getMessageSenderId()

            showProgressDialog("")
            presenter.hitGetThreadIdApi(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                messageUserId!!.toInt()
            )

        }

    }


    private fun init() {
        // AppController.mSocketManager!!.connect()
        presenter = MessagesListingPresenter(this)
        userChatList = ArrayList()
        gotoChatlistEnd = true
    }

    private fun setProfileName(name: String) {
        tvChatTitle.text = name

    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        if (shouldProceedClick()) {
            ivBack.setOnClickListener(clickListener)
            ivCamera.setOnClickListener(clickListener)
            ivSendMessage.setOnClickListener(clickListener)
            //here call setOnFocusChangeListener
            edTypeMessage.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {

                    val layoutManager = LinearLayoutManager(this)

                    if (gotoChatlistEnd) {
                        gotoChatlistEnd = false
                        layoutManager.stackFromEnd = true
                        rvChatList.layoutManager = layoutManager
                    }

                }
            }

        }
    }

    private fun socketConnection() {
        AppController.mSocketManager = null
        AppController.mSocketManager = SocketManager()
        AppController.mSocketManager!!.connect()
        /*  if (AppController.mSocketManager != null) {
              AppController.mSocketManager!!.connect()
          } else {
              AppController.mSocketManager = SocketManager()
              AppController.mSocketManager!!.connect()
          }*/
    }

    private fun onClickSendMessage() {
        Handler().postDelayed(Runnable {
            if (AppController.mSocketManager!!.isConnected()) {

                callSendMessageApi()
            } else {
                socketConnection()
                callSendMessageApi()

            }
        }, 100)


    }

    /**
     * Here call click listner
     */
    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> onBackPressed()
                R.id.ivCamera -> checkCameraPermission()
                R.id.ivSendMessage -> onClickSendMessage()

            }
    }


    override fun onBackPressed() {
        AppController.isChatActivityOpen = false
        SharedPreferencesHelper.saveMessageSenderId("")


        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }

        val subscribedObject = JSONObject()
        if (userChatList!!.size > 0) {
            subscribedObject.put("threadId", userChatList!!.get(0).threadId)
        }
        Log.d(TAG, "subscribedObject: " + subscribedObject.toString())

        AppController.mSocketManager!!.subscribed(subscribedObject)

        AppController.mSocketManager!!.unSubscribed(subscribedObject)

        onBackPressedDispatcher.onBackPressed()
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
                    //   val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    //     imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * here call send messsage api
     */
    private fun callSendMessageApi() {

        val messageText = edTypeMessage.text.toString().trim()
        messageText.replace(" ", "")

        edTypeMessage.requestFocus()
        if (messageText.isNotEmpty() && !messageText.startsWith(" ")) {
            //if (edTypeMessage.text.toString().isNotEmpty()) {
            sendChatDataModel.receiverUserId = messageUserId!!.toInt()
            sendChatDataModel.content = messageText
            sendChatDataModel.type = "TEXT"

            presenter.hitSendMessageApi(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                sendChatDataModel
            )

            //   edTypeMessage.setText("")
        }

    }

    private fun closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        val view = this.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {

                    Constants.App.RequestCode.CAMERA -> beginCrop(Uri.fromFile(imageFile))

                    Crop.REQUEST_CROP -> {
                        val imageUri = Crop.getOutput(data)
                        Log.d(TAG, "Image Uri --> $imageUri")
                        // setImage(imageUri!!)
                        // imageFile = File(getRealPath(this,  imageUri))

                        // isImageSelected = true

                        try {
                            if (Uri.parse(imageUri.toString()) != null) {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    this.contentResolver,
                                    imageUri
                                )


                                imageFile = File(
                                    getRealPath(
                                        this,
                                        getImageUri(rotateImageIfRequired(bitmap, imageUri)!!)
                                    )
                                )

                                setImage(imageUri!!)


                            }
                        } catch (e: java.lang.Exception) {
                            //handle exception
                        }

                    }

                    Constants.App.RequestCode.GALLERY -> beginCrop(data!!.data)
                }
            }

            Activity.RESULT_CANCELED -> {
                //  finish()
            }
        }
    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {

        //    chatAdapter = ChatAdapter(this, userChatList!!, this)

        /*     rvChatList.apply {
                 // layoutManager = linearLayoutManager
                 adapter = chatAdapter
             }*/
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {

    }


    /**
     * Request for Permissions of camera & external storage
     */
    private fun checkCameraPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.App.PermissionsCode.CAMERA_STORAGE,
                    Constants.App.RequestCode.CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.App.PermissionsCode.CAMERA_STORAGE,
                    Constants.App.RequestCode.CAMERA
                )
                return true
            }
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.App.RequestCode.CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFile()
                    //captureImage()
                    DialogChooseImage(
                        this@ChatActivity,
                        imageFile!!,
                        showOptionForVideo = false
                    ).show()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        // now, user has denied permission (but not permanently!)
                        showAlertForPermissions()
                    } else
                        showAlertForSettings()
                }
                return
            }
        }


    }


    /**
     * Here is call for Intent to open Camera
     */
    private fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val apkURI = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName + ".provider",
            imageFile!!
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        this.startActivityForResult(cameraIntent, Constants.App.RequestCode.CAMERA)
    }

    private fun createFile() {

        try {
            val myDirectory = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path
                    .toString() + "/" + getString(R.string.app_name)
            )
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            imageFile = File(myDirectory.path, "image_${System.currentTimeMillis()}.png")

            imageFile!!.createNewFile()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Show dialog if user deny permissions but temporary
     */
    private fun showAlertForPermissions() {
        val alertDialog = AlertDialog.Builder(this, R.style.BaseDialogStyle)
        alertDialog.setMessage(getString(R.string.access_permission_msg))
        alertDialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
            ActivityCompat.requestPermissions(
                this,
                Constants.App.PermissionsCode.CAMERA_STORAGE,
                Constants.App.RequestCode.CAMERA
            )
        }
        alertDialog.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Show dialog if user deny permissions permanently
     */
    private fun showAlertForSettings() {
        val alertDialog = AlertDialog.Builder(this, R.style.BaseDialogStyle)
        alertDialog.setMessage(getString(R.string.access_permission_msg))
        alertDialog.setPositiveButton(getString(R.string.settings)) { dialog, _ ->
            sendUserToSettings()
        }
        alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    /**
     * Send user into settings (if user deny permissions permanently)
     */
    private fun sendUserToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + this.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        this.startActivity(intent)
    }

    private fun beginCrop(source: Uri?) {
        val destination = Uri.fromFile(File(cacheDir, "cropped_" + System.currentTimeMillis()))
        Crop.of(source, destination).asSquare().start(this)
    }


    /**
     * Updating selected/captured image into view
     *
     */
    private fun setImage(imageUri: Uri) {
        Picasso.get()
            .load(imageUri)
            .centerCrop().fit()
            .into(ivCamera)
    }

    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            this.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(selectedImage.path!!)
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }


    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }


    private fun getThreadID(threadID: String?) {
        if (threadID != null) {
            Log.d(TAG, "thread ID: " + threadID)

            presenter.hitGetChatListing(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                threadID,
                messageId = ""
            )


        }
    }

    private fun setProfileImage(image: String) {
        pbChatProfile.visibility = View.VISIBLE
        Glide.with(baseContext)
            .load(ApiClient.BASE_URL_PROFILE + image)
            .placeholder(R.drawable.ic_user_placeholder).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    ivProfileImage.setImageResource(R.drawable.ic_user_placeholder)
                    pbChatProfile.visibility = View.GONE
                    return false

                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    pbChatProfile.visibility = View.GONE
                    return false
                }

            }).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .into(ivProfileImage)
    }

    override fun onMessagesListingResponseSuccess(response: MessagesListingResponseModel) {
    }

    override fun onGetThreadIdResponseSuccess(response: ThreadIdResponse) {
        if (this@ChatActivity.baseContext != null) {
            runOnUiThread {
                if (response.data!!.thread != null) {
                    if (response.data.thread!!.id != null) {
                        conversationId = response.data.thread.id
                        getThreadID(conversationId)
                        for (i in 0 until response.data.users!!.size) {

                            if (!response.data.users.get(i).id.equals(SharedPreferencesHelper.getUserId())) {
                                //here clear message user id get from notifiation

                                if (response.data.users.get(i).profileImg != null) {
                                    mUserImage = response.data.users.get(i).profileImg!!

                                    setProfileImage(mUserImage)

                                }
                                if (response.data.users.get(i).name != null) {

                                    mUserName = response.data.users.get(i).name!!
                                    setProfileName(mUserName)


                                }


                            }
                        }
                    }
                }

            }

        }
    }

    override fun onGetChatListingResponseSuccess(response: ThreadIdResponse) {
        if (this@ChatActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                if (pbLoadMore.isVisible) {
                    pbLoadMore.visibility = View.GONE
                }
                if (response.data != null) {

                    if (response.data.messages!!.size > 0) {
                        tvNoDataFound.visibility = View.GONE

                        if (isLoadMore) {

                            if (userChatList != null) {
                                userChatList!!.addAll(0, response.data.messages)
                                linearLayoutManager!!.scrollToPosition(response.data.messages.size + 4)
                                chatAdapter!!.notifyDataSetChanged()

                            }
                        } else {
                            userChatList!!.clear()
                            userChatList!!.addAll(response.data.messages)


                        }

                        var userImage = ""
                        var userName = ""

                        if (messageUserImage != null) {
                            userImage = messageUserImage!!
                        }
                        if (messageUserName != null) {
                            userName = messageUserName!!
                        }

                        if (!SharedPreferencesHelper.getMessageSenderId().isNullOrEmpty()) {
                            if (!mUserImage.isNullOrEmpty()) {
                                userImage = mUserImage
                                setProfileImage(userImage)
                            }
                            if (!mUserName.isNullOrEmpty()) {

                                userName = mUserName
                                setProfileName(userName)

                            }
                        }
                        if (!isLoadMore) {

                            linearLayoutManager = rvChatList.layoutManager as LinearLayoutManager
                            chatAdapter = ChatAdapter(
                                this,
                                userChatList!!,
                                userImage,
                                userName,
                                this
                            )

                            rvChatList.apply {
                                layoutManager = linearLayoutManager
                                adapter = chatAdapter
                            }
                        }
                        if (!isLoadMore) {
                            Log.d(TAG, "chatAdapter: scroll")

                            rvChatList.scrollToPosition(userChatList!!.size - 1)
                        }
                        isLoadMore = false


                    } else {
                        if (!isLoadMore) {
                            if (userChatList!!.size == 0) {
                                tvNoDataFound.visibility = View.VISIBLE
                            } else {
                                getThreadID(conversationId)
                            }
                        } else {
                            isLoadMore = false
                            noMoreDataFound = true
                        }

                    }

                }
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        AppController.isChatActivityOpen = false
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }
        closeKeyboard()

    }

    override fun onSendChatResponseSuccess(message: String) {

        if (this@ChatActivity.baseContext != null) {
            runOnUiThread {
                edTypeMessage.setText("")

                Log.d(TAG, "onSendChatResponseSuccess: " + message)

                rvChatList.scrollToPosition(userChatList!!.size - 1)

                sendChatDataModel.receiverUserId = 0
                sendChatDataModel.content = ""
                sendChatDataModel.type = ""
            }

        }
    }

    override fun onFailureResponse(message: String) {
        if (this@ChatActivity.baseContext != null) {
            // refreshLayout.isRefreshing = false

            runOnUiThread {
                if (this@ChatActivity.baseContext != null) {

                    Log.d(
                        TAG,
                        "onFailureResponse hitGetChatListing = $message"
                    )
                    hideProgressDialog()
                    if (message.equals("Unauthorized")) {

                        if (dialogSessionExpired == null) {

                            dialogSessionExpired = DialogSessionExpired(
                                listener = this,
                                message = getString(R.string.session_expired),
                                context = this,

                                )
                        }

                        if (dialogSessionExpired!!.isShowing && dialogSessionExpired != null) {
                            dialogSessionExpired!!.dismiss()
                        }

                        try {


                            if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }

                    } else {
                        if (message.equals("Not Found")) {
                            conversationId = message
                        }
                        if (message.equals("Too Many Requests")) {
                            tvNoDataFound.visibility = View.GONE
                            showToast(message)
                        } else {
                            tvNoDataFound.visibility = View.VISIBLE
                        }
                        //userChatList!!.clear()
                        // chatAdapter!!.notifyDataSetChanged()

                        // Toast.makeText(this@ChatActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

        if (!isFinishing && dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
        onSessionExpiredClearLocalDB(databaseHelper)
        // updateLoginStatus()

        startActivity(
            Intent(
                this@ChatActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }

    fun clearNotificationFromStack() {
        NotificationManagerCompat.from(baseContext).cancelAll()

        if (AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
            AppController.simpleExoplayer!!.play()
        }

        if (!AppController.simpleExoplayer!!.isPlaying) {
            AppController.simpleExoplayer!!.pause()
        }
    }

    private fun addScrollListener() {
        rvChatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("SuspiciousIndentation")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearLayoutManager != null) {
                    val firstVisibleItemPosition: Int =
                        linearLayoutManager!!.findFirstVisibleItemPosition()

                    if (dy > 0) {
                        // Scrolling up
                        //  Log.d(TAG, "Scrolling up")

                    } else {
                        // Scrolling down
                        //   Log.d(TAG, "Scrolling down")

                        if (conversationId != null && firstVisibleItemPosition == 0 && isLoadMore == false && noMoreDataFound == false) {
                            isLoadMore = true
                            noMoreDataFound = false
                            pbLoadMore.visibility = View.VISIBLE

                            presenter.hitGetChatListing(
                                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                                conversationId!!,
                                messageId = userChatList!!.get(0).id!!
                            )

                        }
                    }
                } else {

                    return
                }

            }

        })
    }
}