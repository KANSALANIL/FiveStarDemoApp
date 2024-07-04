package com.fivestarmind.android.mvp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.MembershipViewHolder
import com.fivestarmind.android.mvp.model.response.MembershipPackagesResponseModel

class MembershipAdapter(
    private val membershipPackagesList: ArrayList<MembershipPackagesResponseModel>?,
    private val listener: RecyclerViewItemListener,
    private val activity: BaseActivity
) : RecyclerView.Adapter<MembershipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MembershipViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_membership, parent, false)
        return MembershipViewHolder(itemView, listener = listener, activity = activity)
    }

    override fun getItemCount(): Int = membershipPackagesList!!.size

    override fun onBindViewHolder(holder: MembershipViewHolder, position: Int) {
        val response =
            GsonHelper.convertJavaObjectToJsonString(model = membershipPackagesList)

        Log.d(
            "membershipPackagesList",
            "membershipPackagesList onBindViewHolder= $response"
        )

        holder.bindView(
            membershipPackagesModel = membershipPackagesList!![position],
            position = position
        )
    }
}