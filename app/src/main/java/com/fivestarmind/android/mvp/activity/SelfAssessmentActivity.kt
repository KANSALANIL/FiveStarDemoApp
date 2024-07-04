package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.request.DesignAssessmentRequestModel
import com.fivestarmind.android.mvp.model.response.DesignAssessmentResponseModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.presenter.SelfAssessmentPresenter
import kotlinx.android.synthetic.main.activity_self_assessment.*
import kotlinx.android.synthetic.main.layout_design.*
import kotlinx.android.synthetic.main.layout_develop.*
import kotlinx.android.synthetic.main.layout_display.*
import kotlinx.android.synthetic.main.layout_toolbar.*


class SelfAssessmentActivity : BaseActivity(), SelfAssessmentPresenter.ResponseCallBack {

    private lateinit var presenter: SelfAssessmentPresenter

    // 0 for design assessment,
    // 1 for develop assessment,
    // 2 for display assessment
    private var selfAssessmentType: Int = 0

    private var designAssessmentRequestModel: DesignAssessmentRequestModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_assessment)

        setToolBarTitle()
        initPresenter()

        checkIsLoggedIn()
        setListener()
        init()
    }

    /**
     * Here is the updating the title for the screen
     */
    private fun setToolBarTitle() {
        tvTitle.text = getString(R.string.self_assessment)
    }

    /**
     * Check for user logged in or not
     */
    private fun checkIsLoggedIn() {
        when (AppHelper.isUserLoggedIn()) {
            true -> {
                loadInitialAssessment()
            }

            false -> callToOpenLoginActivity()
        }
    }

    /**
     * Here is initializing the presenter
     */
    private fun initPresenter() {
        presenter = SelfAssessmentPresenter(this)
    }

    /**
     * Here is handling click on views
     */
    private fun setListener() {
        ivBackMenu.setOnClickListener(clickListener)
        clDesign.setOnClickListener(clickListener)
        clDevelop.setOnClickListener(clickListener)
        clDisplay.setOnClickListener(clickListener)
        btnSubmit.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> goBack()

                R.id.clDesign -> onDesignViewSelected()

                R.id.clDevelop -> onDevelopViewSelected()

                R.id.clDisplay -> onDisplayViewSelected()

                R.id.btnSubmit -> clickedSubmit()
            }
    }

    /**
     * Initial UI setup
     */
    private fun loadInitialAssessment() {
        onDesignViewSelected()
    }

    /**
     * Here is initializing views
     */
    private fun init() {

    }

    /**
     * It is called when user click on design view
     */
    private fun onDesignViewSelected() {
        selfAssessmentType = 0

        showDesignView()

        hideDevelopView()
        clearDevelopAssessmentViews()

        hideDisplayView()
        clearDisplayAssessmentViews()
    }

    private fun showDesignView() {
        clDesign.isSelected = true

        layoutDesign.visibility = View.VISIBLE
    }

    private fun hideDesignView() {
        clDesign.isSelected = false

        layoutDesign.visibility = View.GONE
    }

    /**
     * It is called when user click on develop view
     */
    private fun onDevelopViewSelected() {
        selfAssessmentType = 1

        showDevelopView()

        hideDesignView()
        clearDesignAssessmentViews()

        hideDisplayView()
        clearDisplayAssessmentViews()
    }

    private fun showDevelopView() {
        clDevelop.isSelected = true

        layoutDevelop.visibility = View.VISIBLE
    }

    private fun hideDevelopView() {
        clDevelop.isSelected = false

        layoutDevelop.visibility = View.GONE
    }

    /**
     * It is called when user click on display view
     */
    private fun onDisplayViewSelected() {
        selfAssessmentType = 2

        showDisplayView()

        hideDesignView()
        clearDisplayAssessmentViews()

        hideDevelopView()
        clearDevelopAssessmentViews()
    }

    private fun showDisplayView() {
        clDisplay.isSelected = true

        layoutDisplay.visibility = View.VISIBLE
    }

    private fun hideDisplayView() {
        clDisplay.isSelected = false

        layoutDisplay.visibility = View.GONE
    }

    /**
     * It is called when user click on submit view
     */
    private fun clickedSubmit() {
        if (!validateForm()) return

        if (isInternetConnected(shouldShowToast = true))
            callToUpdateAssessmentProcess()
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

        when (selfAssessmentType) {
            AppConst.NUMBER.ZERO -> valid = validateDesignForm()

            AppConst.NUMBER.ONE -> valid = validateDevelopForm()

            AppConst.NUMBER.TWO -> valid = validateDisplayForm()
        }

        return valid
    }

    /**
     * Here validating the field entries entered by the user for design
     *
     * @return status for validation success/failure
     */
    private fun validateDesignForm(): Boolean {
        var valid = true

        etTrainingDays.error = when {
            etTrainingDays.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etFocus.error = when {
            etFocus.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etGoal.error = when {
            etGoal.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        return valid
    }

    /**
     * Here validating the field entries entered by the user for develop
     *
     * @return status for validation success/failure
     */
    private fun validateDevelopForm(): Boolean {
        var valid = true

        etHoursTrained.error = when {
            etHoursTrained.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etHighlights.error = when {
            etHighlights.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etSelfReport.error = when {
            etSelfReport.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        return valid
    }

    /**
     * Here validating the field entries entered by the user for display
     *
     * @return status for validation success/failure
     */
    private fun validateDisplayForm(): Boolean {
        var valid = true

        etGameDays.error = when {
            etGameDays.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etMinutesPlayed.error = when {
            etMinutesPlayed.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        etDisplaySelfReport.error = when {
            etDisplaySelfReport.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        return valid
    }

    private fun callToUpdateAssessmentProcess() {
        designAssessmentRequestModel =
            when (selfAssessmentType) {
                AppConst.NUMBER.ZERO -> updateRequestModelForDesignAssessment()

                AppConst.NUMBER.ONE -> updateRequestModelForDevelopAssessment()

                AppConst.NUMBER.TWO -> updateRequestModelForDisplayAssessment()

                else -> null
            }

        showProgressDialog("")

        if (isInternetConnected(shouldShowToast = true))
            presenter.hitApiToUpdateAssessment(
                designAssessmentRequestModel = designAssessmentRequestModel!!,
                selfAssessmentType = selfAssessmentType
            )
    }

    /**
     * Here is updating the request model for design assessment
     */
    private fun updateRequestModelForDesignAssessment(): DesignAssessmentRequestModel =
        DesignAssessmentRequestModel().apply {
            trainingDays = (etTrainingDays.text.trim().toString()).toLong()
            focus = etFocus.text.trim().toString()
            goal = etGoal.text.trim().toString()
        }

    /**
     * Here is updating the request model for develop assessment
     */
    private fun updateRequestModelForDevelopAssessment(): DesignAssessmentRequestModel =
        DesignAssessmentRequestModel().apply {
            hoursTrained = (etHoursTrained.text.trim().toString()).toLong()
            highlights = etHighlights.text.trim().toString()
            selfReport = etSelfReport.text.trim().toString()
        }

    /**
     * Here is updating the request model for display assessment
     */
    private fun updateRequestModelForDisplayAssessment(): DesignAssessmentRequestModel =
        DesignAssessmentRequestModel().apply {
            gameDays = (etGameDays.text.trim().toString()).toLong()
            minutesPlayed = (etMinutesPlayed.text.trim().toString()).toLong()
            selfReport = etDisplaySelfReport.text.trim().toString()
        }

    /**
     * When successful response or data retrieved from update design assessment Api
     *
     * @param designAssessmentResponseModel is successful response from Api
     */
    override fun onUpdateAssessmentSuccess(designAssessmentResponseModel: DesignAssessmentResponseModel) {
        if (this@SelfAssessmentActivity.baseContext != null) {
            runOnUiThread {
                if (this@SelfAssessmentActivity.baseContext != null) {
                    hideProgressDialog()

                    if (designAssessmentResponseModel.message.isNotEmpty()) {
                        val positiveAlertDialog = PositiveAlertDialog(
                            baseActivity = this@SelfAssessmentActivity,
                            requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
                            message = designAssessmentResponseModel.message,
                            positiveButtonText = getString(R.string.ok),
                            listener = this@SelfAssessmentActivity
                        )

                        positiveAlertDialog.show()
                    }
                }
            }
        }
    }

    /**
     * When error occurred in user payment api
     *
     * @param errorResponse for Error message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@SelfAssessmentActivity.baseContext != null) {
            runOnUiThread {
                if (this@SelfAssessmentActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    /**
     * Here is updating in data in views
     */
    private fun clearViews() {
        when (selfAssessmentType) {
            AppConst.NUMBER.ZERO -> clearDesignAssessmentViews()

            AppConst.NUMBER.ONE -> clearDevelopAssessmentViews()

            AppConst.NUMBER.TWO -> clearDisplayAssessmentViews()
        }
    }

    /**
     * Here is updating data in views for design assessment
     */
    private fun clearDesignAssessmentViews() {
        etTrainingDays.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etFocus.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etGoal.apply {
            error = null
            text!!.clear()
            clearFocus()
        }
    }

    /**
     * Here is updating data in views for develop assessment
     */
    private fun clearDevelopAssessmentViews() {
        etHoursTrained.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etHighlights.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etSelfReport.apply {
            error = null
            text!!.clear()
            clearFocus()
        }
    }

    /**
     * Here is updating data in views for display assessment
     */
    private fun clearDisplayAssessmentViews() {
        etGameDays.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etMinutesPlayed.apply {
            error = null
            text!!.clear()
            clearFocus()
        }

        etDisplaySelfReport.apply {
            error = null
            text!!.clear()
            clearFocus()
        }
    }

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.SELF_ASSESSMENT_SCREEN
        )
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
            Constants.App.RequestCode.POSITIVE_ALERT -> clearViews()

            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.SELF_ASSESSMENT_SCREEN -> loadInitialAssessment()
                }
            }

            Activity.RESULT_CANCELED -> {
                finish()
            }
        }
    }

    /**
     * It is called when user clicked on back icon
     */
    private fun goBack() {
        onBackPressed()
    }
}
