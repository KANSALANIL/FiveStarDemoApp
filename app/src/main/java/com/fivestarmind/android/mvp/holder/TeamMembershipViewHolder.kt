package com.fivestarmind.android.mvp.holder

import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel
import kotlinx.android.synthetic.main.item_team_membership.view.*

class TeamMembershipViewHolder(
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
            updateMembers()

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
            modelPackages?.membershipFee?.apply {
                tvPrice!!.text = context.getString(
                    R.string.format_price,
                    this.toString()
                )
            }
        }
    }

    /**
     * Here is updating the codes in view
     */
    private fun updateCodes() {
        itemView.apply {
            modelPackages?.codes?.apply {
                tvCodes!!.text = context.getString(
                    R.string.format_codes,
                    this.toString()
                )
            }
        }
    }

    /**
     * Here is updating the team members in view
     */
    private fun updateMembers() {
        itemView.apply {
            modelPackages?.codes?.apply {
                tvTeamMembers!!.text = context.getString(
                    R.string.format_team_members,
                   this.toString()
                )
            }
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