package com.fivestarmind.android.mvp.holder

import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel
import kotlinx.android.synthetic.main.item_individual_membership.view.*

class IndividualMembershipViewHolder(
    private var itemView: View,
    private val listener: RecyclerViewItemListener,
    private val activity: BaseActivity
) :
    BaseViewHolder(itemView, listener) {

    private var modelPackages: MembershipTypePackagesResponseModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (Constants.App.ProceedClick.shouldProceedClick())
            when (view.id) {
                R.id.clRoot -> clickedDetail()
            }
    }

    init {
        itemView.apply {
            clRoot.setOnClickListener(clickListener)
        }
    }

    fun bindView(membershipModelPackages: MembershipTypePackagesResponseModel?, position: Int) {
        this.modelPackages = membershipModelPackages

        modelPackages?.apply {
            updatePrice()
            updateTime()

            if (isSelected || position == Constants.App.Number.ZERO) {
                if (position == Constants.App.Number.ZERO) updateSelectedModel(position = position)
                else showSelectedUI()
            } else showUnSelectedUI()

            updateDiscountPrice()
        }
    }

    /**
     * Here is updating the price in view
     */
    private fun updatePrice() {
        itemView.apply {
            tvPrice.text = context.getString(
                R.string.format_price,
                modelPackages?.membershipFee.toString()
            )
        }
    }

    /**
     * Here is updating the time in view
     */
    private fun updateTime() {
        itemView.apply {
            tvTime.text = modelPackages?.duration
        }
    }

    private fun showSelectedUI() {
        updateSelectedView(status = true)
    }

    private fun showUnSelectedUI() {
        updateSelectedView(status = false)
    }

    /**
     * Here updating the selected view with data
     *
     * @param status
     */
    private fun updateSelectedView(status: Boolean) {
        itemView.apply {
            clRoot.isSelected = status
        }
    }

    /**
     * Here updating the discounted price value
     */
    private fun updateDiscountPrice() {
        itemView.apply {
            when (modelPackages?.discount) {
                null -> modelPackages?.membershipPrice = modelPackages?.membershipFee!!

                Constants.App.Number.ZERO -> modelPackages?.membershipPrice =
                    modelPackages?.membershipFee!!

                else -> calculateDiscount()
            }
        }
    }

    private fun calculateDiscount() {
        itemView.apply {
            val discountedValue = (modelPackages?.membershipFee!!.toDouble() -
                    (modelPackages?.membershipFee!!.toDouble() * modelPackages?.discount!!.toDouble() / 100))

            tvPrice.text = activity.formatPrice(activity, discountedValue.toBigDecimal())

            modelPackages?.membershipPrice = discountedValue
        }
    }

    private fun updateSelectedModel(position: Int) {
        modelPackages?.apply {
            if (!isSelected) {
                isSelected = true

                showSelectedUI()

                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.UPDATE_MODEL,
                    model = this,
                    position = layoutPosition,
                    view = itemView
                )
            }
            else{
                if (position == Constants.App.Number.ZERO){
                    isSelected = true

                    showSelectedUI()

                    listener.onRecyclerViewItemClick(
                        itemClickType = ItemClickType.UPDATE_MODEL,
                        model = this,
                        position = layoutPosition,
                        view = itemView
                    )
                }
            }
        }
    }

    /**
     * It is called when user click on root view
     */
    private fun clickedDetail() {
        modelPackages?.apply {
            if (!isSelected) {
                isSelected = true

                showSelectedUI()

                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.DETAIL,
                    model = this,
                    position = layoutPosition,
                    view = itemView
                )
            }
        }
    }
}