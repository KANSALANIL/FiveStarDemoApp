package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.mvp.model.request.SuggestionsRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.ContactUsPresenter
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ContactUsActivity : BaseActivity(), ContactUsPresenter.ResponseCallBack, DialogListener {

    override val TAG = ContactUsActivity::class.java.simpleName
    private lateinit var presenter: ContactUsPresenter
    private var suggestionsRequestModel = SuggestionsRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        checkIsUserLoggedIn()

        setToolbarTitle()
        initEditTextFilters()

        initPresenter()
        clickListener()
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
                    Constants.App.RequestCode.CONTACT_US
                )
            }

            else -> {}
        }
    }

    /**
     * Here is updating the title for toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.title_contact_us)
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        emojiFilter.apply {
            etTitle.filters = etTitle.filters + this
            etNote.filters = etNote.filters + this
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ContactUsPresenter(this)
    }

    /**
     * Click event on views ivMenu and btnSubmit
     */
    private fun clickListener() {
        btnSubmit.setOnClickListener(clickListener)
        ivBackMenu.setOnClickListener(clickListener)
    }

    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        onBackPressed()
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()

                R.id.btnSubmit -> clickedSubmit()
            }
    }

    /**
     * It is called when user click on submit view
     */
    private fun clickedSubmit() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true))
            callSubmitSuggestionsProcess()
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

        etTitle.error = when {
            etTitle.text!!.isBlank() -> {
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
     * Get field entries for api call and connecting to server
     */
    private fun callSubmitSuggestionsProcess() {
        suggestionsRequestModel.apply {
            subject = etTitle.text.toString()
            note = etNote.text.toString()
        }

        showProgressDialog("")

        presenter.hitApiToSubmitContactUs(
            SharedPreferencesHelper.getAuthToken(),
            suggestionsRequestModel
        )
    }

    private fun showPositiveAlert(message: String?) {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@ContactUsActivity,
            requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
            message = message,
            positiveButtonText = getString(R.string.ok),
            listener = this@ContactUsActivity
        )

        positiveAlertDialog.show()
    }

    /**
     * When success response from api submit contact us info
     *
     * @param response Success Response
     */
    override fun onContactUsResponseSuccess(response: CommonResponse) {
        if (this@ContactUsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                clearDetails()
                showPositiveAlert(response.message)
            }
        }
    }

    /**
     * When error occurred to submit contact us info
     *
     * @param errorResponse Error response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@ContactUsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                responseFailure(errorResponse)
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

            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.CONTACT_US -> {

                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                finish()
            }
        }
    }

    /**
     * Clear field entries
     */
    private fun clearDetails() {
        etTitle.text.clear()
        etNote.text.clear()
    }

    override fun onBackPressed() = finish()

}
