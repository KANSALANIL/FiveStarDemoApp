package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.WebViewType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.RememberMeModel
import com.fivestarmind.android.mvp.model.request.SignInRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.LetsGoDataResponse
import com.fivestarmind.android.mvp.model.response.LetsGoResponse
import com.fivestarmind.android.mvp.model.response.UserLoginResponseModel
import com.fivestarmind.android.mvp.presenter.LetsGoPresenter
import com.fivestarmind.android.mvp.presenter.SignInPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.fivestarmind.android.utils.AsteriskPasswordTransformationMethod
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_lets_go.progressBar
import kotlinx.android.synthetic.main.activity_sign_in.btnLogin
import kotlinx.android.synthetic.main.activity_sign_in.cbPrivacyPolicy
import kotlinx.android.synthetic.main.activity_sign_in.cbRememberMe
import kotlinx.android.synthetic.main.activity_sign_in.etEmail
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import kotlinx.android.synthetic.main.activity_sign_in.ivBack
import kotlinx.android.synthetic.main.activity_sign_in.ivLogo
import kotlinx.android.synthetic.main.activity_sign_in.ivPaswrdVisibility
import kotlinx.android.synthetic.main.activity_sign_in.terms_and_text
import kotlinx.android.synthetic.main.activity_sign_in.tvForgotPass
import kotlinx.android.synthetic.main.activity_sign_in.tvSignUp


class SignInActivity : BaseActivity(), SignInPresenter.ResponseCallBack,
    LetsGoPresenter.ResponseCallBack {

    override val TAG = SignInActivity::class.java.simpleName
    private lateinit var letsGoPresenter: LetsGoPresenter
    private var letsGoDataResponse: LetsGoDataResponse? = null
    private lateinit var presenter: SignInPresenter
    private var signInRequestModel = SignInRequestModel()

    private var rememberMeModel: RememberMeModel? = null
    private var fcmToken: String = ""
    var getMemberShipCode: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        getFCMToken()
        initPresenter()
        initEditTextFilters()
        init()
        getIntentFromDeepLink()
        // callOrganisationLogoApi()
        setClickListener()


    }

    private fun getIntentFromDeepLink() {
        val getMemberShipCode = intent.getStringExtra(Constants.App.MEMBERSHIP_CODE)

        if (!getMemberShipCode.isNullOrEmpty()&& SharedPreferencesHelper.getAuthToken().isEmpty()) {
            etPassword.setText(getMemberShipCode)
            cbRememberMe.isChecked = true
            cbPrivacyPolicy.isChecked = false
        } else if(!getMemberShipCode.isNullOrEmpty() && !SharedPreferencesHelper.getAuthToken().isNullOrEmpty() && getMemberShipCode.equals(SharedPreferencesHelper.getRememberMeModel()?.paswrd)) {
          Log.d(TAG,"Password: "+SharedPreferencesHelper.getRememberMeModel()?.paswrd +" : getMemberShipCode="+getMemberShipCode)
            //SharedPreferencesHelper.getRememberMeModel()?.paswrd
            etPassword.setText(getMemberShipCode)
            cbRememberMe.isChecked = true
            cbPrivacyPolicy.isChecked = true
            afterLoginSuccess()
        } else if(!getMemberShipCode.isNullOrEmpty() && !SharedPreferencesHelper.getAuthToken().isNullOrEmpty() && !getMemberShipCode.equals(SharedPreferencesHelper.getRememberMeModel()?.paswrd)) {
            etPassword.setText(getMemberShipCode)
            cbRememberMe.isChecked = true
            cbPrivacyPolicy.isChecked = false

        } else {
            //Here update Ui with local store value in UI
            updateDataInUI()

        }

    }


    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            fcmToken = task.result.toString()
            Log.d(TAG, "FCM Token: " + fcmToken.toString())
        })
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        letsGoPresenter = LetsGoPresenter(this)
        presenter = SignInPresenter(this)
    }

    /**
     * call organisation logo api
     */
    private fun callOrganisationLogoApi() {
        if (isInternetConnected(shouldShowToast = true))
            showProgressDialog("Please wait...")
        //   letsGoPresenter.getOrganisationLogo(24)
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etEmail.filters = etEmail.filters + this
            etPassword.filters = etPassword.filters + this
        }
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        etPassword.filters = arrayOf<InputFilter>(AllCaps())

        etEmail.transformationMethod = AsteriskPasswordTransformationMethod()
        initPaswrdVisibilityForEditText(
            etPassword = etPassword,
            ivPaswrdVisibility = ivPaswrdVisibility
        )
        termsAndPrivacyPolicy()
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        btnLogin.setOnClickListener(clickListener)
        tvForgotPass.setOnClickListener(clickListener)
        tvSignUp.setOnClickListener(clickListener)
        ivBack.setOnClickListener(clickListener)
        ivPaswrdVisibility.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivPaswrdVisibility -> callShowHidePaswrdVisibilityForEditText(
                    etPassword = etPassword,
                    ivPaswrdVisibility = ivPaswrdVisibility
                )

                R.id.btnLogin -> clickedLogIn()

                R.id.tvForgotPass -> clickedForgotPaswrd()

                R.id.tvSignUp -> clickedSignUp()

                R.id.ivBack -> onBackPressed()
            }
    }

    /**
     * Here updating views w.r.t. data
     */
    private fun updateDataInUI() {
        SharedPreferencesHelper.getRememberMeModel()?.apply {
            if (rememberMeStatus) {
                updateRememberMeCheck(status = true)
                updateEmailEditText(emailId = email)
                updatePasswordEditText(paswrd = paswrd)
            }
            if (termsAndPrivacyPolicy) {
                updatePrivacyPolicyCheck(status = true)
            }
        }
    }

    /**
     *  This is called when user tap on Forgot Password Text view
     */
    private fun clickedForgotPaswrd() {
        startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java))
        resetEditTexts()
    }

    /**
     *  This is called when user tap on SignUp Text view
     */
    private fun clickedSignUp() {
        startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        resetEditTexts()
    }

    /**
     *  This is called when user tap on SignIn Button
     */
    private fun clickedLogIn() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true)) {

            if (cbPrivacyPolicy.isChecked)
                callSignInProcess()
            else Toast.makeText(
                this@SignInActivity,
                "Please select Terms & Conditions and Privacy Policy",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true
        //   etEmail.setText("anil11@yopmail.com")
        /* etEmail.error = when {
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

        if (etPassword.text!!.isBlank()) {
            valid = false
            showToast("Please enter your membership code")
        }

        /* etPaswrd.error = when {
             etPaswrd.text!!.isBlank() -> {
                 valid = false
                 getString(R.string.required)
             }*/

        /* etPaswrd.length() < resources.getInteger(R.integer.pass_min) -> {
             valid = false
             getString(R.string.please_enter_valid_pass)
         }

         else -> null
     }*/

        return valid
    }


    private fun callSignInProcess() {

        rememberMeModel = RememberMeModel(
            rememberMeStatus = cbRememberMe.isChecked,
            termsAndPrivacyPolicy = cbPrivacyPolicy.isChecked,
            email = etEmail.text!!.trim().toString(),
            paswrd = etPassword.text!!.trim().toString()
        )

        showProgressDialog("")
        Log.d(TAG, "Fcm_Token__:" + fcmToken)
        signInRequestModel.apply {
            // email = etEmail.text.toString()
            role = "user"
            password = etPassword.text.toString()
            deviceToken = fcmToken
            // rememberMe = cbRememberMe.isChecked
        }

        presenter.hitApiForSignIn(signInRequestModel)
    }

    /**
     * Here updating the remember me checkBox view with data
     *
     * @param status checked status
     */
    private fun updateRememberMeCheck(status: Boolean) {
        cbRememberMe.isChecked = status
    }

    /**
     * Here updating the remember me checkBox view with data
     *
     * @param status checked status
     */
    private fun updatePrivacyPolicyCheck(status: Boolean) {
        cbPrivacyPolicy.isChecked = status
    }


    /**
     * Here updating the Email id editText view with data
     *
     * @param emailId email address of the user for login
     */
    private fun updateEmailEditText(emailId: String?) {
        etEmail.setText(emailId)
    }

    /**
     * Here updating the Paswrd editText view with data
     *
     * @param paswrd paswrd of the user for login
     */
    private fun updatePasswordEditText(paswrd: String?) {
        etPassword.setText(paswrd)
    }

    /**
     * It is called when user successfully logged into the App. Here user login response and rememberMe info is stored.
     *
     * @param userModel userAuthModel which contains userModel
     */
    private fun storeInfoAfterLoginSuccess(
        userModel: UserLoginResponseModel
    ) {
        SharedPreferencesHelper.storeAuthToken(authToken = userModel.token!!)
        SharedPreferencesHelper.storeAppID(appID = userModel.user?.orgUser?.organizationId!!.toString())
        SharedPreferencesHelper.storeUserName(name = userModel.user.name!!)

        if (userModel.user.orgUser.user_subrole!!.size > 0) {
            SharedPreferencesHelper.storeSubRoleName(
                name = userModel.user.orgUser.user_subrole.get(
                    0
                ).name
            )
        }
        if (userModel.user.profileImage != null) {
            SharedPreferencesHelper.storeUserImage(image = userModel.user.profileImage)
        }
        SharedPreferencesHelper.storeUserInfoModel(userInfoModel = userModel)

        SharedPreferencesHelper.storeRememberMeModel(rememberMeModel = rememberMeModel!!)


    }

    /**
     * It is called after user login success
     */
    private fun afterLoginSuccess() {
        if (intent.getStringExtra(Constants.App.MEMBERSHIP_CODE)!=null) {
            startActivity(Intent(this@SignInActivity, LetsGoActivity::class.java))
            finish()
            return
        }
        else if (intent.extras != null) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            //  startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
            startActivity(Intent(this@SignInActivity, LetsGoActivity::class.java))
            finish()
        }


    }

    /**
     * When user get success response from api login
     *
     * @param response Success Response
     */
    override fun onLoginResponseSuccess(response: UserLoginResponseModel) {
        storeInfoAfterLoginSuccess(userModel = response)

        if (this@SignInActivity.baseContext != null) {
            runOnUiThread {
                if (this@SignInActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message!!)

                    afterLoginSuccess()
                }
            }
        }
    }

    /**
     * When error occurred in success response
     *
     * @param errorResponse Error response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@SignInActivity.baseContext != null)
            runOnUiThread {
                if (this@SignInActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
    }

    /**
     * Here resetting the editTexts w.r.t. error, text and focus
     */
    private fun resetEditTexts() {
        etEmail.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etPassword.apply {
            error = null
            text!!.clear()
            clearFocus()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (intent.extras != null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else finish()
    }

    override fun onResponseSuccess(response: LetsGoResponse) {
        if (this@SignInActivity.baseContext != null) {
            runOnUiThread {
                if (this@SignInActivity.baseContext != null) {
                    hideProgressDialog()

                    response.data?.apply {
                        letsGoDataResponse = this

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
                                }
                            }

                            )
                    }
                }
            }
        }
    }

    override fun onAppTimeResponseSuccess(commonResponse: CommonResponse) {
    }

    override fun onFailureResponse(message: String) {
        if (this@SignInActivity.baseContext != null) {
            runOnUiThread {
                if (this@SignInActivity.baseContext != null) {
                    hideProgressDialog()


                }
            }
        }
    }

    private fun termsAndPrivacyPolicy() {
        val SpanString = SpannableString(
            getString(R.string.by_creating_account)
        )

        val teremsAndConditionClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                //  moveToWebActivity("Terms");
                clickedTermsAndConditions()

            }
        }

        val privacyClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                // moveToWebActivity("PrivacyPolicy")
                clickedPrivacyPolicy()
            }
        }

        // Character starting from 37 - 42 is Terms and condition.
        // Character starting from 47 - 61 is privacy policy.

        val startTerms = 15
        val endTerms = 33

        val startPrivacy = 38
        val endPrivacy = 52

        SpanString.setSpan(
            teremsAndConditionClick,
            startTerms,
            endTerms,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        SpanString.setSpan(
            privacyClick,
            startPrivacy,
            endPrivacy,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        SpanString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.dark_brownish_red)),
            startTerms,
            endTerms,
            0
        )

        SpanString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.dark_brownish_red)),
            startPrivacy,
            endPrivacy,
            0
        )

        SpanString.setSpan(UnderlineSpan(), startTerms, endTerms, 0)
        SpanString.setSpan(UnderlineSpan(), startPrivacy, endPrivacy, 0)
        terms_and_text.movementMethod = LinkMovementMethod.getInstance()
        terms_and_text.setText(SpanString, TextView.BufferType.SPANNABLE)
        terms_and_text.isSelected = true

    }

    /**
     * It is called when user clicked on privacy policy view
     */
    private fun clickedPrivacyPolicy() {
        callToOpenWebViewActivity(webViewType = WebViewType.PRIVACY_POLICY)
    }

    /**
     * It is called when user clicked on terms & conditions view
     */
    private fun clickedTermsAndConditions() {
        callToOpenWebViewActivity(webViewType = WebViewType.TERMS_AND_CONDITIONS)
    }

    /**
     * Here is call to open web url
     */
    private fun callToOpenWebViewActivity(webViewType: WebViewType) {
        val intent = Intent(this@SignInActivity, WebViewActivity::class.java)
        intent.putExtra(Constants.App.WEBVIEW_TYPE, webViewType)
        startActivity(intent)
    }
}
