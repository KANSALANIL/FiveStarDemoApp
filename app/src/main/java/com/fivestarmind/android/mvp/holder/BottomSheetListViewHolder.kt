package com.fivestarmind.android.mvp.holder

import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.CommonResponse
import kotlinx.android.synthetic.main.item_bottom_sheet_list.view.*

/**
 * This is the viewHolder class used by BottomSheetListAdapter for showing view with category data
 *
 * @param itemView view in which data is set from CategoryItem
 * @param listener
 */
class BottomSheetListViewHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : BaseViewHolder(
    itemView = itemView,
    listener = listener
) {

    private var model: CommonResponse? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (AppHelper.shouldProceedClick())
            when (view.id) {
                R.id.clRoot -> clickedDetail()
            }
    }

    init {
        itemView.apply {
            clRoot.setOnClickListener(clickListener)
        }
    }

//    internal fun bind(model: CommonResponse) {
    internal fun bind() {
        this.model = model

        hideTopDividerLine()

        this.model?.apply {
//            binding.tvTitle.text = title
        }
    }

    /**
     * Here we are hiding top divider line for first item only
     */
    private fun hideTopDividerLine() {
        itemView.apply {
            if (adapterPosition == 0) {
                viewLine.visibility = View.GONE
            } else {
                viewLine.visibility = View.VISIBLE
            }
        }
    }

    /**
     * It is called when user click on detail view
     */
    private fun clickedDetail() {
        itemView.apply {
            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.SELECT_BOTTOMSHEET_ITEM,
                model = model,
                position = layoutPosition,
                view = itemView
            )
        }
    }

}