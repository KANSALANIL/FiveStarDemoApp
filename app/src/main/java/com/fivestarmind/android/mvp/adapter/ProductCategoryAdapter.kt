package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.ProductCategoryHolder
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel

class ProductCategoryAdapter(
    private var activity: BaseActivity,
    private val productCategoryResponseList: ArrayList<ProductCategoryDataModel>?,
    private val programInterface: ProgramInterface,
) : RecyclerView.Adapter<ProductCategoryHolder>() {
     var number: Int = 0

    private var categoryId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductCategoryHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ProductCategoryHolder(
            itemView,
            activity = activity,
            programInterface = programInterface
        )
    }

    fun updateCategoryId(categoryId: String) {
        this.categoryId = categoryId
    }

    override fun getItemCount(): Int = productCategoryResponseList!!.size


    override fun onBindViewHolder(holder: ProductCategoryHolder, position: Int) {
        holder.bindView(productCategoryResponseList!![position], categoryId)

    }
}