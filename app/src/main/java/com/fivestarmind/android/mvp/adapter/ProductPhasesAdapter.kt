package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.ProductPhaseListingActivity
import com.fivestarmind.android.mvp.holder.ProductPhasesHolder
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel

class ProductPhasesAdapter(
    private var videoList: ArrayList<FavoriteResponseDataModel>,
    private var activity: ProductPhaseListingActivity,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<ProductPhasesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductPhasesHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_phase_list, parent, false)
        return ProductPhasesHolder(view, activity = activity, listener = listener)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    internal fun clearVideoList() {
        this.videoList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductPhasesHolder, position: Int) {
        holder.bindView(videoList[position])
    }
}