package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookmarkSectionItemHolder
import com.fivestarmind.android.mvp.model.response.BookmarkAllCategoryItem
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem

class BookMarkSectionItemAdapter(
    private var activity: Context,
    private var bookmarkFilesItem: ArrayList<BookmarkFilesItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookmarkSectionItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookmarkSectionItemHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookmarks_all_category, parent, false)
        return BookmarkSectionItemHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookmarkFilesItem.size
    }


    override fun onBindViewHolder(holder: BookmarkSectionItemHolder, position: Int) {
        holder.bindView(activity, bookmarkFilesItem.get(position),bookmarkFilesItem)
    }


}