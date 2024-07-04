package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.AllPdfListHolder
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel

class AllPdfListAdapter(
    private var activity: Context,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<AllPdfListHolder>() {

    private var programsList = ArrayList<FavoriteResponseDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): AllPdfListHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_pdf, parent, false)
        return AllPdfListHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return 10
    }


    override fun onBindViewHolder(holder: AllPdfListHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(activity)
    }
}