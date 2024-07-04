package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.holder.PerformanceHolder

class PerformanceAdapter(
    private var activity: BaseActivity,
    private val programInterface: ProgramInterface,
) : RecyclerView.Adapter<PerformanceHolder>() {

    private var categoryId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PerformanceHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_performance, parent, false)
        return PerformanceHolder(
            itemView,
            activity = activity,
            programInterface = programInterface
        )
    }

    fun updateCategoryId(categoryId: String) {
        this.categoryId = categoryId
    }

    override fun getItemCount(): Int = 5
//    override fun getItemCount(): Int = productCategoryResponseList!!.size

    override fun onBindViewHolder(holder: PerformanceHolder, position: Int) {
//        holder.bindView(productCategoryResponseList!![position], categoryId)
    }
}