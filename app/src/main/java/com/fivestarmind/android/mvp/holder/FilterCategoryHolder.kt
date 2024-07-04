package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.TagAllCategoryItem
import kotlinx.android.synthetic.main.item_filter_category.view.clAllCategoryItem
import kotlinx.android.synthetic.main.item_filter_category.view.tvCategoryCount
import kotlinx.android.synthetic.main.item_filter_category.view.tvCategoryTitle
import kotlinx.android.synthetic.main.item_filter_category.view.view1
import kotlinx.android.synthetic.main.item_filter_category.view1

class FilterCategoryHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var TAG = FilterCategoryHolder::class.java.simpleName
    private var tagAllCategoryItem: TagAllCategoryItem?=null
    fun bindView(
        activity: Context,
        model: TagAllCategoryItem,
        tagAllCategoryList: ArrayList<TagAllCategoryItem>

        ) {

        tagAllCategoryItem =model
        itemView.apply {
            tvCategoryTitle.setText(tagAllCategoryItem!!.name)
            tvCategoryCount.setText(""+tagAllCategoryItem!!.filesCount!!)

            clAllCategoryItem.setOnClickListener {
                clickedAllCategoryItem()
            }

        }

        if(layoutPosition==tagAllCategoryList.size-1){
            itemView.view1.visibility =View.GONE
        }else{
            itemView.view1.visibility =View.VISIBLE

        }

    }

    /**
     * It is called when user clicked heart view
     */
    private fun clickedAllCategoryItem() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.DETAIL,
            model = tagAllCategoryItem!!,
            position = layoutPosition,
            view = itemView
        )
    }

}