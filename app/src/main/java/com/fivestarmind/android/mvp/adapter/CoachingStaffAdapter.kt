package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.CoachingStaffViewHolder
import com.fivestarmind.android.mvp.model.response.UsersDataItemListing

class CoachingStaffAdapter(
    private val activity: BaseActivity,
    private val usersListing: ArrayList<UsersDataItemListing>,
    private val recyclerViewItemListener: RecyclerViewItemListener
) : RecyclerView.Adapter<CoachingStaffViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachingStaffViewHolder {
        return CoachingStaffViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_coaching_staff, parent, false),
            activity = activity,
            listener = recyclerViewItemListener,
        )
    }

    override fun getItemCount(): Int = usersListing.size

    override fun onBindViewHolder(holder: CoachingStaffViewHolder, position: Int) {
        val model = usersListing[position]
        holder.bindView(model)
    }

}