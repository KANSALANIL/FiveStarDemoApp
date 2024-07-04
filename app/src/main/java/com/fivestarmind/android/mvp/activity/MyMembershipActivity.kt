package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.fivestarmind.android.*
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.DateTimeHelper
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.MembershipResponseModel
import com.fivestarmind.android.mvp.presenter.MyMembershipsPresenter
import kotlinx.android.synthetic.main.activity_my_membership.*
import kotlinx.android.synthetic.main.layout_toolbar.*


class MyMembershipActivity : BaseActivity(), MyMembershipsPresenter.ResponseCallBack {

    private lateinit var presenter: MyMembershipsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_membership)

        setToolBarTitle()
        initPresenter()

        init()
        setListener()
    }

    /**
     * Here is the updating the title for the screen
     */
    private fun setToolBarTitle() {
        tvTitle.text = getString(R.string.my_memberships)
    }

    /**
     * Here is initializing the presenter
     */
    private fun initPresenter() {
        presenter = MyMembershipsPresenter(this@MyMembershipActivity)
    }

    /**
     * Here is handling click on views
     */
    private fun setListener() {
        ivBackMenu.setOnClickListener(clickListener)
        btnCancelSubscription.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()

                R.id.btnCancelSubscription -> clickedCancelSubscription()
            }
    }

    /**
     * It is called when user clicked on back icon
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * It is called when user click on cancel subscription button
     */
    private fun clickedCancelSubscription() {
        callCancelSubscriptionProcess()
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.getUserMembership(AppHelper.getCurrentTimestampInSeconds())
        }
    }

    /**
     * Here is updating data w.r.t views
     */
    private fun updateDataInUI(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        membershipResponseModel?.data?.apply {
            updateMembershipType(membershipType = type)
            updatePrice(price = amountPaid.toString())
            updateStartDate(startDate = startDate)
            updateEndDate(endDate = endDate)
            calculateMembershipPeriod(startDate = startDate, endDate = endDate)

            updateStatusForCancelSubscription(isCanceled = isCanceled)
        }
    }

    /**
     * Here is updating the membership type in view
     */
    private fun updateMembershipType(membershipType: String) {
        tvMembershipType.text = membershipType
    }

    /**
     * Here is updating the paid amount for membership in view
     */
    private fun updatePrice(price: String) {
        tvPrice.text = getString(
            R.string.format_price,
            price
        )
    }

    /**
     * Here is updating the start date for membership in view
     */
    private fun updateStartDate(startDate: Long) {
        tvStartDateValue.text = AppHelper.formatCalendarDateForSeconds(seconds = startDate)
    }

    /**
     * Here is updating the end date for membership in view
     */
    private fun updateEndDate(endDate: Long) {
        tvEndDateValue.text = AppHelper.formatCalendarDateForSeconds(seconds = endDate)
    }

    /**
     * Here is calculating membership time period
     */
    private fun calculateMembershipPeriod(startDate: Long, endDate: Long) {
        val monthsCount =
            DateTimeHelper.getMonthsBetweenDatesForSeconds(
                startSeconds = startDate,
                endSeconds = endDate
            )

        Log.d(TAG, "calculateMembershipPeriod 1-- $monthsCount")

        when {
            monthsCount >= AppConst.NUMBER.TWELVE -> updateMembershipTime(getString(R.string.for_twelve_months))

            monthsCount >= AppConst.NUMBER.SIX -> updateMembershipTime(getString(R.string.for_six_months))
        }
    }

    /**
     * Here is updating the membership time period in view
     */
    private fun updateMembershipTime(timePeriod: String) {
        tvMembershipPeriod.text = timePeriod
    }

    /**
     * Here is updating the UI for membership available
     */
    private fun updateUIForEmptyList() {
        llEmptyMsg.visibility = View.VISIBLE
        tvEmptyMsg.text = getString(R.string.no_membership_yet)

        clMembership.visibility = View.INVISIBLE
    }

    /**
     * Here is updating the status for cancel subscription view
     */
    private fun updateStatusForCancelSubscription(isCanceled: Int?) {
        when (isCanceled) {
            AppConst.NUMBER.ONE -> showHideViewForCancelSubscription(status = false)
            else -> showHideViewForCancelSubscription(status = true)
        }
    }

    private fun showHideViewForCancelSubscription(status: Boolean) {
        when (status) {
            true -> showCancelUI()
            false -> showMembershipExpiredUI()
        }
    }

    /**
     * Here is showing view for Cancel
     */
    private fun showCancelUI() {
        btnCancelSubscription.visibility = View.VISIBLE
        tvMembershipExpired.visibility = View.GONE
    }

    /**
     * Here is showing view for membership expire
     */
    private fun showMembershipExpiredUI() {
        tvMembershipExpired.visibility = View.VISIBLE
        btnCancelSubscription.visibility = View.GONE
    }

    private fun callCancelSubscriptionProcess() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.cancelUserMembership()
        }
    }

    /**
     * When successful response or data retrieved from Api get user membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@MyMembershipActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                if (membershipResponseModel.message == getString(R.string.no_membership_yet))
                    updateUIForEmptyList()
                else updateDataInUI(membershipResponseModel = membershipResponseModel)
            }
        }
    }

    /**
     * When successful response or data retrieved from Api get user cancel membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onCancelUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@MyMembershipActivity.baseContext != null) {
            runOnUiThread {
                showMembershipExpiredUI()
                hideProgressDialog()
                showToast(message = membershipResponseModel.message)
            }
        }
    }

    /**
     * When error occurred in response of api get user memberships
     *
     * @param errorResponse for Error Message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@MyMembershipActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyMembershipActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }
}
