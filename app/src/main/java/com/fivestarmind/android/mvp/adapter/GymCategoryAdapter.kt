package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.GymCategoryHolder
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel

class GymCategoryAdapter(
    private var activity: BaseActivity,
    private val productCategoryResponseList: ArrayList<ProductCategoryDataModel>?,
    private val listener: RecyclerViewItemListener,
) : RecyclerView.Adapter<GymCategoryHolder>() {
    var number: Int = 0

    private var categoryId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GymCategoryHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gym_category, parent, false)
        return GymCategoryHolder(
            itemView,
            activity = activity,
            listener = listener
        )
    }

    fun updateCategoryId(categoryId: String) {
        this.categoryId = categoryId
    }

    override fun getItemCount(): Int = productCategoryResponseList!!.size


    override fun onBindViewHolder(holder: GymCategoryHolder, position: Int) {
        holder.bindView(productCategoryResponseList!![position],productCategoryResponseList)

    }
}