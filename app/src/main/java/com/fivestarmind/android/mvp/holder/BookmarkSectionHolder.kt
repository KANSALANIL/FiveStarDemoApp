package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BookMarkActivity
import com.fivestarmind.android.mvp.adapter.BookMarkSectionItemAdapter
import com.fivestarmind.android.mvp.model.response.BookmarkAllCategoryItem
import kotlinx.android.synthetic.main.item_bookmarks_section.view.clRootBookMark
import kotlinx.android.synthetic.main.item_bookmarks_section.view.rvBookmarkItem
import kotlinx.android.synthetic.main.item_bookmarks_section.view.tvSeeAll
import kotlinx.android.synthetic.main.item_bookmarks_section.view.tvSubtitle

class BookmarkSectionHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var TAG = BookmarkSectionHolder::class.java.simpleName

    private var BookmarkAllCategoryItemList:ArrayList<BookmarkAllCategoryItem>? = null
    var bookMarkSectionItemAdapter: BookMarkSectionItemAdapter? = null

    fun bindView(
        activity: Context,
        bookmarkAllCategoryItem: BookmarkAllCategoryItem? = null

    ) {
      /*  for (i in 0 until bookmarkAllCategoryItem.files!!.size) {
        }
*/

            if (bookmarkAllCategoryItem!!.files!!.size > 0){
                /*itemView.tvSubtitle.visibility =View.VISIBLE
                itemView.tvSeeAll.visibility =View.VISIBLE*/
                itemView.clRootBookMark.visibility = View.VISIBLE
                itemView.tvSubtitle.text = bookmarkAllCategoryItem.name
                itemView.tvSeeAll.text = activity.resources.getString(R.string.see_all_)

                bookMarkSectionItemAdapter =
                    BookMarkSectionItemAdapter(activity, bookmarkAllCategoryItem.files!!, listener)
                itemView.rvBookmarkItem.adapter = bookMarkSectionItemAdapter
               /* itemView.rvBookmarkItem.apply {
                    //  layoutManager = linearLayoutManager
                    adapter = bookMarkSectionItemAdapter


                }*/

            }else{
                itemView.clRootBookMark.visibility = View.GONE

                itemView.tvSubtitle.visibility =View.GONE
                itemView.tvSeeAll.visibility =View.GONE
            }



        itemView.tvSeeAll.setOnClickListener {
           /* activity.startActivity(
                Intent(activity, BookMarkActivity::class.java).putExtra(
                    Constants.App.CATEGORY_ID, bookmarkAllCategoryItem.id!!
                )
            )*/


            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.DETAIL,
                model = bookmarkAllCategoryItem,
                position = layoutPosition,
                view = itemView
            )
        }
        // progressBarItem
    }

}