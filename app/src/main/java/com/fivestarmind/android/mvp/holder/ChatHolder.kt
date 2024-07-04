package com.fivestarmind.android.mvp.holder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.MessagesItem
import com.fivestarmind.android.retrofit.ApiClient
import kotlinx.android.synthetic.main.item_chat.view.clSenderRoot
import kotlinx.android.synthetic.main.item_chat.view.clUserSide
import kotlinx.android.synthetic.main.item_chat.view.ivSenderProfileImage
import kotlinx.android.synthetic.main.item_chat.view.ivUserProfileImage
import kotlinx.android.synthetic.main.item_chat.view.pbSenderImage
import kotlinx.android.synthetic.main.item_chat.view.pbUserImage
import kotlinx.android.synthetic.main.item_chat.view.tvReceived
import kotlinx.android.synthetic.main.item_chat.view.tvSendUserMessage
import kotlinx.android.synthetic.main.item_chat.view.tvSenderName
import kotlinx.android.synthetic.main.item_chat.view.tvSenderTime
import kotlinx.android.synthetic.main.item_chat.view.tvTimeUser
import kotlinx.android.synthetic.main.item_chat.view.tvUserName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ChatHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var TAG = ChatHolder::class.java.simpleName
    private var messagesItem: MessagesItem? = null

    // private var chatResponse: ThreadIdResponse? = null
    var senderImagePath: String = ""
    var senderName: String = ""
    @SuppressLint("SuspiciousIndentation")
    fun bindView(
        model: MessagesItem,
        image: String,
        name: String,
        activity: Context
    ) {
        messagesItem = model
        // chatResponse = response
        senderImagePath = image
        senderName = name
        messagesItem?.apply {
            if (senderUserId.equals(SharedPreferencesHelper.getUserId())) {
                //User
                itemView.clSenderRoot.visibility = View.GONE
                itemView.ivSenderProfileImage.visibility = View.GONE

                itemView.clUserSide.visibility = View.VISIBLE

                itemView.tvSendUserMessage.text = content
                itemView.tvUserName.text = activity.getString(R.string.login_user_me)
                itemView.tvTimeUser.text = uTCToLocal(createdAt)

                    setGlideImage(
                        activity,
                        SharedPreferencesHelper.getUserImage(),
                        itemView.pbUserImage,
                        itemView.ivUserProfileImage
                    )


            } else {
                //Sender
                itemView.clSenderRoot.visibility = View.VISIBLE
                itemView.ivSenderProfileImage.visibility = View.VISIBLE

                itemView.clUserSide.visibility = View.GONE
                itemView.tvReceived.text = content
                itemView.tvSenderTime.text = uTCToLocal(createdAt)


                //Receiver side

                    setGlideImage(
                        activity,
                        senderImagePath,
                        itemView.pbSenderImage,
                        itemView.ivSenderProfileImage
                    )

                //set sender name
                if (senderName.isNotEmpty()) {

                    itemView.tvSenderName.text = senderName
                }

                /*
                                    for (i in 0 until chatResponse!!.data!!.users!!.size) {

                                    if (!chatResponse!!.data!!.users!!.get(i).id.equals(SharedPreferencesHelper.getUserId())) {
                                        if (chatResponse!!.data!!.users!!.get(i).profileImg != null) {
                                            setGlideImage(
                                                activity,
                                                chatResponse!!.data!!.users!!.get(i).profileImg!!,
                                                itemView.pbSenderImage,
                                                itemView.ivSenderProfileImage
                                            )
                                            if (chatResponse!!.data!!.users!!.get(i).name != null) {
                                                itemView.tvSenderName.text =
                                                    chatResponse!!.data!!.users!!.get(i).name!!
                                            }


                                        }
                                    }
                                }*/

            }
        }
    }

    fun setGlideImage(
        context: Context,
        imageUrl: String,
        progressBar: ProgressBar,
        imageView: ImageView
    ) {

        progressBar.visibility = View.VISIBLE

        Glide.with(context)
            .load(ApiClient.BASE_URL_PROFILE + imageUrl)
            .placeholder(R.drawable.ic_user_placeholder).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageResource(R.drawable.ic_user_placeholder)
                    progressBar.visibility = View.GONE
                    return false

                }


                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

            }).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .into(imageView)

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

}