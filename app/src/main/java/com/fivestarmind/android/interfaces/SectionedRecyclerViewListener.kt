package com.fivestarmind.android.interfaces

import com.fivestarmind.android.mvp.adapter.sectionAdapter.ListSectionAdapter

/**
 * Interface definition for a callback to be invoked when a view of RecyclerView's item
 * is clicked.
 */
interface SectionedRecyclerViewListener {

    /**
     * Called when an item's view has been clicked
     *
     * @param itemClickType type of the item which has been clicked
     * @param model model whose options are requested
     * @param position index of the list on which user clicked
     * @param view reference of the view on which user clicked
     */
  //  fun onRecyclerViewItemClick(itemClickType: ItemClickType, model: Any?, position: Int, view: View)


    fun onItemRootViewClicked(section: ListSectionAdapter, model:Any, itemAdapterPosition: Int, type :String)

    fun onFooterRootViewClicked(section: ListSectionAdapter, itemAdapterPosition: Int)

    fun onHeaderViewClicked(section: ListSectionAdapter, productId:Int, titleName:String,itemAdapterPosition: Int)

}
