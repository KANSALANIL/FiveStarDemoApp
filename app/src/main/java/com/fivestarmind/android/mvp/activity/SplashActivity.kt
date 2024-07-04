package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.fivestarmind.android.BuildConfig
import com.fivestarmind.android.R
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.LetsGoDataResponse
import com.fivestarmind.android.mvp.model.response.LetsGoResponse
import com.fivestarmind.android.mvp.presenter.LetsGoPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lets_go.progressBar
import kotlinx.android.synthetic.main.activity_splash.ivLogo
import me.leolin.shortcutbadger.ShortcutBadger


class SplashActivity : BaseActivity(), LetsGoPresenter.ResponseCallBack {
    lateinit var handler: Handler
    private lateinit var letsGoPresenter: LetsGoPresenter
    private var letsGoDataResponse: LetsGoDataResponse? = null
    var appUpdateManager: AppUpdateManager? = null
    var mIntent:Intent?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d(TAG, "SharedPref_AuthToken = " + SharedPreferencesHelper.getAuthToken())
        initPresenter()
        handler = Handler(Looper.getMainLooper())


        if (!BuildConfig.DEBUG) {
            checkForAppUpdate()

        } else {

            clearFcmNotification()

        }

        try {
            val versionName: String = baseContext.getPackageManager()
                .getPackageInfo(baseContext.getPackageName(), 0).versionName


            Log.d(TAG,"version Name:- "+ versionName)
        } catch (e: PackageManager.NameNotFoundException) {
           // e.printStackTrace()
            Log.d(TAG,"versionName Exception:- "+  e.printStackTrace().toString())

        }

    }


    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        letsGoPresenter = LetsGoPresenter(this)
    }


    private fun callOrganisationLogoApi() {
        /* if (isInternetConnected(shouldShowToast = true)) {
      }*/

        //  showProgressDialog("Please wait...")
        //  letsGoPresenter.getOrganisationLogo(SharedPreferencesHelper.getAppID().toInt())

    }

   /* override fun onNewIntent(intent: Intent?) {
        mIntent = intent
        super.onNewIntent(mIntent)
    }*/


    override fun onNewIntent(intent: Intent?) {
        setIntent(intent)
        super.onNewIntent(intent)

    }

    /**
     * It is called after 2 sec
     */
    private fun callLetsGo() {
        //membsership_code
        //get Intent From DeepLink to open SignIn Activity
        val message = intent.getStringExtra("message")
        val type = intent.getStringExtra("type")

        if (intent != null && intent.data.toString().contains(Constants.App.MEMBERSHIP_CODE)
        ) {
            val uri = Uri.parse(intent.data.toString())
            val membsership_code = uri.getQueryParameter(Constants.App.MEMBERSHIP_CODE)

            startActivity(Intent(this@SplashActivity, SignInActivity::class.java).putExtra(Constants.App.MEMBERSHIP_CODE,membsership_code))
            finish()

        } else if(message!=null && message.contains("New content has been uploaded for you")){
            val title = intent!!.getStringExtra("title")
            val notificationId = intent!!.getStringExtra("notification_id")
            SharedPreferencesHelper.saveNotificationId(notificationId!!)

            startActivity(Intent(this@SplashActivity, HomeTabsActivity::class.java))


        }
        else if(type!=null && type.contains("THREAD_SINGLE_MESSAGE")){
            val title = intent!!.getStringExtra("title")
            val sender_id = intent!!.getStringExtra("sender_id")
            SharedPreferencesHelper.saveMessageSenderId(sender_id!!)

            startActivity(Intent(this@SplashActivity, HomeTabsActivity::class.java))


        }



        else {

            if (SharedPreferencesHelper.getAuthToken().isEmpty()) {
                startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
                //startActivity(Intent(this@SplashActivity, LetsGoActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, LetsGoActivity::class.java))
                finish()
            }
        }

    }

    override fun onBackPressed() {
        handler.removeCallbacksAndMessages(null)
        super.onBackPressed()
    }

    override fun onResponseSuccess(response: LetsGoResponse) {
        if (this@SplashActivity.baseContext != null) {
            runOnUiThread {
                if (this@SplashActivity.baseContext != null) {
                    hideProgressDialog()

                    response.data?.apply {
                        letsGoDataResponse = this
                        updateDataInUI()

                    }
                }
            }
        }
    }

    /*  override fun attachBaseContext(newBase: Context) {
          super.attachBaseContext(ContextWrapper(newBase.setAppLocale("es")))
      }*/


    override fun onAppTimeResponseSuccess(commonResponse: CommonResponse) {

    }

    override fun onFailureResponse(message: String) {

        if (this@SplashActivity.baseContext != null) {
            runOnUiThread {
                if (this@SplashActivity.baseContext != null) {

                    hideProgressDialog()

                }
            }
        }
    }

    private fun updateDataInUI() {

        progressBar.visibility = View.VISIBLE
        Picasso.get()
            .load(ApiClient.BASE_URL_PROFILE + letsGoDataResponse!!.logoImage)
            .placeholder(R.drawable.app_logo_placeholder)
            //   .into(ivOrganisationLogo, object : Callback {
            .into(ivLogo, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBar.visibility = View.GONE
                    ivLogo.setImageDrawable(getDrawable(R.drawable.app_logo_placeholder))
                }
            })
    }

    override fun onPause() {
        super.onPause()
        hideProgressDialog()
    }


    fun clearFcmNotification() {
        ShortcutBadger.removeCount(baseContext)

        SharedPreferencesHelper.saveBadgeCount(0)
        ShortcutBadger.applyCount(baseContext, 0)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            callLetsGo()

        }, 2000)
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

        }
        )
    }

    private fun handleAppUpdateInfo(appUpdateInfo: AppUpdateInfo?) {
        when (appUpdateInfo?.updateAvailability()) {
            UpdateAvailability.UPDATE_AVAILABLE -> {
                Log.d(TAG, " Update Available")

                startUpdateFlow(appUpdateInfo)
            }

            UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                Log.d(TAG, "No Update Available")
                clearFcmNotification()
            }

            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                Log.d(TAG, "No Update In Progress Available")

                startUpdateFlow(appUpdateInfo)
            }

            else -> {
                Log.d(TAG, "No Update Available")
                clearFcmNotification()
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

                clearFcmNotification()

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
}




