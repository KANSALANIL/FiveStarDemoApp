package com.fivestarmind.android.mvp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gowtham.library.utils.TrimVideo
import com.fivestarmind.android.*
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.MediaHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.VideoCompressionListener
import com.fivestarmind.android.mvp.dialog.DialogChooseImage
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.AddJournalPresenter
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_journal_pics.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddJournalActivity : BaseActivity(), AddJournalPresenter.ResponseCallBack {

    override var TAG = AddJournalActivity::class.java.simpleName
    private lateinit var presenter: AddJournalPresenter
    private var cal = Calendar.getInstance()
    private var date: Date? = null

    private var trimType = 0

    private var file: File? = null
    private var videoFile: File? = null

    private var coverImageFile: File? = null
    private var isImageSelected = false

    private var compressedVideoFile: File? = null
    private var videoCompressionStatus: Boolean = false
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_pics)

        setToolbarTitle()
        initPresenter()
        initEditTextFilters()
        clickListener()
        clickForDatePicker()
    }

    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.journal_pics_and_videos)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = AddJournalPresenter(this)
        checkIsUserLoggedIn()
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etNote.filters = etNote.filters + this
        }
    }

    /**
     * Click event for views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        btnSubmit.setOnClickListener(clickListener)
        clJournalPic.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

                R.id.btnSubmit -> clickedSubmit()

                R.id.clJournalPic -> checkCameraPermission()
            }
    }

    /**
     * It is called when user click on submit button
     */
    private fun clickedSubmit() {
        if (!validateDetailsForJournal()) return

        if (isInternetConnected(shouldShowToast = true))
            hitApiToCreateJournal()
    }

    /**
     * Check validation for create journal
     *
     * @return status for validation success/failure
     */
    private fun validateDetailsForJournal(): Boolean {
        var valid = true

        if (null == file && null == videoFile) {
            showToast(getString(R.string.upload_photo_or_video))
            valid = false
        }

        etDate.error = when {
            etDate.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etNote.error = when {
            etNote.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        return valid
    }

    /**
     * Selection of date from calender
     */
    private fun clickForDatePicker() {
        etDate.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    datePickerDialog = DatePickerDialog(
                        this@AddJournalActivity,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )

                    datePickerDialog.show()
                    datePickerDialog.datePicker.minDate = Date().time
                }
            }
            false
        }
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    /**
     * update selected date in view
     */
    private fun updateDateInView() {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etDate.text = sdf.format(cal.time)
        date = sdf.parse(etDate.text.toString())
    }

    /**
     * call for api to create journal
     */
    private fun hitApiToCreateJournal() {
        val note = etNote.text.toString()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val strDate = dateFormat.format(date!!)

        if (!isImageSelected) {
            if (compressedVideoFile == null) {
                showToast(message = getString(R.string.pls_wait_video_is_uploading))
                return
            } else {
                showProgressDialog("")
                Handler().postDelayed({
                    presenter.apiPostToCreateJournalWithVideo(
                        activity = this@AddJournalActivity,
                        SharedPreferencesHelper.getAuthToken(),
                        compressedVideoFile!!, coverImageFile = coverImageFile!!, strDate, note
                    )
                }, 1000)
            }
        } else {
            showProgressDialog("")
            presenter.apiPostToCreateJournalWithImage(
                SharedPreferencesHelper.getAuthToken(),
                file!!, strDate, note
            )
        }
    }

    /**
     * Check for user logged in or not
     */
    private fun checkIsUserLoggedIn() {
        when (AppHelper.isUserLoggedIn()) {
            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.JOURNAL_PICS
                )
            }

            else -> {}
        }
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
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.App.PermissionsCode.CAMERA_VIDEO_ARRAY,
                    Constants.App.RequestCode.CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.App.PermissionsCode.CAMERA_VIDEO_ARRAY,
                    Constants.App.RequestCode.CAMERA
                )
                return true
            }
        }
        return true
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
     * Show dialog if user deny permissions but temporary
     */
    private fun showAlertForPermissions() {
        val alertDialog = AlertDialog.Builder(this, R.style.BaseDialogStyle)
        alertDialog.setMessage(getString(R.string.access_permission_msg))
        alertDialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
            ActivityCompat.requestPermissions(
                this,
                Constants.App.PermissionsCode.CAMERA_VIDEO_ARRAY,
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

    /**
     * Crop captured/selected image
     *
     * @param source image uri
     */
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
            .into(ivJournal)
    }

    private fun createFile() {
        try {
            val myDirectory = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + getString(R.string.app_name)
            )
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            file = File(myDirectory.path, "image_${System.currentTimeMillis()}.png")
            file!!.createNewFile()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This calls VideoTrimmingActivity without finishing the current activity for result
     *
     * @param data
     */
    private fun goToVideoTrimmingActivity(data: String) {
        if (trimType == 0) {
            TrimVideo.activity(data) //                  .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                .setDestination("/storage/emulated/0/DCIM/TESTFOLDER")
                .setMinDuration(Constants.App.Duration.ONE_SECOND)
                .start(this)
        }
    }

    private fun onVideoTrimmingSuccess(videoFile: File) {
        val coverImageFile: File? =
            MediaHelper.resizeImage(
                activity = this@AddJournalActivity,
                bitmap = MediaHelper.getCoverImageFromVideo(
                    context = this@AddJournalActivity,
                    videoUri = Uri.fromFile(videoFile)
                ),
                maxSize = AppHelper.getMaxImageSizeVideoOriginal(),
                compressionQuality = AppHelper.getImageCompressionQualityVideoOriginal()
            )

        if (coverImageFile == null) {
            showToast(getString(R.string.unable_to_process_video))
            return
        }

        this.videoFile = videoFile
        this.coverImageFile = coverImageFile

        setImage(Uri.fromFile(coverImageFile))

        callVideoCompressionProcess()
    }

    /**
     * Here compressing the video
     */
    private fun callVideoCompressionProcess() {
        pbLoadMore.visibility = View.VISIBLE
        val compressedVideoDirectory =
            MediaHelper.getVideoTempDirectory(context = this@AddJournalActivity)

        if (compressedVideoDirectory == null) {
            videoCompressionStatus = true
            return
        }

        videoCompressionStatus = false

        MediaHelper.compressVideo(
            context = this@AddJournalActivity,
            inputFile = videoFile!!,
            outputDirectory = compressedVideoDirectory,
            videoCompressionListener = videoCompressionListener
        )
    }

    private val videoCompressionListener: VideoCompressionListener =
        object : VideoCompressionListener {
            override fun onVideoCompressionSuccess(compressedVideoFile: File) {
                Log.d(TAG, "videoCompressionListener: onSuccess")

                pbLoadMore.visibility = View.GONE
                this@AddJournalActivity.compressedVideoFile = compressedVideoFile
                this@AddJournalActivity.videoCompressionStatus = true
            }

            override fun onVideoCompressionFailure() {
                Log.d(TAG, "videoCompressionListener: onFailure")

                this@AddJournalActivity.compressedVideoFile = videoFile
                this@AddJournalActivity.videoCompressionStatus = true
            }
        }

    /**
     * When successful response or data retrieved from create journal Api
     *
     * @param response is successful response from Api
     */
    override fun onSuccessResponse(response: CommonResponse) {
        if (this@AddJournalActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                showToast(response.message)

                startActivity(Intent(this, JournalListingActivity::class.java))
                finish()
            }
        }
    }

    /**
     * When error occurred in getting successful response of create journal Api.
     *
     * @param errorResponse for Error message
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@AddJournalActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                responseFailure(errorResponse)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.App.RequestCode.CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFile()
                    DialogChooseImage(
                        this@AddJournalActivity,
                        file!!,
                        showOptionForVideo = true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.JOURNAL_PICS -> {

                    }

                    Constants.App.RequestCode.CAMERA -> beginCrop(Uri.fromFile(file))

                    Crop.REQUEST_CROP -> {
                        val imageUri = Crop.getOutput(data)
                        Log.d(TAG, "Image Uri --> $imageUri")
                        setImage(imageUri!!)
                        file = File(getRealPath(this, imageUri)!!)
                        isImageSelected = true
                    }

                    Constants.App.RequestCode.GALLERY -> beginCrop(data!!.data)

                    Constants.App.RequestCode.SELECT_VIDEO -> {
                        Log.d(TAG, "SELECT_VIDEO--> ${data!!.data}")

                        goToVideoTrimmingActivity(data = data.data.toString())
                    }

                    Constants.App.RequestCode.VIDEO_TRIMMER_REQ_CODE -> {
                        val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data))

                        val filepath = uri.toString()
                        val file = File(filepath)
                        val length = file.length()
                        Log.d(TAG, "Video size:: " + length / 1024)

                        onVideoTrimmingSuccess(videoFile = file)
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
//                finish()
            }
        }
    }

    override fun onBackPressed() = finish()

    override fun onDestroy() {
        super.onDestroy()

        file = null
        videoFile = null
    }

}



