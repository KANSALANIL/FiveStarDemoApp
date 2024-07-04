package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookmarkSectionHolder
import com.fivestarmind.android.mvp.model.response.BookmarkAllCategoryItem
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem

class BookMarkSectionAdapter(
    private var activity: Context,
    private var bookmarkAllCategoryItem: ArrayList<BookmarkAllCategoryItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookmarkSectionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookmarkSectionHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookmarks_section, parent, false)
        return BookmarkSectionHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookmarkAllCategoryItem.size
    }


    override fun onBindViewHolder(holder: BookmarkSectionHolder, position: Int) {
         holder.bindView(activity,bookmarkAllCategoryItem.get(position))
    }


}