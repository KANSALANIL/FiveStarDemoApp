package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.DateTimeHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.MembershipAdapter
import com.fivestarmind.android.mvp.model.request.RequestMembershipModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.MembershipsPresenter
import kotlinx.android.synthetic.main.activity_memberships.*
import kotlinx.android.synthetic.main.layout_toolbar.*


class MembershipsActivity : BaseActivity(), MembershipsPresenter.ResponseCallBack,
    RecyclerViewItemListener {

    override val TAG = MembershipsActivity::class.java.simpleName

    private var membershipAdapter: MembershipAdapter? = null
    private var membershipLayoutManager: LinearLayoutManager? = null
    private var membershipPackagesList: ArrayList<MembershipPackagesResponseModel>? = null

    private var membershipTypeSelectedPackagesModel: MembershipTypePackagesResponseModel? = null
    private var requestMembershipModel = RequestMembershipModel()

    private var coupon = ""

    private lateinit var presenter: MembershipsPresenter
    private var checkProcessAccess = false

    companion object {
        internal val DEVICE_WIDTH =
            (AppController.mInstance!!.resources.displayMetrics.widthPixels)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memberships)

        setToolBarTitle()
        checkIsLoggedIn()

        initData()
        initRecyclerViewWithAdapter()


        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    /**
     * Here is the updating the title for the screen
     */
    private fun setToolBarTitle() {
        tvTitle.text = getString(R.string.memberships)
    }

    /**
     * Check for user logged in or not
     */
    private fun checkIsLoggedIn() {
        when (AppHelper.isUserLoggedIn()) {
            false -> callToOpenLoginActivity()
            else -> {}
        }
    }

    /**
     * Initializing data to initial state
     */
    private fun initData() {
        membershipPackagesList = arrayListOf()
        presenter = MembershipsPresenter(this@MembershipsActivity)

        initUI()
    }

    private fun initUI() {
        topImg.layoutParams.height = (DEVICE_WIDTH).toInt()
    }

    /**
     * Here recyclerViews are setup with it's adapter
     */
    private fun initRecyclerViewWithAdapter() {
        // Here setup is done for membership types list
        membershipAdapter = MembershipAdapter(
            membershipPackagesList = membershipPackagesList!!,
            listener = this@MembershipsActivity,
            activity = this@MembershipsActivity
        )

        membershipLayoutManager =
            LinearLayoutManager(this@MembershipsActivity, RecyclerView.VERTICAL, false)

        rvMemberships.apply {
            layoutManager = this@MembershipsActivity.membershipLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = membershipAdapter
        }
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        checkProcessAccess = false
        clFree.visibility = View.GONE
        presenter.getUserMembership(currentTime = AppHelper.getCurrentTimestampInSeconds())
//        if (SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1) hideMembershipView()
//        else {
//            showMembershipView()
//            hitApiToGetMembershipList()
//        }
    }

    private fun hideMembershipView() {
        rvMemberships.visibility = View.GONE
        clFree.visibility = View.VISIBLE
    }

    private fun showMembershipView() {
        rvMemberships.visibility = View.VISIBLE
        clFree.visibility = View.GONE
    }

    /**
     * Call for api get memberships
     */
    private fun hitApiToGetMembershipList() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            membershipPackagesList!!.clear()
            presenter.getMembershipList()
        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        ivBackMenu.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()
            }
    }

    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * It is called when user applied coupon on membership price
     */
    private fun afterCouponApplied(
        membershipPackagesResponseModel: MembershipPackagesResponseModel?,
        position: Int
    ) {
        if (isInternetConnected(shouldShowToast = true)) {
            val couponCode = membershipPackagesResponseModel?.coupon!!
            coupon = couponCode

            showProgressDialog("")

            presenter.apiToApplyMembershipCoupon(SharedPreferencesHelper.getAuthToken(), couponCode)
        }
    }

    private fun afterMembershipTypeSelected(
        membershipTypePackagesResponseModel: MembershipTypePackagesResponseModel,
        position: Int
    ) {

        membershipTypeSelectedPackagesModel = membershipTypePackagesResponseModel
        when (membershipTypeSelectedPackagesModel?.type) {

            getString(R.string.individual_) -> presenter.getUserMembership(currentTime = AppHelper.getCurrentTimestampInSeconds())
//                membershipTypeSelectedPackagesModel?.apply {
//                DialogIndividualSubscriptionDetail(
//                    this@MembershipsActivity,
//                    this,
//                    context = this@MembershipsActivity
//                ).show()
//            }

            getString(R.string.team_) ->
                callToUpdateMembershipProcess()

            getString(R.string.club_) ->
                callToUpdateMembershipProcess()
        }
    }

    private fun onIndividualSubscriptionDetailClicked(
        model: MembershipTypePackagesResponseModel
    ) {
        presenter.getUserMembership(currentTime = AppHelper.getCurrentTimestampInSeconds())
    }

    private fun callToUpdateMembershipProcess() {
        val intent = Intent(this, MembershipCardDetailActivity::class.java)
        startActivityForResult(intent, Constants.App.RequestCode.MEMBERSHIP_SCREEN)
    }

    private fun afterPaymentTokenSuccess(paymentToken: String) {
        callToUpdateMembershipApiProcess(paymentToken = paymentToken)
    }

    /**
     * Here is connecting to server and using update membership api
     */
    private fun callToUpdateMembershipApiProcess(paymentToken: String) {
        var start_date = 0L
        var end_date = 0L

        if (membershipTypeSelectedPackagesModel?.type == getString(R.string.individual_)) {
            start_date = AppHelper.getCurrentTimestampInSeconds()
            end_date = when (membershipTypeSelectedPackagesModel?.duration) {
                getString(R.string.one_month) ->
                    AppHelper.convertMillisecondsToSeconds(AppHelper.getTimeStampOfComingOneMonth())
                getString(R.string.three_months) ->
                    AppHelper.convertMillisecondsToSeconds(AppHelper.getTimeStampOfComingThreeMonths())
                getString(R.string.six_months) ->
                    AppHelper.convertMillisecondsToSeconds(AppHelper.getTimeStampOfComingSixMonths())
                getString(R.string.twelve_months) ->
                    AppHelper.convertMillisecondsToSeconds(AppHelper.getTimeStampOfComingTwelveMonths())
                else ->
                    AppHelper.convertMillisecondsToSeconds(AppHelper.getTimeStampOfComingTwelveMonths())
            }
        }

        requestMembershipModel.apply {
            type = membershipTypeSelectedPackagesModel?.type!!
            paymentMode = getString(R.string.stripe)
            codes = membershipTypeSelectedPackagesModel?.codes!!
            startDate = start_date
            endDate = end_date
            amountPaid = membershipTypeSelectedPackagesModel?.membershipFee!!
            iapId = membershipTypeSelectedPackagesModel?.id!!
            receipt = ""
            token = paymentToken
            purchaseDate = DateTimeHelper.getCurrentTimeDateTime()
            couponCode = coupon
        }

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.updateMembership(
                requestMembershipModel = requestMembershipModel,
                token = SharedPreferencesHelper.getAuthToken()
            )
        }
    }

    /**
     * Here is updating the UI for empty list
     */
    private fun updateUIForEmptyList() {
        llEmptyMsg.visibility = View.VISIBLE
        tvEmptyMsg.text = getString(R.string.no_memberships_msg)
    }

    /**
     * Here is adding data to list
     */
    private fun addDataToList(listData: ArrayList<MembershipPackagesResponseModel>) {
        val oldSize: Int = membershipPackagesList!!.size
        Log.d(TAG, membershipPackagesList!!.size.toString())
        membershipPackagesList?.addAll(listData)
        membershipAdapter?.notifyItemRangeInserted(oldSize, membershipPackagesList!!.size)

        listData.clear()
    }

    /**
     * Updating UI and calculation after applying coupon on product
     *
     * @param couponResponseModel is success response of Api apply coupon on product
     */
    private fun updateCouponResponse(couponResponseModel: MembershipCouponResponseModel.MembershipCouponResponseFirstModel) {
        if (couponResponseModel.membershipCouponResponseSecondModel.userInfoModel.proAccess == 1) {

            val userInfo = SharedPreferencesHelper.getUserInfoModel()
            /*userInfo?.apply {
                proAccess =
                    couponResponseModel.membershipCouponResponseSecondModel.userInfoModel.proAccess
            }*/

            if (null != userInfo)
                SharedPreferencesHelper.storeUserInfoModel(userInfoModel = userInfo)

            hideMembershipView()
        } else {
            membershipPackagesList!![Constants.App.Number.ZERO].apply {
                coupon =
                    couponResponseModel.membershipCouponResponseSecondModel.couponCode
                discount =
                    couponResponseModel.membershipCouponResponseSecondModel.discountValue
            }

            membershipAdapter?.notifyItemChanged(Constants.App.Number.ZERO)
        }
    }

    private fun resetCouponCodeView() {
        membershipPackagesList!![Constants.App.Number.ZERO].apply {
            coupon = ""
        }
    }

    /**
     * When successful response or data retrieved from Api get memberships
     *
     * @param packagesResponse is successful response of Api
     */
    override fun onMembershipPackagesResponseSuccess(packagesResponse: ArrayList<MembershipPackagesResponseModel>) {
        if (this@MembershipsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                if (packagesResponse.size == Constants.App.Number.ZERO) {
                    updateUIForEmptyList()
                } else {
                    rvMemberships.visibility = View.VISIBLE
                    addDataToList(listData = packagesResponse)
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from Api update membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@MembershipsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()
                showToast(message = membershipResponseModel.message)

                resetCouponCodeView()
            }
        }
    }

    /**
     * When successful response or data retrieved from Api get user membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@MembershipsActivity.baseContext != null) {
            runOnUiThread {
                if (!checkProcessAccess) {
                    checkProcessAccess = true

                   /* val model = SharedPreferencesHelper.getUserInfoModel()?.apply {
                        proAccess = membershipResponseModel.data!!.pro_access!!
                    }*/

                  //  SharedPreferencesHelper.storeUserInfoModel(model!!)
                    /*if (SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1) hideMembershipView()
                    else {
                        showMembershipView()
                        hitApiToGetMembershipList()
                    }*/
                } else {

                    when (membershipResponseModel.data) {
                        null -> callToUpdateMembershipProcess()
                        else -> {
                            if (membershipResponseModel.message.equals("No Membership yet") && membershipResponseModel.data!!.pro_access == 0) {
                                callToUpdateMembershipProcess()
                            }
                            if (membershipResponseModel.data?.isCanceled == AppConst.NUMBER.ZERO) {
                                val positiveAlertDialog = PositiveAlertDialog(
                                    baseActivity = this@MembershipsActivity,
                                    requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
                                    message = membershipResponseModel.message,
                                    positiveButtonText = getString(R.string.ok),
                                    listener = this@MembershipsActivity
                                )

                                positiveAlertDialog.show()
                            } else if (membershipResponseModel.data?.isCanceled == AppConst.NUMBER.ONE) {
                                if (AppHelper.getCurrentTimestampInSeconds() > membershipResponseModel.data?.currentPeriodEnd!!) {
                                    callToUpdateMembershipProcess()
                                } else {
                                    val positiveAlertDialog = PositiveAlertDialog(
                                        baseActivity = this@MembershipsActivity,
                                        requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
                                        message = getString(R.string.your_current_membership_has_not_expired_yet),
                                        positiveButtonText = getString(R.string.ok),
                                        listener = this@MembershipsActivity
                                    )

                                    positiveAlertDialog.show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * When successful response for api applying coupon on product
     *
     * @param response is successful response from Api
     */
    override fun onSuccessCouponResponse(response: MembershipCouponResponseModel.MembershipCouponResponseFirstModel) {
        if (this@MembershipsActivity.baseContext != null) {
            runOnUiThread {
                if (this@MembershipsActivity.baseContext != null) {
                    hideProgressDialog()
//                    coupon = response.coupon.number

                    updateCouponResponse(response)
                }
            }
        }
    }

    /**
     * When error occurred in response of api get memberships, get user membership, update membership
     *
     * @param errorResponse for Error Message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@MembershipsActivity.baseContext != null) {
            runOnUiThread {
                if (this@MembershipsActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.MEMBERSHIP
        )
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.SUBSCRIPTION -> afterMembershipTypeSelected(
                membershipTypePackagesResponseModel = model as MembershipTypePackagesResponseModel,
                position = position
            )

            ItemClickType.APPLY -> afterCouponApplied(
                membershipPackagesResponseModel = model as MembershipPackagesResponseModel?,
                position = position
            )

            else ->
                Log.e(TAG, "onRecyclerViewItemClick: itemClickType- INVALID")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.MEMBERSHIP_SCREEN -> {
                        Log.d(TAG, "onActivityResult")
                        val paymentToken: String? =
                            data?.getSerializableExtra(Constants.App.PAYMENT_TOKEN) as String?
                        afterPaymentTokenSuccess(paymentToken = paymentToken!!)
                    }

                    Constants.App.RequestCode.MEMBERSHIP -> {

                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                finish()
            }

            Activity.RESULT_FIRST_USER -> {

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
        when (requestCode) {
            Constants.App.Number.SIXTEEN -> onIndividualSubscriptionDetailClicked(
                model = model as MembershipTypePackagesResponseModel
            )

            else ->
                Log.e(TAG, "onDialogPositiveEvent: itemClickType- INVALID")
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
