package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.AllVideoListHolder
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem

class AllVideoListAdapter(
    private var activity: Context,
    private var allDataList: ArrayList<SeeAllDataItem>? = null,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<AllVideoListHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AllVideoListHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_favorite, parent, false)
        return AllVideoListHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return allDataList!!.size
    }

    override fun onBindViewHolder(holder: AllVideoListHolder, position: Int) {
        // holder.bindView(programsList[position], activity)
        holder.bindView(activity, allDataList!!.get(position))
    }

    fun updateData(allDataList: ArrayList<SeeAllDataItem>){

    }
}