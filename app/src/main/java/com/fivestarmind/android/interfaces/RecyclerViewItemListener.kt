package com.fivestarmind.android.interfaces

import android.view.View
import com.fivestarmind.android.enum.ItemClickType

/**
 * Interface definition for a callback to be invoked when a view of RecyclerView's item
 * is clicked.
 */
interface RecyclerViewItemListener {

    /**
     * Called when an item's view has been clicked
     *
     * @param itemClickType type of the item which has been clicked
     * @param model model whose options are requested
     * @param position index of the list on which user clicked
     * @param view reference of the view on which user clicked
     */
    fun onRecyclerViewItemClick(itemClickType: ItemClickType, model: Any?, position: Int, view: View)

}
