package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookmarkVideosHolder
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem

class BookMarkVideosAdapter(
    private var activity: Context,
      private var bookmarkVideoFilesItem: ArrayList<BookmarkFilesItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookmarkVideosHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookmarkVideosHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bookmarks_all_category, parent, false)
        return BookmarkVideosHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookmarkVideoFilesItem.size
    }


    override fun onBindViewHolder(holder: BookmarkVideosHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder. bindView(activity,bookmarkVideoFilesItem.get(position))
    }



}