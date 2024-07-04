package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.WebViewType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.request.SignUpRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SignInSignUpResponseModel
import com.fivestarmind.android.mvp.presenter.SignUpPresenter
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity(), SignUpPresenter.ResponseCallBack {

    private var fcmToken: String? = null
    private lateinit var presenter: SignUpPresenter
    private var signUpRequestModel = SignUpRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initPresenter()
        initEditTextFilters()

        init()
        setClickListener()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = SignUpPresenter(this)
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etName.filters = etName.filters + this
            etEmail.filters = etEmail.filters + this
            etPaswrd.filters = etPaswrd.filters + this
            etClubCollege.filters = etClubCollege.filters + this
            etState.filters = etState.filters + this
        }
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        initPaswrdVisibilityForEditText(
            etPassword = etPaswrd,
            ivPaswrdVisibility = ivPaswrdVisibility
        )
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        btnRegister.setOnClickListener(clickListener)
        tvAcceptConditions.setOnClickListener(clickListener)
        ivBack.setOnClickListener(clickListener)
        ivPaswrdVisibility.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivPaswrdVisibility -> callShowHidePaswrdVisibilityForEditText(
                    etPassword = etPaswrd,
                    ivPaswrdVisibility = ivPaswrdVisibility
                )

                R.id.btnRegister -> clickedRegister()

                R.id.tvAcceptConditions -> clickedTermsAndConditions()

                R.id.ivBack -> onBackPressed()
            }
    }

    /**
     * It is called when user clicked on terms & conditions view
     */
    private fun clickedTermsAndConditions() {
        val intent = Intent(this@SignUpActivity, WebViewActivity::class.java)
        intent.putExtra(Constants.App.WEBVIEW_TYPE, WebViewType.TERMS_AND_CONDITIONS)
        startActivity(intent)
    }

    /**
     * It is called when user clicked on Register button
     */
    private fun clickedRegister() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true))
            fetchFcmToken()
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

        etName.error = when {
            etName.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etEmail.error = when {
            etEmail.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            !AppHelper.isValidEmail(etEmail.text!!.trim().toString()) -> {
                valid = false
                getString(R.string.invalid_format)
            }

            else -> null
        }

        etPaswrd.error = when {
            etPaswrd.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            etPaswrd.length() < resources.getInteger(R.integer.pass_min) -> {
                valid = false
                getString(R.string.please_enter_valid_pass)
            }

            else -> null
        }

        if (!cbAcceptConditions.isChecked) {
            showToast(getString(R.string.accept_terms_conditions))
            valid = false
        }

        return valid
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                fcmToken = task.result
            } else {

                ErrorResponse().apply {
                    errorDescription = getString(R.string.error_occurred)
                }

            }
        }

    }

    private fun callSignUpProcess() {
        showProgressDialog("")

        signUpRequestModel.apply {
            name = etName.text.toString()
            email = etEmail.text.toString()
            password = etPaswrd.text.toString()
            deviceToken = fcmToken!!
            clubCollege = etClubCollege.text.toString()
            state = etState.text.toString()
            referralCode = etReferralCode.text.toString()
        }
        presenter.hitApiForSignUp(signUpRequestModel)
    }

    /**
     * Clear field entries
     */
    private fun clearDetails() {
        etName.text.clear()
        etEmail.text.clear()
        etPaswrd.text.clear()
        etClubCollege.text.clear()
        etState.text.clear()
    }

    /**
     * When success response from api sign up
     *
     * @param response Success Response
     */
    override fun onSignUpResponseSuccess(response: SignInSignUpResponseModel.SignInSignUpResponseFirstModel) {
        SharedPreferencesHelper.storeAuthToken(authToken = response.data.token)
        //SharedPreferencesHelper.storeUserInfoModel(userInfoModel = response.data)

        if (this@SignUpActivity.baseContext != null) {
            runOnUiThread {
                if (this@SignUpActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)

                    startActivity(
                        Intent(
                            this@SignUpActivity,
                            OnboardingQuestionsActivity::class.java
                        )
                    )
                    finish()
                }
            }
        }
    }

    /**
     * When error occurred in sign up api
     *
     * @param errorResponse Error Response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    override fun onBackPressed() = finish()
}
