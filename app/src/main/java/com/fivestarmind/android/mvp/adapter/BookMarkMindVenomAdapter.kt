package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookmarkMindVenomHolder
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem

class BookMarkMindVenomAdapter(
    private var activity: Context,
      private var bookMarkList: ArrayList<BookmarkFilesItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookmarkMindVenomHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookmarkMindVenomHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bookmarks_all_category, parent, false)
        return BookmarkMindVenomHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookMarkList.size
    }


    override fun onBindViewHolder(holder: BookmarkMindVenomHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder. bindView(activity,bookMarkList.get(position))
    }
}