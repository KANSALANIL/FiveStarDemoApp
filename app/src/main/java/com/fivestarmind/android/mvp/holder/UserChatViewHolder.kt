package com.fivestarmind.android.mvp.holder

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.ThreadsItem
import com.fivestarmind.android.retrofit.ApiClient
import kotlinx.android.synthetic.main.item_coaching_staff.view.pbUserImage
import kotlinx.android.synthetic.main.item_user_chat.view.ivChatUserProfile
import kotlinx.android.synthetic.main.item_user_chat.view.layoutChatCount
import kotlinx.android.synthetic.main.item_user_chat.view.onlineView
import kotlinx.android.synthetic.main.item_user_chat.view.tvChatCount
import kotlinx.android.synthetic.main.item_user_chat.view.tvChatTime
import kotlinx.android.synthetic.main.item_user_chat.view.tvLastMessage
import kotlinx.android.synthetic.main.item_user_chat.view.tvUserName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class UserChatViewHolder(
    itemView: View,
    var activity: BaseActivity,
    private var recyclerViewItemListener: RecyclerViewItemListener,
) : RecyclerView.ViewHolder(itemView) {

    private var messageItem: ThreadsItem? = null

    fun bindView(model: ThreadsItem) {
        messageItem = model
        messageItem!!.apply {
            if (recipient!= null) {
            if (recipient.name != null) {
                itemView.tvUserName.text = recipient.name
            }
        }
            if (recipientOnline!!) {
                itemView.onlineView.visibility = View.VISIBLE
                itemView.onlineView.background =
                    activity.resources.getDrawable(R.drawable.drawable_circle_yellow)

            } else {
                itemView.onlineView.visibility = View.GONE

              /*  itemView.onlineView.visibility = View.VISIBLE
                itemView.onlineView.background =
                    activity.resources.getDrawable(R.drawable.drawable_circle_green)*/

            }

            if (unreadCount != 0) {
                itemView.layoutChatCount.visibility = View.VISIBLE

                itemView.tvChatCount.setText("" + unreadCount)
            } else {
                itemView.layoutChatCount.visibility = View.GONE
            }

            if (messages!!.size > 0) {
                itemView.tvLastMessage.text = messages.get(messages.size - 1)!!.content

                itemView.tvChatTime.text = uTCToLocal(messages.get(messages.size - 1)!!.createdAt!!)

            }

            if (recipient!= null) {
                if (recipient!!.profileImg != null) {
                    itemView.apply {
                        Glide.with(activity)
                            .load(ApiClient.BASE_URL_PROFILE + recipient.profileImg)
                            .placeholder(R.drawable.ic_user_placeholder).listener(object :
                                RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    pbUserImage.visibility = View.GONE
                                    ivChatUserProfile.setImageDrawable(
                                        activity.resources.getDrawable(
                                            R.drawable.ic_user_placeholder
                                        )
                                    )
                                    return false

                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    pbUserImage.visibility = View.GONE
                                    return false
                                }

                            }).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            )
                            .into(ivChatUserProfile)

                    }

                }
            }
        }

        itemView.setOnClickListener { clickedDetail() }


    }

    /**
     * here call uTC To Local  date format funtion
     */
    fun uTCToLocal(
        datesToConvert: String?
    ): String? {
        var dateToReturn = datesToConvert
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        var gmt: Date? = null
        val sdfOutPutToSend = SimpleDateFormat("hh:mm a")
        sdfOutPutToSend.timeZone = TimeZone.getDefault()
        try {
            gmt = sdf.parse(datesToConvert)
            dateToReturn = sdfOutPutToSend.format(gmt)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return dateToReturn
    }

    private fun clickedDetail() {
        itemView.apply {

            recyclerViewItemListener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.DETAIL,
                model = messageItem!!,
                position = layoutPosition,
                view = itemView
            )
        }
    }

}