package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.ProductDetailActivity
import com.fivestarmind.android.mvp.holder.ProductDetailPhaseViewHolder
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel

class ProductDetailPhasesAdapter(
    private var activity: ProductDetailActivity,
    private var phasesVideoList: ArrayList<PhaseVideoModel>?,
    private val listener: RecyclerViewItemListener
) :
    RecyclerView.Adapter<ProductDetailPhaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailPhaseViewHolder {
        return ProductDetailPhaseViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_phase,
                parent,
                false
            ), activity = activity,
            listener = listener
        )
    }

    override fun getItemCount(): Int = phasesVideoList!!.size

    override fun onBindViewHolder(holder: ProductDetailPhaseViewHolder, position: Int) {
        val model = phasesVideoList!![position]
        holder.bindView(model)
    }
}