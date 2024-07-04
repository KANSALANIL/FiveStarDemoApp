package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.mvp.model.request.RequestChangePassword
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.ChangePasswordPresenter
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import kotlinx.android.synthetic.main.layout_toolbar.*

class ChangePasswordActivity : BaseActivity(), ChangePasswordPresenter.ResponseCallBack {

    private var requestChangePassword = RequestChangePassword()
    private lateinit var presenter: ChangePasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        initUi()
        initPresenter()

        init()
        clickListener()
    }

    private fun initUi() {
        tvTitle.text = getString(R.string.change_password)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ChangePasswordPresenter(this)
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        initPaswrdVisibilityForEditText(
            etPassword = etPaswrd,
            ivPaswrdVisibility = ivOldPaswrdVisibility
        )

        initPaswrdVisibilityForEditText(
            etPassword = etNewPaswrd,
            ivPaswrdVisibility = ivNewPaswrdVisibility
        )

        initPaswrdVisibilityForEditText(
            etPassword = etConfirmPaswrd,
            ivPaswrdVisibility = ivConfirmPaswrdVisibility
        )
    }

    /**
     * Click event on menu and save button
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        ivOldPaswrdVisibility.setOnClickListener(clickListener)
        ivNewPaswrdVisibility.setOnClickListener(clickListener)
        ivConfirmPaswrdVisibility.setOnClickListener(clickListener)
        btnSave.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivOldPaswrdVisibility -> callShowHidePaswrdVisibilityForEditText(
                    etPassword = etPaswrd,
                    ivPaswrdVisibility = ivOldPaswrdVisibility
                )

                R.id.ivNewPaswrdVisibility -> callShowHidePaswrdVisibilityForEditText(
                    etPassword = etNewPaswrd,
                    ivPaswrdVisibility = ivNewPaswrdVisibility
                )

                R.id.ivConfirmPaswrdVisibility -> callShowHidePaswrdVisibilityForEditText(
                    etPassword = etConfirmPaswrd,
                    ivPaswrdVisibility = ivConfirmPaswrdVisibility
                )

                R.id.ivBackMenu -> onBackPressed()

                R.id.btnSave -> clickedSave()
            }
    }

    /**
     * It is called when user click on save button
     */
    private fun clickedSave() {
        if (!validateDetail()) return

        if (isInternetConnected(shouldShowToast = true))
            fetchAllFieldsData()
    }

    /**
     * Check validation for change password parameters
     *
     * @return status for success/failure
     */
    private fun validateDetail(): Boolean {
        var valid = true

        etPaswrd.error = when {
            etPaswrd.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            etPaswrd.length() < resources.getInteger(R.integer.pass_min) -> {
                valid = false
                getString(R.string.please_enter_valid_pass)
            }

            etPaswrd.length() > resources.getInteger(R.integer.pass_max) -> {
                valid = false
                getString(R.string.pass_is_too_long)
            }

            else -> null
        }

        etNewPaswrd.error = when {
            etNewPaswrd.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            etNewPaswrd.length() < resources.getInteger(R.integer.pass_min) -> {
                valid = false
                getString(R.string.please_enter_valid_pass)
            }

            etNewPaswrd.length() > resources.getInteger(R.integer.pass_max) -> {
                valid = false
                getString(R.string.pass_is_too_long)
            }

            else -> null
        }

        etConfirmPaswrd.error = when {
            etConfirmPaswrd.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            etConfirmPaswrd.length() < resources.getInteger(R.integer.pass_min) -> {
                valid = false
                getString(R.string.please_enter_valid_pass)
            }

            etConfirmPaswrd.length() > resources.getInteger(R.integer.pass_max) -> {
                valid = false
                getString(R.string.pass_is_too_long)
            }

            else -> null
        }

        if (etConfirmPaswrd.text.toString() != etNewPaswrd.text.toString()) {
            etConfirmPaswrd.error = getString(R.string.confirm_pass_is_not_match_with_new)
            valid = false
        }
        if (etNewPaswrd.text.toString() == etPaswrd.text.toString()) {
            etNewPaswrd.error = getString(R.string.pass_is_match_with_old)
            valid = false
        }

        return valid
    }

    /**
     * fetch data from fields entered by user for change password parameter
     */
    private fun fetchAllFieldsData() {
        requestChangePassword.apply {
            oldPassword = etPaswrd.text.toString()
            password = etNewPaswrd.text.toString()
            passwordConfirmation = etConfirmPaswrd.text.toString()
        }
        hitApiToChangePassword()
    }

    /**
     * call for api to change password
     */
    private fun hitApiToChangePassword() {
        showProgressDialog("")
        presenter.apiPutForChangePassword(
            SharedPreferencesHelper.getAuthToken(),
            requestChangePassword
        )
    }

    /**
     * When successful response or data retrieved from change password api
     *
     * @param msg success message
     */
    override fun onChangePasswordResponseSuccess(msg: CommonResponse) {
        if (this@ChangePasswordActivity.baseContext != null) {
            runOnUiThread {
                if (this@ChangePasswordActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(msg.message)
                    finish()
                }
            }
        }
    }

    /**
     * When error occurred in change password Api.
     *
     * @param errorResponse for Error message
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@ChangePasswordActivity.baseContext != null) {
            runOnUiThread {
                if (this@ChangePasswordActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() = finish()
}
