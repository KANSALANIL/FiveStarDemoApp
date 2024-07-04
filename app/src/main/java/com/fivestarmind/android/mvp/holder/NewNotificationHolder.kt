package com.fivestarmind.android.mvp.holder


import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.NotificationData
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_my_favorite.view.*
import kotlinx.android.synthetic.main.item_new_notification.view.*
import okhttp3.internal.Util

class NewNotificationHolder(
    itemView: View,
    activity: BaseActivity,
    private var recyclerViewItemListener: RecyclerViewItemListener
) :
    RecyclerView.ViewHolder(itemView) {

    private var notificationData: NotificationData? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (activity.shouldProceedClick())
            when (view.id) {
               // R.id.clProgramsRoot -> clickedDetail()
            }
    }
/*
    init {
        itemView.apply {
            clProgramsRoot.setOnClickListener(clickListener)
        }
    }*/

    fun bindView(
        context: Context,
        responseModel: NotificationData,
    ) {

        this.notificationData = responseModel

        if (notificationData!!.file!=null) {
            itemView.tvTitle.setText(notificationData!!.file!!.title)

            if (notificationData!!.file!!.duration != null) {
                itemView.tvSubTitle.setText(
                    context.getString(
                        R.string.format_notification_time,
                        AppHelper.getTimeFromat(notificationData!!.file!!.duration!!), "minutes"
                    )
                )
            } else {
                itemView.tvSubTitle.visibility = View.VISIBLE
            }

        }

        itemView.setOnClickListener {
            clickedDetail()
        }

        //itemView.tvTitle
    }

    /**
     * It is called when user click on view
     */
    private fun clickedDetail() {
        notificationData?.apply {
            recyclerViewItemListener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.OPEN_NOTIFICATION,
                model=notificationData,
                position = layoutPosition,
                view = itemView)
        }
    }
}