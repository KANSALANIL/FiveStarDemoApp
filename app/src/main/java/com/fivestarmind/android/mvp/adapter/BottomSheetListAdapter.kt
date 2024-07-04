package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.BottomSheetListViewHolder
import com.fivestarmind.android.mvp.model.response.CommonResponse
import java.util.ArrayList

class BottomSheetListAdapter(
    private val context: Context?,
    private val list: ArrayList<CommonResponse>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<BottomSheetListViewHolder>() {

    private val TAG = javaClass.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetListViewHolder =
        BottomSheetListViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_bottom_sheet_list,
                parent,
                false
            ),
            listener = listener
        )

    override fun onBindViewHolder(holder: BottomSheetListViewHolder, position: Int) =
        holder.bind()
//        holder.bind(list[position])

    override fun getItemCount(): Int = 5
//    override fun getItemCount(): Int = list.size

}