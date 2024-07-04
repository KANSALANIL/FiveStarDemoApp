package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.request.UpdateSettingsRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SettingsDataResponse
import com.fivestarmind.android.mvp.model.response.SettingsResponse
import com.fivestarmind.android.mvp.presenter.SettingsPresenter
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class SettingsActivity : BaseActivity(), SettingsPresenter.ResponseCallBack {

    private lateinit var settingsPresenter: SettingsPresenter
    private var settingsDataResponse: SettingsDataResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setToolbarTitle()

        initPresenter()
        setClickListener()

        init()
    }

    /**
     * Here is updating the title of the screen
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.settings)
    }

    /**
     * Here is initializing presenter
     */
    private fun initPresenter() {
        settingsPresenter = SettingsPresenter(this)
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        switchEmailNotification.setOnCheckedChangeListener(checkedChangedListener)
        switchPushNotification.setOnCheckedChangeListener(checkedChangedListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()
            }
    }

    /**
     * Checked change event on toggle button views handles here
     */
    private val checkedChangedListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { view, isChecked ->
            if (shouldProceedClick())
                when (view.id) {
                    R.id.switchPushNotification ->
                        changedPushNotificationStatus(status = isChecked)

                    R.id.switchEmailNotification ->
                        changedEmailNotificationStatus(status = isChecked)
                }
        }

    /**
     * Here updating the PushNotificationStatus Switch view
     */
    private fun updateDataInUI() {
        settingsDataResponse?.apply {
            switchPushNotification.apply {
                isEnabled = false
                isChecked = settingsDataResponse?.pushNotifications == 1
                isEnabled = true
            }

            switchEmailNotification.apply {
                isEnabled = false
                isChecked = settingsDataResponse?.emailNotifications == 1
                isEnabled = true
            }
        }
    }

    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        fetchUserSettingsStatus()
    }

    /**
     * Here connecting to server and uses UserSettings API to get settings
     */
    private fun fetchUserSettingsStatus() {
        showProgressDialog("")

        if (isInternetConnected(shouldShowToast = true))
            settingsPresenter.getSettingsStatus(SharedPreferencesHelper.getAuthToken())
    }

    private fun changedPushNotificationStatus(status: Boolean) {
        if (switchPushNotification.isEnabled) {
            val updateSettingsRequestModel = UpdateSettingsRequestModel().apply {
                type = Constants.App.NotificationType.PUSH_NOTIFICATION
                action = if (status) Constants.App.Number.ONE else Constants.App.Number.ZERO
            }

            callToUpdateUserSettings(updateSettingsRequestModel = updateSettingsRequestModel)
        }
    }

    private fun changedEmailNotificationStatus(status: Boolean) {
        if (switchEmailNotification.isEnabled) {
            val updateSettingsRequestModel = UpdateSettingsRequestModel().apply {
                type = Constants.App.NotificationType.EMAIL
                action = if (status) Constants.App.Number.ONE else Constants.App.Number.ZERO
            }

            callToUpdateUserSettings(updateSettingsRequestModel = updateSettingsRequestModel)
        }
    }

    /**
     * Here connecting to server and uses UserSettings Api to update settings
     */
    private fun callToUpdateUserSettings(updateSettingsRequestModel: UpdateSettingsRequestModel) {
        showProgressDialog("")

        if (isInternetConnected(shouldShowToast = true))
            settingsPresenter.updateSettings(
                SharedPreferencesHelper.getAuthToken(),
                updateSettingsRequestModel = updateSettingsRequestModel
            )
    }

    override fun onResponseSuccess(response: SettingsResponse) {
        if (this@SettingsActivity.baseContext != null) {
            runOnUiThread {
                if (this@SettingsActivity.baseContext != null) {
                    hideProgressDialog()

                    response.data?.apply {
                        settingsDataResponse = this
                        updateDataInUI()
                    }
                }
            }
        }
    }

    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@SettingsActivity.baseContext != null) {
            runOnUiThread {
                if (this@SettingsActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() = finish()

}
