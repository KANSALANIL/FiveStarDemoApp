package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.FilterCategoryHolder
import com.fivestarmind.android.mvp.model.response.TagAllCategoryItem

class FilterCategoryAdapter(
    private var activity: Context,
    private var tagAllCategoryList: ArrayList<TagAllCategoryItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<FilterCategoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FilterCategoryHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_filter_category, parent, false)
        return FilterCategoryHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return tagAllCategoryList.size
    }


    override fun onBindViewHolder(holder: FilterCategoryHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(activity,tagAllCategoryList.get(position),tagAllCategoryList)
    }
}