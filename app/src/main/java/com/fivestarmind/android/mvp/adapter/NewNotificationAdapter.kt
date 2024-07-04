package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.NewNotificationHolder
import com.fivestarmind.android.mvp.model.response.NotificationData

class NewNotificationAdapter(
    private var activity: BaseActivity,
    private val recyclerViewItemListener: RecyclerViewItemListener,
    private val notificationDataList:ArrayList<NotificationData>?
) : RecyclerView.Adapter<NewNotificationHolder>() {

    private var categoryId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NewNotificationHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_new_notification, parent, false)
        return NewNotificationHolder(
            itemView,
            activity = activity,
            recyclerViewItemListener = recyclerViewItemListener
        )
    }

    fun updateCategoryId(categoryId: String) {
        this.categoryId = categoryId
    }


    override fun getItemCount(): Int = notificationDataList!!.size

    override fun onBindViewHolder(holder: NewNotificationHolder, position: Int) {
       holder.bindView(activity,notificationDataList!!.get(position))
    }
}