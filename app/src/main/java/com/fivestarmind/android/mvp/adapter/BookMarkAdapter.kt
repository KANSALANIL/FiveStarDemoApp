package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookMarkHolder
import com.fivestarmind.android.mvp.model.response.BookMarkDataItem

class BookMarkAdapter(
    private var activity: Context,
    private var bookMarkList: ArrayList<BookMarkDataItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookMarkHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookMarkHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_favorite, parent, false)
        return BookMarkHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookMarkList.size
    }


    override fun onBindViewHolder(holder: BookMarkHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(activity,bookMarkList.get(position))
    }
}