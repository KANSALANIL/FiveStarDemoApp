package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.BaseViewHolder
import com.fivestarmind.android.mvp.holder.ClubMembershipViewHolder
import com.fivestarmind.android.mvp.holder.IndividualMembershipViewHolder
import com.fivestarmind.android.mvp.holder.TeamMembershipViewHolder
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel


class MembershipTypeAdapter(
    private val membershipTypePackagesList: ArrayList<MembershipTypePackagesResponseModel>?,
    private val listener: RecyclerViewItemListener,
    private val activity: BaseActivity
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val VIEW_INDIVIDUAL = 1
    private val VIEW_TEAM = 2
    private val VIEW_CLUB = 3

    companion object {
        internal val DEVICE_WIDTH =
            (AppController.mInstance!!.resources.displayMetrics.widthPixels)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when (viewType) {
            VIEW_INDIVIDUAL -> createIndividualMembershipViewHolder(parent)

            VIEW_TEAM -> createTeamMembershipGridViewHolder(parent)

            VIEW_CLUB -> createClubMembershipGridViewHolder(parent)

            else -> createIndividualMembershipViewHolder(parent)
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is IndividualMembershipViewHolder -> holder.bindView(
                membershipModelPackages = membershipTypePackagesList!![position],
                position = position
            )

            is TeamMembershipViewHolder -> holder.bindView(
                membershipModelPackages = membershipTypePackagesList!![position],
                position = position
            )

            is ClubMembershipViewHolder -> holder.bindView(
                membershipModelPackages = membershipTypePackagesList!![position],
                position = position
            )
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (membershipTypePackagesList!![position].type) {
            activity.getString(R.string.individual_) -> VIEW_INDIVIDUAL
            activity.getString(R.string.team_)  -> VIEW_TEAM
            activity.getString(R.string.club_)  -> VIEW_CLUB
            else -> VIEW_INDIVIDUAL
        }

    /**
     * Here creating the view holder for individual membership
     */
    private fun createIndividualMembershipViewHolder(parent: ViewGroup): IndividualMembershipViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_individual_membership, parent, false)
        if (membershipTypePackagesList!!.size == Constants.App.Number.ONE) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 1.22).toInt()
        }
        return IndividualMembershipViewHolder(itemView, listener = listener, activity = activity)
    }

    /**
     * Here creating the view holder for team membership
     */
    private fun createTeamMembershipGridViewHolder(parent: ViewGroup): TeamMembershipViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_membership, parent, false)
        if (membershipTypePackagesList!!.size == Constants.App.Number.ONE) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 1.22).toInt()
        }
        return TeamMembershipViewHolder(itemView, listener = listener)
    }

    /**
     * Here creating the view holder for club membership
     */
    private fun createClubMembershipGridViewHolder(parent: ViewGroup): ClubMembershipViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_club_membership, parent, false)
        if (membershipTypePackagesList!!.size == Constants.App.Number.ONE) {
            itemView.layoutParams.width = (DEVICE_WIDTH / 1.22).toInt()
        }
        return ClubMembershipViewHolder(itemView, listener = listener)
    }

    override fun getItemCount(): Int = membershipTypePackagesList!!.size

    internal fun destroyObjects() {

    }
}