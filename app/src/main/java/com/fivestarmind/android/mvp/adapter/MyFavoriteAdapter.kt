package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.MyFavoriteHolder
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel

class MyFavoriteAdapter(
    private var activity: Context,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<MyFavoriteHolder>() {

    private var programsList = ArrayList<FavoriteResponseDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyFavoriteHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_favorite, parent, false)
        return MyFavoriteHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return 10
    }

    internal fun updateResponseList(responseDataList: ArrayList<FavoriteResponseDataModel>) {
        this.programsList = responseDataList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyFavoriteHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(activity)
    }
}