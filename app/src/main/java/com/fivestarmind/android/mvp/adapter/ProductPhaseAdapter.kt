package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.ProductPhaseViewHolder
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel

class ProductPhaseAdapter(
    private val productsList: ArrayList<PhaseVideoModel>,
    private val activity: BaseActivity,
    private var recyclerViewItemListener: RecyclerViewItemListener
) : RecyclerView.Adapter<ProductPhaseViewHolder>() {

    private var productImage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPhaseViewHolder {
        return ProductPhaseViewHolder(
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_series,
                parent,
                false
            ), activity = activity,
            listener = recyclerViewItemListener
        )
    }

    override fun getItemCount(): Int = productsList.size

    override fun onBindViewHolder(holder: ProductPhaseViewHolder, position: Int) {
        val model = productsList[position]
        holder.bindView(model, productImage = productImage)
    }

    override fun getItemViewType(position: Int): Int {
        return productsList[position].id.toInt()
    }

    internal fun updateProductImage(image: String?) {
        productImage = image!!
    }
}