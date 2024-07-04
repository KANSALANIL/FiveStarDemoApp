package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.ImageListHolder
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel
import com.fivestarmind.android.mvp.model.response.FilesItem
import kotlinx.android.synthetic.main.item_images.view.ivItemImage

class ImageListAdapter(
    private var activity: Context,
    private var dataList: ArrayList<FilesItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<ImageListHolder>() {

    private var programsList = ArrayList<FavoriteResponseDataModel>()
    var mholder:ImageListHolder?=null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ImageListHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_images, parent, false)

        return ImageListHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ImageListHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        mholder =holder
        holder.bindView(activity,dataList.get(position))

    }

}