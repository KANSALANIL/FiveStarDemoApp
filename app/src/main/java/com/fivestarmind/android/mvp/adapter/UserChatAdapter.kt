package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.UserChatViewHolder
import com.fivestarmind.android.mvp.model.response.ThreadsItem

class UserChatAdapter(
    private val activity: BaseActivity,
    private var messagesList: ArrayList<ThreadsItem>? = null,
    private val recyclerViewItemListener: RecyclerViewItemListener,
) : RecyclerView.Adapter<UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        return UserChatViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_chat, parent, false),
            activity = activity,
            recyclerViewItemListener = recyclerViewItemListener,
        )
    }

    override fun getItemCount(): Int = messagesList!!.size

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val model = messagesList!![position]
        holder.bindView(model)

    }

    fun setFilter(FilteredDataList:ArrayList<ThreadsItem>) {
        messagesList = FilteredDataList
        notifyDataSetChanged()
    }

}