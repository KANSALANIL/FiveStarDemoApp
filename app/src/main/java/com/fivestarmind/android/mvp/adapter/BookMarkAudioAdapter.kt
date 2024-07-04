package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BookmarkAudioHolder
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem

class BookMarkAudioAdapter(
    private var activity: Context,
      private var bookMarkList: ArrayList<BookmarkFilesItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BookmarkAudioHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BookmarkAudioHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bookmarks_all_category, parent, false)
        return BookmarkAudioHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return bookMarkList.size
    }


    override fun onBindViewHolder(holder: BookmarkAudioHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(activity,bookMarkList.get(position))
    }
}