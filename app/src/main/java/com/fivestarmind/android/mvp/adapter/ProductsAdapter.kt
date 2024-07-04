package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.ProductsViewHolder
import com.fivestarmind.android.mvp.model.response.AllProductsDataModel

class ProductsAdapter(
    private val productsList: ArrayList<AllProductsDataModel>,
    private val activity: BaseActivity,
    private val recyclerViewItemListener: RecyclerViewItemListener,
    private val isFromHome: Boolean
) : RecyclerView.Adapter<ProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false),
            activity = activity,
            recyclerViewItemListener = recyclerViewItemListener,
            isFromHome = isFromHome
        )
    }

    override fun getItemCount(): Int = productsList.size

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val model = productsList[position]
        holder.bindView(model)
    }

    override fun getItemViewType(position: Int): Int {
        return productsList[position].id.toInt()
    }
}