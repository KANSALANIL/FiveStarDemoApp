package com.fivestarmind.android.mvp.activity

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.fivestarmind.android.*
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.request.AppTimeRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.LetsGoDataResponse
import com.fivestarmind.android.mvp.model.response.LetsGoResponse
import com.fivestarmind.android.mvp.presenter.LetsGoPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_audio_play.ivBack
import kotlinx.android.synthetic.main.activity_lets_go.*
import kotlinx.android.synthetic.main.activity_lets_go.progressBar
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class LetsGoActivity : BaseActivity(), LetsGoPresenter.ResponseCallBack {
    private lateinit var letsGoPresenter: LetsGoPresenter
    private var letsGoDataResponse: LetsGoDataResponse? = null

    var appUpdateManager: AppUpdateManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lets_go)

        letsGoPresenter = LetsGoPresenter(this)
        askNotificationPermission()
        setClickListener()
      //  callOrganisationLogoApi()

    }

    private fun callOrganisationLogoApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("Please wait...")
            letsGoPresenter.getOrganisationLogo(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                SharedPreferencesHelper.getAppID().toInt())

        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        btnLetsGo.setOnClickListener(clickListener)

    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.btnLetsGo -> {
                    clickedLetsGo()
                }
            }
    }

    /**
     * It is called when user click on lets Go button
     */
    private fun clickedLetsGo() {
        if (isInternetConnected(shouldShowToast = true)) {
           // startActivity(Intent(this@LetsGoActivity, HomeActivity::class.java).putExtra(Constants.App.FROM,"LetsGoActivity"))
            startActivity(Intent(this@LetsGoActivity, HomeTabsActivity::class.java))
            finish()
        }

    }


    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.d(TAG, "AppUpdate onSuccess:")

            handleAppUpdateInfo(appUpdateInfo)
        }
        appUpdateInfoTask.addOnFailureListener(object : OnFailureListener {

            override fun onFailure(p0: java.lang.Exception) {
                Log.d(TAG, "AppUpdate onFailureListener:" + p0.message)

            }

        })
    }

    private fun handleAppUpdateInfo(appUpdateInfo: AppUpdateInfo?) {
        when (appUpdateInfo?.updateAvailability()) {
            UpdateAvailability.UPDATE_AVAILABLE -> {
                Log.d(TAG, " Update Available")

                startUpdateFlow(appUpdateInfo)
            }
            UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                Log.d(TAG, "No Update Available")

                setClickListener()
            }

            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                Log.d(TAG, "No Update In Progress Available")

                startUpdateFlow(appUpdateInfo)
            }
            else -> {
                Log.d(TAG, "No Update Available")
                setClickListener()
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                this,
                AppConst.REQUEST_CODE.RC_APP_UPDATE
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (AppConst.REQUEST_CODE.RC_APP_UPDATE == requestCode) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: RESULT_OK")
                setClickListener()
            }
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: RESULT_CANCELED")
                checkForAppUpdate()
            }
            if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                Log.d(TAG, "onActivityResult: RESULT_IN_APP_UPDATE_FAILED")
                checkForAppUpdate()
            }
        }
    }

    override fun onResponseSuccess(response: LetsGoResponse) {

        if (this@LetsGoActivity.baseContext != null) {
            runOnUiThread {
                if (this@LetsGoActivity.baseContext != null) {
                    hideProgressDialog()

                    response.data?.apply {
                        letsGoDataResponse = this
                        updateDataInUI()
                    }
                }
            }
        }
    }

    override fun onAppTimeResponseSuccess(commonResponse: CommonResponse) {

    }

    private fun updateDataInUI() {
        progressBar.visibility = View.VISIBLE
        tvAppName.text = letsGoDataResponse!!.welcome_text
        btnLetsGo.text = letsGoDataResponse!!.welcomeButtonText
        /* var url = ApiClient.BASE_URL_MEDIA + letsGoDataResponse!!.logoImage
         ImageUtils().fetchSVG(this, url, ivBackgroundImage, progressBar)*/

        Picasso.get()
            .load(ApiClient.BASE_URL_PROFILE + letsGoDataResponse!!.logoImage).placeholder(R.drawable.app_logo_placeholder)
            //   .into(ivOrganisationLogo, object : Callback {
            .into(ivAppLogo, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                    ivAppLogo.setImageDrawable(getDrawable(R.drawable.app_logo_placeholder))

                }
            })

        Picasso.get()
            .load(ApiClient.BASE_URL_MEDIA + "profile/" + letsGoDataResponse!!.backgroundImg)
            .into(ivBackgroundImage, object : Callback {

                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                }
            })
    }

    override fun onFailureResponse(message: String) {
        if (this@LetsGoActivity.baseContext != null) {
            runOnUiThread {
                if (this@LetsGoActivity.baseContext != null) {
                    hideProgressDialog()
                }
            }
        }
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                callOrganisationLogoApi()
//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            callOrganisationLogoApi()

        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            /*  Toast.makeText(this, "Notifications Permission Granted", Toast.LENGTH_SHORT)
                  .show()*/

            callOrganisationLogoApi()
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

}
