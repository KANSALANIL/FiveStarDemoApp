package com.fivestarmind.android.mvp.holder


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import kotlinx.android.synthetic.main.item_category.view.*

class ProductCategoryHolder(
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
               R.id.clProgramsRoot -> clickedDetail()
            }
    }

    init {
        itemView.apply {
            clProgramsRoot.performClick()
            clProgramsRoot.setOnClickListener(clickListener)
        }
    }

    fun bindView(
        responseModel: ProductCategoryDataModel,
        categoryId: String
    ) {

        this.productCategoryDataModel = responseModel

        this.productCategoryDataModel?.apply {
            if (categoryId == responseModel.id)
                showSelectedUI(image = responseModel.image2, categoryName = responseModel.name)
            else showUnselectedUI(image = responseModel.image2, categoryName = responseModel.name)
        }
    }

    private fun showSelectedUI(image: String, categoryName: String) {
        itemView.apply {
            clProgramsRoot.isSelected = true
            updateProductName(categoryName = categoryName)
        }
    }

    private fun showUnselectedUI(image: String, categoryName: String) {
        itemView.apply {
            clProgramsRoot!!.isSelected = false
            updateProductName(categoryName = categoryName)
        }
    }

    /**
     * Here is updating the name of the product category
     */
    private fun updateProductName(categoryName: String?) {
        itemView.apply {
            categoryName?.apply {
                tvCategoryName.text = this
            }
        }
    }

    /**
     * It is called when user click on view
     */
    private fun clickedDetail() {

        //programInterface.onProductCategorySelected(adapterPosition, 1, "Home")

        productCategoryDataModel?.apply {
            programInterface.onProductCategorySelected(adapterPosition, this.id.toInt(), name)
        }
    }
}