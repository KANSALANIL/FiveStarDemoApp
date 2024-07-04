package com.fivestarmind.android.mvp.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.mvp.dialog.DialogChooseImage
import com.fivestarmind.android.mvp.dialog.DialogDeleteAccount
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.UserLoginResponseModel
import com.fivestarmind.android.mvp.model.response.UserProfilleResponseModel
import com.fivestarmind.android.mvp.presenter.ProfilePresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.io.ByteArrayOutputStream
import java.io.File


class ProfileActivity : BaseActivity(), ProfilePresenter.ResponseCallBack,DialogSesstionExpiredListener {

    lateinit var presenter: ProfilePresenter
    private var imageFile: File? = null
    private var isImageSelected = false
    private var databaseHelper = DatabaseHelper(this)
    var userInfoModel: UserLoginResponseModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setToolbarTitle()
        initEditTextFilters()

        initPresenter()
        clickListener()
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.profile)
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etName.filters = etName.filters + this
            etNumber.filters = etNumber.filters + this
            etClubCollege.filters = etClubCollege.filters + this
            etState.filters = etState.filters + this
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        userInfoModel = UserLoginResponseModel()

        presenter = ProfilePresenter(this)
        checkIsLoggedIn()
    }

    /**
     * Check for user logged in or not
     */
    private fun checkIsLoggedIn() {
        when (AppHelper.isUserLoggedIn()) {
            true -> hitApiToGetProfile()
            false -> callToOpenLoginActivity()
        }
    }

    /**
     * Click event on view ivMenu and btnSave
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        btnSave.setOnClickListener(clickListener)
        //  btnDeleteAccount.setOnClickListener(clickListener)
        ivProfile.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

                R.id.btnSave -> clickedSave()

                //   R.id.btnDeleteAccount -> clickedDeleteAccount()

                R.id.ivProfile -> checkCameraPermission()
            }
    }

    /**
     * It is called when user clicked on save button
     */
    private fun clickedSave() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true))
            hitApiToUpdateProfile()
    }

    /**
     * It is called when user clicked on deleteAccount button
     */
    private fun clickedDeleteAccount() {
        DialogDeleteAccount(
            this,
            context = this@ProfileActivity
        ).show()
    }

    /**
     * Check Validation for update profile
     *
     * @return status for success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

        if (!isImageSelected) {
            showToast(getString(R.string.please_upload_your_image))
            valid = false
        }

        /*  etName.error = when {
              etName.text!!.isBlank() -> {
                  valid = false
                  getString(R.string.required)
              }*/

        //  else -> null
        //}

        /*  etEmail.error = when {
              etEmail.text!!.isBlank() -> {
                  valid = false
                  getString(R.string.required)
              }

              !AppHelper.isValidEmail(etEmail.text!!.trim().toString()) -> {
                  valid = false
                  getString(R.string.invalid_format)
              }

              else -> null
          }*/

        /*etNumber.error = when {
            etNumber.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            etNumber.length() < resources.getInteger(R.integer.phone_min) -> {
                valid = false
                getString(R.string.length_of_phone_number)
            }

            else -> null
        }*/

        return valid
    }

    /**
     * Call for api to get profile
     */
    private fun hitApiToGetProfile() {
        showProgressDialog("")
        presenter.apiToGetProfile(token = "Bearer " + SharedPreferencesHelper.getAuthToken(), appId = SharedPreferencesHelper.getAppID().toInt())
    }

    /**
     * Call for api to update profile
     */
    private fun hitApiToUpdateProfile() {

        if (imageFile != null) {

            showProgressDialog("")

            presenter.apiHitToUpdateWithProfileImage(
                token ="Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId= SharedPreferencesHelper.getAppID().toInt(),
                file = imageFile!!,
                userName = etName.text.toString()
            )
        }else{

            showProgressDialog("")

            presenter.apiHitToUpdateWithOutProfileImage(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                userName = etName.text.toString(),
            )
        }

    }

    /**
     * Set details from update profile response
     */
    private fun setDetails(response: UserProfilleResponseModel) {

        SharedPreferencesHelper.storeUserName(name = response.data!!.name!!)
        if (response.data.profileImg!=null)
            SharedPreferencesHelper.storeUserImage(image = response.data.profileImg!!)
        response.data.apply {


            updateProfileImage(image = response.data.profileImg)
            updateName(name = name!!)
            updateEmail(email = email!!)
            //  updatePhoneNumber(userPhone = userPhone)
            //   updateClubCollegeView(clubCollege = clubCollege)
            //updateStateView(state = state)
        }
    }

    //ApiClient.BASE_URL_MEDIA+"profile"
    private fun updateProfileImage(image: String?) {
        if (null != image) {
            progressBar.visibility = View.VISIBLE
            Picasso.get()
                .load(ApiClient.BASE_URL_PROFILE + image)
                .resize(160, 160)
                .into(ivProfile, object : Callback {
                    override fun onSuccess() {
                        isImageSelected = true
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                    }
                })
        } else ivProfile.setImageDrawable(resources.getDrawable(R.drawable.ic_user_placeholder))
    }

    private fun updateName(name: String) {
        etName.setText(name)
    }

    private fun updateEmail(email: String) {
        etEmail.text = email
    }

    private fun updatePhoneNumber(userPhone: String?) {
        if (userPhone == null) etNumber.hint = getString(R.string.hint_number)
        else etNumber.setText(userPhone)
    }

    private fun updateClubCollegeView(clubCollege: String) {
        etClubCollege.setText(clubCollege)
    }

    private fun updateStateView(state: String) {
        etState.setText(state)
    }

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PROFILE
        )
    }

    private fun showPositiveAlert(message: String?) {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@ProfileActivity,
            requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
            message = message,
            positiveButtonText = getString(R.string.ok),
            listener = this@ProfileActivity
        )
        hitApiToGetProfile()

        positiveAlertDialog.show()
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
            .into(ivProfile)
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
     * When success response from api get profile
     *
     * @param response Success Response
     */
    override fun onProfileResponseSuccess(response: UserProfilleResponseModel) {

        if (this@ProfileActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                setDetails(response)


            }
        }
    }

    /**
     *When success response from api update profile
     *
     * @param response Success response
     */
    override fun onProfileUpdateResponse(response: CommonResponse) {
        if (this@ProfileActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                // setDetails(response.user)

                showPositiveAlert(response.message)
            }
        }
    }

    /**
     * When error occurred in success response of api
     * get profile
     * update profile
     *
     * @param errorResponse Error response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@ProfileActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                if (errorResponse.message.equals("Unauthenticated.")){

                    val dialogSessionExpired = DialogSessionExpired(
                        listener = this,
                        message = getString(R.string.session_expired),
                        context = this,

                        )

                    dialogSessionExpired.show()

                    //  showSestionExpiredtDialog("You are logged in with another device to continue with this device you need to login again",this,this)

                }else{

                    responseFailure(errorResponse)

                }
            }
        }
    }

    override fun onResponseUpdateProfileFailure(message: String) {
        if (this@ProfileActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                //   showToast(commonResponse.message)
                if (message.equals("Unauthorized")) {
                    showToast("You are logged in with another device to continue with this device you need to login again")
                }else {
                    showToast(message)
                }
            }
        }
    }

    /**
     * It is called when user clicked on yes button for delete account
     */
    private fun clickedYesForDeleteAccount() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
           // presenter.hitApiForDeleteAccount(SharedPreferencesHelper.getAuthToken())
        }
    }

    /**
     * When successful response or data retrieved from DeleteAccount Api
     *
     * @param response is successful response from Api
     */
    override fun onDeleteAccountResponseSuccess(response: CommonResponse) {
        SharedPreferencesHelper.removeAllDetails()
        databaseHelper.removeAllData()

        if (this@ProfileActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProfileActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)

                    callToLoginActivity()
                }
            }
        }
    }

    private fun callToLoginActivity() {
//        startActivity(Intent(this, SignInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
        startActivity(Intent(this@ProfileActivity, SplashActivity::class.java))
        finishAffinity()
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
                    DialogChooseImage(
                        this@ProfileActivity,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.PROFILE -> hitApiToGetProfile()

                    Constants.App.RequestCode.CAMERA -> beginCrop(Uri.fromFile(imageFile))

                    Crop.REQUEST_CROP -> {
                        val imageUri = Crop.getOutput(data)
                        Log.d(TAG, "Image Uri --> $imageUri")
                        // setImage(imageUri!!)
                        // imageFile = File(getRealPath(this,  imageUri))

                        isImageSelected = true

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
                finish()
            }
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

            else ->
                super.onDialogEventListener(
                    dialogEventType = dialogEventType,
                    requestCode = requestCode,
                    model = model
                )
        }
    }

    override fun onDialogPositiveEvent(requestCode: Int, model: Any?) {
        Log.d(TAG, "onDialogPositiveEvent: requestCode- $requestCode")

        when (requestCode) {
            Constants.App.RequestCode.POSITIVE_ALERT ->
                finish()

            Constants.App.RequestCode.DELETE_ACCOUNT -> clickedYesForDeleteAccount()

            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
        }
    }

    override fun onBackPressed() = finish()

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


        // imageFile = File(getRealPath(this,  getImageUri(rotatedImg)!!))
        return rotatedImg
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

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }
    }

    fun clearLocalDB(){
        onSessionExpiredClearLocalDB(databaseHelper)
        startActivity(Intent(this@ProfileActivity, SignInActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK   or Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()

    }
}
