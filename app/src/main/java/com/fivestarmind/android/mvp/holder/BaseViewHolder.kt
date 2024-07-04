package com.fivestarmind.android.mvp.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.interfaces.RecyclerViewItemListener

/**
 * All the viewHolders are using this BaseViewHolder class as parent class
 *
 * @param itemView view in which data is set from model
 * @param listener
 */
abstract class BaseViewHolder(itemView: View, private val listener: RecyclerViewItemListener) :
    RecyclerView.ViewHolder(itemView) {
    protected val TAG: String = javaClass.simpleName

}