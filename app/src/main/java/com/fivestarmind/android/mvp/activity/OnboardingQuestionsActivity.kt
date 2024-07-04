package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.BottomSheetListAdapter
import com.fivestarmind.android.mvp.model.request.SignUpRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SignInSignUpResponseModel
import com.fivestarmind.android.mvp.presenter.OnboardingQuestionsPresenter
import kotlinx.android.synthetic.main.activity_onboarding_questions.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_list.*

class OnboardingQuestionsActivity : BaseActivity(), RecyclerViewItemListener, OnboardingQuestionsPresenter.ResponseCallBack {

    private lateinit var presenter: OnboardingQuestionsPresenter
    private var signUpRequestModel = SignUpRequestModel()

    private var bottomSheetListAdapter: BottomSheetListAdapter? = null
    private var list: ArrayList<CommonResponse> = ArrayList()

    private var selectedImproveId: Int = 0
    private var dropdownClicked: Int = 0

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_questions)

        initPresenter()

        init()
        setClickListener()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = OnboardingQuestionsPresenter(this)
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        initBottomSheet()

    }

    private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(clBottomSheet);
        (bottomSheetBehavior as BottomSheetBehavior<ConstraintLayout>).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                Log.d(TAG, "onStateChanged = " + newState)

                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    cdlList.setBackgroundColor(ContextCompat.getColor(this@OnboardingQuestionsActivity, R.color.transparent))

                }else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    cdlList.setBackgroundColor(ContextCompat.getColor(this@OnboardingQuestionsActivity, R.color.transparent))

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })
    }

    private fun setUpDropdownAdapter() {
        if (bottomSheetListAdapter == null) {
            bottomSheetListAdapter = BottomSheetListAdapter(this, list, this)
            rvBottomSheetList.adapter = bottomSheetListAdapter

        }else {
            bottomSheetListAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        btnLetsGo.setOnClickListener(clickListener)
//        ivBack.setOnClickListener(clickListener)
        tvSkillLevel.setOnClickListener(clickListener)
        tvPlayPosition.setOnClickListener(clickListener)
        tvWeekDays.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.btnLetsGo -> clickedLetsGo()

//                R.id.ivBack -> onBackPressed()

                R.id.tvSkillLevel -> clickedDropDown(1)

                R.id.tvPlayPosition -> clickedDropDown(2)

                R.id.tvWeekDays -> clickedDropDown(3)
            }
    }

    private fun clickedDropDown(clickedValue: Int) {
        dropdownClicked = clickedValue
        list.clear()
        bottomSheetListAdapter = null
//        list = SharedPreferencesHelper.getAppContentModel()?.categories?.improvement!!
        setUpDropdownAdapter()
        openBottomSheet()
    }

    /**
     * It is called when user clicked on LetsGo button
     */
    private fun clickedLetsGo() {
        startActivity(Intent(this@OnboardingQuestionsActivity, HomeActivity::class.java))
        finish()

//        if (!validateForm()) return
//
//        if (isInternetConnected(shouldShowToast = true))
//            callLetsGoProcess()
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateForm(): Boolean {
        var valid = true

        tvSkillLevel.error = when {
            tvSkillLevel.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        tvPlayPosition.error = when {
            tvPlayPosition.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        tvWeekDays.error = when {
            tvWeekDays.text!!.isBlank() -> {
                valid = false
                getString(R.string.required)
            }

            else -> null
        }

        return valid
    }

    private fun callLetsGoProcess() {
//        showProgressDialog("")

//        signUpRequestModel.apply {
//            name = etName.text.toString()
//            email = etEmail.text.toString()
//            password = etPaswrd.text.toString()
//            deviceToken = fcmToken!!
//            clubCollege = etClubCollege.text.toString()
//            state = etState.text.toString()
//            referralCode = etReferralCode.text.toString()
//        }
//        presenter.hitApiForLetsGo(signUpRequestModel)
    }

    /**
     * When success response from lets go api
     *
     * @param response Success Response
     */
    override fun onLetsGoResponseSuccess(response: SignInSignUpResponseModel.SignInSignUpResponseFirstModel) {
        SharedPreferencesHelper.storeAuthToken(authToken = response.data.token)
      //  SharedPreferencesHelper.storeUserInfoModel(userInfoModel = response.data)

        if (this@OnboardingQuestionsActivity.baseContext != null) {
            runOnUiThread {
                if (this@OnboardingQuestionsActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)

                    startActivity(Intent(this@OnboardingQuestionsActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }
    }

    /**
     * When error occurred in lets go api
     *
     * @param errorResponse Error Response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        hideProgressDialog()
        responseFailure(errorResponse)
    }

    /**
     * call a function to Open Bottom Sheet
     */
    private fun openBottomSheet() {
        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * call a function to Close Bottom Sheet
     */
    private fun closeBottomSheet() {
        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.SELECT_BOTTOMSHEET_ITEM -> {
//                clickedBottomSheetItem(model as CommonResponse)
                clickedBottomSheetItem()
            }

            else -> Log.e(
                TAG,
                "onRecyclerViewItemClick: itemClickType- INVALID"
            )
        }
    }

    private fun clickedBottomSheetItem() {
        closeBottomSheet()

        when(dropdownClicked){
            1 -> {
                tvSkillLevel.text = "Dummy value"
            }
            2 -> {
                tvPlayPosition.text = "Dummy value"
            }
            3 -> {
                tvWeekDays.text = "Dummy value"
            }
        }
//        selectedImproveId = model.id
//        tvImprove.text = model.title
    }

    override fun onBackPressed() = finish()

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        hideKeyboardAndBottomSheet(event, bottomSheetBehavior, clBottomSheet)

        return super.dispatchTouchEvent(event)
    }
}
