package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel
import kotlinx.android.synthetic.main.item_all_pdf.view.*

class AllPdfListHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var favoriteResponseDataModel: FavoriteResponseDataModel? = null
    private var TAG = AllPdfListHolder::class.java.simpleName

    /*fun bindView(
        model: FavoriteResponseDataModel,
        activity: AllPdfListActivity
    ) */
    fun bindView(
        activity: Context
    ) {
        //favoriteResponseDataModel = model

        /*favoriteResponseDataModel?.apply {
            updateFavProductName(productName = title)
            updateFavProductImage(thumbnail = thumbnail)
            updateTime(time = duration)
            updateMasteredStatusView(isMasteredByMe = isMasteredByMe)

            if (videoView == null || videoView?.duration == null) updateVideoProgress(
                maxDuration = duration,
                videoDuration = "00:00:00"
            )
            else updateVideoProgress(
                maxDuration = duration,
                videoDuration = videoView?.duration
            )
        }
*/
        // Handle click event on view
        itemView.apply {

            clPdfRoot.setOnClickListener {
                clickedDetail(activity = activity)
            }

            ivPdfBookMark.setOnClickListener {
                selectOrUnselectBookMark(ivPdfBookMark)
                clickedBookMark()
            }
        }
    }


    private fun selectOrUnselectBookMark(ivPdfBookMark: ImageView?) {
        if (!ivPdfBookMark!!.isSelected) {
            ivPdfBookMark.isSelected = true
            ivPdfBookMark.setImageResource(R.drawable.ic_select_bookmark)
        } else {
            ivPdfBookMark.isSelected = false

            ivPdfBookMark.setImageResource(R.drawable.ic_unselected_book_mark)

        }
    }


    /**
     * It is called when user clicked heart view
     */
    private fun clickedBookMark() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            // model = favoriteResponseDataModel!!,
            model = null,
            position = layoutPosition,
            view = itemView
        )
    }


    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(activity: Context) {
        itemView.apply {

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.OPEN_PDF,
                // model = favoriteResponseDataModel!!,
                model = null,
                position = layoutPosition,
                view = itemView
            )
        }
    }


}