package com.fivestarmind.android.mvp.holder


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel

class PerformanceHolder(
    itemView: View,
    activity: BaseActivity,
    private var programInterface: ProgramInterface
) :
    RecyclerView.ViewHolder(itemView) {

    private var productCategoryDataModel: ProductCategoryDataModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (activity.shouldProceedClick())
            when (view.id) {
             //   R.id.clProgramsRoot -> clickedDetail()
            }
    }

    init {
        itemView.apply {
         //   pe.setOnClickListener(clickListener)
        }
    }

    fun bindView(
        responseModel: ProductCategoryDataModel,
        categoryId: String
    ) {


    }

    }
