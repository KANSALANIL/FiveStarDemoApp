package com.fivestarmind.android.mvp.holder

import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel
import kotlinx.android.synthetic.main.item_club_membership.view.*

class ClubMembershipViewHolder(
    private var itemView: View,
    private val listener: RecyclerViewItemListener
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

    fun bindView(membershipModelPackages: MembershipTypePackagesResponseModel, position: Int) {
        this.modelPackages = membershipModelPackages

        modelPackages?.apply {
            updatePrice()
            updateCodes()

            if (isSelected)
                showSelectedUI()
            else
                showUnSelectedUI()
        }
    }

    /**
     * Here is updating the price in view
     */
    private fun updatePrice() {
        itemView.apply {
            tvPrice!!.text = context.getString(
                R.string.format_price,
                modelPackages?.membershipFee.toString()
            )
        }
    }

    /**
     * Here is updating the codes in view
     */
    private fun updateCodes() {
        itemView.apply {
            tvCodes!!.text = context.getString(
                R.string.format_codes,
                modelPackages?.codes.toString()
            )
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