package com.fivestarmind.android.mvp.holder

import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.Constants.App.ProceedClick.shouldProceedClick
import com.fivestarmind.android.interfaces.DateTimeConst
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.adapter.MembershipTypeAdapter
import com.fivestarmind.android.mvp.model.response.MembershipPackagesResponseModel
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel
import kotlinx.android.synthetic.main.item_membership.view.*

class MembershipViewHolder(
    private var itemView: View,
    private val listener: RecyclerViewItemListener,
    private val activity: BaseActivity
) :
    RecyclerView.ViewHolder(itemView), RecyclerViewItemListener {

    private var TAG = MembershipViewHolder::class.java.simpleName
    private var modelPackages: MembershipPackagesResponseModel? = null

    private var membershipTypeAdapter: MembershipTypeAdapter? = null
    private var membershipLayoutManager: GridLayoutManager? = null

    private var membershipTypePackagesList: ArrayList<MembershipTypePackagesResponseModel>? =
        arrayListOf()
    private var selectedMembershipTypePackagesModel = MembershipTypePackagesResponseModel()

    private var uppercaseTextWatcher: TextWatcher? = null

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.clRoot -> clickedArrowView()

                R.id.btnDetails -> clickedDetails()

                R.id.tvApply -> clickedApply()

                R.id.ivCross -> clickedCross()
            }
    }

    init {
        initData()
        initRecyclerViewWithAdapter()

        initRootView()
        setClickListener()
    }

    /**
     * Initializing data to initial state
     */
    private fun initData() {
//        membershipTypePackagesList = arrayListOf()

        itemView.apply {
            uppercaseTextWatcher = AppHelper.UppercaseTextWatcher(editText = etPromoCode)
            etPromoCode.addTextChangedListener(uppercaseTextWatcher)
        }
    }

    /**
     * Here recyclerViews are setup with it's adapter
     */
    private fun initRecyclerViewWithAdapter() {
        // Here setup is done for membership types list
        membershipTypeAdapter = MembershipTypeAdapter(
            membershipTypePackagesList = membershipTypePackagesList!!,
            listener = this,
            activity = activity
        )

        membershipLayoutManager =
            GridLayoutManager(itemView.context, Constants.App.Number.TWO)

        itemView.rvMemberships.apply {
            layoutManager = membershipLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = membershipTypeAdapter
        }
    }

    /**
     * Setting up the root layout
     */
    private fun initRootView() =
        itemView.apply {
            clRoot.apply {
                viewTreeObserver.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        clRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        initIndividualRootView()
                    }
                })
            }
        }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        itemView.apply {
            clRoot.setOnClickListener(clickListener)
            btnDetails.setOnClickListener(clickListener)
            tvApply.setOnClickListener(clickListener)
            ivCross.setOnClickListener(clickListener)
        }
    }

    fun bindView(membershipPackagesModel: MembershipPackagesResponseModel, position: Int) {
        this.modelPackages = membershipPackagesModel

        modelPackages?.apply {
            when (membershipType) {
                activity.getString(R.string.team_) -> itemView.clRoot.visibility = View.GONE
                activity.getString(R.string.club_) -> itemView.clRoot.visibility = View.GONE
                else -> itemView.clRoot.visibility = View.VISIBLE
            }

            updateMembershipType()
            updateButtonText()
            addDataToList(listData = data!!)
            showHideCouponAppliedUI()
        }
    }

    /**
     * Here is updating the date in view
     */
    private fun updateMembershipType() {
        itemView.apply {
            tvMembershipType.text = modelPackages?.membershipType
        }
    }

    /**
     * Here is updating the button text
     */
    private fun updateButtonText() {
        itemView.context.apply {
            when (modelPackages?.membershipType) {
                getString(R.string.individual_) ->
                    updateSubscribeOrBuyView(
                        subscribe = getString(R.string.subscribe),
                        status = false
                    )

                getString(R.string.team_) ->
                    updateSubscribeOrBuyView(subscribe = getString(R.string.buy), status = true)

                getString(R.string.club_) ->
                    updateSubscribeOrBuyView(subscribe = getString(R.string.buy), status = false)
            }
        }
    }

    private fun updateSubscribeOrBuyView(subscribe: String, status: Boolean) {
        itemView.apply {
            btnDetails.text = subscribe
            tvTeamMembershipDesc.visibility = if (status) View.VISIBLE else View.GONE
        }
    }

    /**
     * Here is adding data to list
     */
    private fun addDataToList(listData: ArrayList<MembershipTypePackagesResponseModel>) {
        membershipTypePackagesList!!.clear()

        membershipTypePackagesList?.addAll(listData)
        membershipTypeAdapter?.notifyDataSetChanged()

//        listData.clear()
    }

    private fun initIndividualRootView() {
        itemView.apply {
            ivDropDown.isSelected = true
            toggleIndividualRootView(duration = 0)
        }
    }

    /**
     * Here root layout is shown/hidden with animation
     *
     * @param duration total time of animation's execution
     */
    private fun toggleIndividualRootView(duration: Long) =
        itemView.apply {
            when (ivDropDown.isSelected) {
                true -> hideIndividualRootView(duration = duration)
                false -> showIndividualRootView(duration = duration)
            }
        }

    private fun showIndividualRootView(duration: Long) {
        itemView.apply {
            if (!ivDropDown.isSelected) {
                ivDropDown.isSelected = true

                clInner.animate().translationY(0f).duration = duration
                ivDropDown.animate().rotation(90f).duration = duration

                clInner.setOnTouchListener(null)
                clInner.visibility = View.VISIBLE
            }
        }
    }

    private fun hideIndividualRootView(duration: Long) {
        itemView.apply {
            if (ivDropDown.isSelected) {
                ivDropDown.isSelected = false

                ivDropDown.animate().rotation(0f).duration = duration

                clInner.setOnTouchListener { v, event ->
                    return@setOnTouchListener true
                }

                clInner.visibility = View.GONE
            }
        }
    }

    /**
     * Here is showing coupon applied/ promo code UI
     */
    private fun showHideCouponAppliedUI() {
        modelPackages?.apply {
            when {
                coupon.isEmpty() || coupon.isBlank() -> showApplyCouponUI()
                else -> showCouponAppliedUI()
            }
        }
    }

    private fun showApplyCouponUI() {
        itemView.apply {
            clPromoCode.visibility = View.VISIBLE
            clAppliedPromoCode.visibility = View.GONE
        }
    }

    private fun showCouponAppliedUI() {
        itemView.apply {
            clAppliedPromoCode.visibility = View.VISIBLE
            clPromoCode.visibility = View.GONE

            updateCouponAppliedDataInUI()
        }
    }

    private fun updateCouponAppliedDataInUI() {
        itemView.apply {
            tvCouponName.text =
                activity.getString(R.string.format_parentheses, modelPackages?.coupon)

            tvDiscountVal.text = activity.getString(
                R.string.format_price_discount_on_code,
                modelPackages?.discount.toString()
            )
        }

        updateDiscountPrice()
    }

    private fun updateDiscountPrice() {
        modelPackages?.data?.apply {
            this[Constants.App.Number.ZERO].discount = modelPackages?.discount!!
        }

        membershipTypeAdapter?.notifyItemChanged(Constants.App.Number.ZERO)
    }

    /**
     *  It is called when user click on individual arrow view
     */
    private fun clickedArrowView() {
        modelPackages?.apply {
            toggleIndividualRootView(duration = DateTimeConst.DURATION_IN_MILLISECONDS.ANIMATION_LONG)

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.SELECT_MEMBERSHIP,
                model = modelPackages,
                position = layoutPosition,
                view = itemView
            )
        }
    }

    /**
     * It is called when user click on subscription button view
     */
    private fun clickedDetails() {
        itemView.apply {
            if (selectedMembershipTypePackagesModel.type.isEmpty())
                activity.showToast(message = context.getString(R.string.pls_select_membership))
            else listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.SUBSCRIPTION,
                model = selectedMembershipTypePackagesModel,
                position = layoutPosition,
                view = itemView
            )
        }
    }

    private fun selectedMembership(
        membershipTypePackagesResponseModel: MembershipTypePackagesResponseModel,
        position: Int
    ) {
        selectedMembershipTypePackagesModel?.apply {
            val index = membershipTypePackagesList!!.indexOf(this)

            if (index >= Constants.App.Number.ZERO) {
                membershipTypePackagesList?.get(index)!!.isSelected = false
                membershipTypeAdapter?.notifyItemChanged(index)
            }
        }

        selectedMembershipTypePackagesModel = membershipTypePackagesResponseModel.apply {
            isSelected = true
        }
        membershipTypeAdapter?.notifyItemChanged(position)
    }

    private fun updateSelectedModel(
        membershipTypePackagesResponseModel: MembershipTypePackagesResponseModel,
        position: Int
    ) {
        selectedMembershipTypePackagesModel?.apply {
            val index = membershipTypePackagesList!!.indexOf(this)

            if (index >= Constants.App.Number.ZERO) {
                membershipTypePackagesList?.get(index)!!.isSelected = false
            }
        }

        selectedMembershipTypePackagesModel = membershipTypePackagesResponseModel.apply {
            isSelected = true
        }
    }

    /**
     * It is called when user clicked on apply view
     */
    private fun clickedApply() {
        itemView.apply {
            modelPackages?.apply {
                coupon = etPromoCode.text.toString()
            }

            etPromoCode.text.clear()
        }

        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.APPLY,
            model = modelPackages,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked on cross to clear applied coupon
     */
    private fun clickedCross() {
        showApplyCouponUI()

        membershipTypePackagesList?.apply {
            this[Constants.App.Number.ZERO].discount = 0
        }

        membershipTypeAdapter?.notifyItemChanged(Constants.App.Number.ZERO)
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.DETAIL -> selectedMembership(
                membershipTypePackagesResponseModel = model as MembershipTypePackagesResponseModel,
                position = position
            )

            ItemClickType.UPDATE_MODEL -> updateSelectedModel(
                membershipTypePackagesResponseModel = model as MembershipTypePackagesResponseModel,
                position = position
            )

            else -> Log.e(TAG, "onRecyclerViewItemClick: itemClickType- INVALID")
        }
    }
}