package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.NewVideosViewHolder
import com.fivestarmind.android.mvp.model.response.NewVideosModel

class NewVideosAdapter(
    private val newVideosList: ArrayList<NewVideosModel>,
    private val activity: BaseActivity,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<NewVideosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewVideosViewHolder {
        return NewVideosViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_program_new_videos,
                parent,
                false
            ), activity = activity, listener = listener
        )
    }

    override fun getItemCount(): Int = newVideosList.size

    override fun onBindViewHolder(holder: NewVideosViewHolder, position: Int) {
        val model = newVideosList[position]
        holder.bindView(model)
    }

    override fun getItemViewType(position: Int): Int {
        return newVideosList[position].id
    }
}