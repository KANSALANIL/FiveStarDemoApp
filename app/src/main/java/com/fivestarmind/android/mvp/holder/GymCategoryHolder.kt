package com.fivestarmind.android.mvp.holder


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import kotlinx.android.synthetic.main.item_category.view.tvCategoryName
import kotlinx.android.synthetic.main.item_filter_category.view.view1
import kotlinx.android.synthetic.main.item_gym_category.view.clCategoryName
import kotlinx.android.synthetic.main.item_gym_category.view.viewLine1

class GymCategoryHolder(
    var itemView: View,
    var activity: BaseActivity,
    var listener: RecyclerViewItemListener,
) : RecyclerView.ViewHolder(itemView) {
    private var productCategoryDataModel: ProductCategoryDataModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (activity.shouldProceedClick())
            when (view.id) {
                R.id.clCategoryName -> clickedDetail()
            }
    }

    init {
        itemView.apply {
            clCategoryName.setOnClickListener(clickListener)
        }
    }

    fun bindView(
        responseModel: ProductCategoryDataModel,
        productCategoryResponseList: ArrayList<ProductCategoryDataModel>

    ) {

        this.productCategoryDataModel = responseModel

        this.productCategoryDataModel?.apply {


            itemView.apply {

                tvCategoryName.text = name

            }

            if(layoutPosition==productCategoryResponseList.size-1){
                itemView.viewLine1.visibility =View.GONE
            }else{
                itemView.viewLine1.visibility =View.VISIBLE

            }


            /* if (categoryId == responseModel.id)
                 showSelectedUI(image = responseModel.image2, categoryName = responseModel.name)
             else showUnselectedUI(image = responseModel.image2, categoryName = responseModel.name)*/
        }
    }

    /**
     * It is called when user click on view
     */
    private fun clickedDetail() {

        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.DETAIL,
            model = productCategoryDataModel!!,
            position = layoutPosition,
            view = itemView
        )
        productCategoryDataModel?.apply {


        }
    }
}
