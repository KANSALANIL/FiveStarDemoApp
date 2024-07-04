package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.ChatHolder
import com.fivestarmind.android.mvp.model.response.MessagesItem

class ChatAdapter(
    private var activity: Context,
    private var userChatList: ArrayList<MessagesItem>,
    private var ChatSenderImagePath: String,
    private var ChatSenderName: String,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<ChatHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ChatHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return userChatList.size
    }


    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.bindView(userChatList[position], ChatSenderImagePath,ChatSenderName, activity)
    }


   fun insertData(messagesItem: ArrayList<MessagesItem>?){
        this.userChatList=messagesItem!!
    //    notifyDataSetChanged()
    }
}