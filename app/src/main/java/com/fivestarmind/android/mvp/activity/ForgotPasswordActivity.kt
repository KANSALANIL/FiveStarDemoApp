package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.mvp.model.request.ForgotPasswordRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.ForgotPasswordPresenter
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity(), ForgotPasswordPresenter.ResponseCallBack,
    DialogListener {

    private lateinit var presenter: ForgotPasswordPresenter
    private var forgotPasswordRequestModel = ForgotPasswordRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initPresenter()
        initEditTextFilters()
        setClickListener()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ForgotPasswordPresenter(this)
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etEmail.filters = etEmail.filters + this
        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        btnSubmit.setOnClickListener(clickListener)
        ivBack.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> clickedBack()

                R.id.btnSubmit -> clickedSubmit()
            }
    }

    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * It is called when user click on submit button
     */
    private fun clickedSubmit() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true))
            callForgotPasswordProcess()
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

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

        return valid
    }

    private fun callForgotPasswordProcess() {
        forgotPasswordRequestModel.apply {
            email = etEmail.text.toString()
        }

        showProgressDialog("")
        presenter.hitApiForForgotPassword(forgotPasswordRequestModel)
    }

    /**
     * When successful response from forgot password api
     *
     * @param msg Success message
     */
    override fun onForgotPasswordSuccess(msg: CommonResponse) {
        if (this@ForgotPasswordActivity.baseContext != null) {
            runOnUiThread {
                if (this@ForgotPasswordActivity.baseContext != null) {
                    hideProgressDialog()
                    clearDetails()
                    showPositiveAlert(msg.message)
                }
            }
        }
    }

    /**
     * When error occurred for getting success response from forgot password api
     *
     * @param errorResponse Error Response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    private fun showPositiveAlert(message: String?) {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@ForgotPasswordActivity,
            requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
            message = message,
            positiveButtonText = getString(R.string.ok),
            listener = this@ForgotPasswordActivity
        )

        positiveAlertDialog.show()
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

            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
        }
    }

    /**
     * Clear field entries
     */
    private fun clearDetails() {
        etEmail.text.clear()
    }

    override fun onBackPressed() = finish()
}
