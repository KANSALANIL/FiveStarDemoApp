package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_favorite.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MyFavoriteHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var favoriteResponseDataModel: FavoriteResponseDataModel? = null
    private var TAG = MyFavoriteHolder::class.java.simpleName

    fun bindView(
       // model: FavoriteResponseDataModel,
        activity: Context
    ) {
       /* favoriteResponseDataModel = model

        favoriteResponseDataModel?.apply {
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
        }*/

        // Handle click event on view
        itemView.apply {
            ivBookMark.setOnClickListener {
              //  clickedFavorite()
                selectOrUnselectBookMark(ivBookMark)
            }

           /* ivMaster.setOnClickListener {
                clickedMastered()
            }*/

            clRoot.setOnClickListener {
                clickedDetail(activity = activity)
            }
        }
    }

    /**
     * here set image selector
     */
    private fun selectOrUnselectBookMark(imageView: ImageView?) {
        if (!imageView!!.isSelected) {
            imageView.isSelected = true
            imageView.setImageResource(R.drawable.ic_select_bookmark)
        } else{
            imageView.isSelected = false

            imageView.setImageResource(R.drawable.ic_unselected_book_mark)

        }
    }


    /**
     * Here is updating the favorite product name
     */
    private fun updateFavProductName(productName: String?) {
        itemView.apply {
            productName?.apply {
                tvFavProgramName.text = this
            }
        }
    }

    /**
     * Here is updating the favorite product image
     */
    private fun updateFavProductImage(thumbnail: String) {
        itemView.apply {
            pbImage.visibility = View.VISIBLE

            if (thumbnail.isNotEmpty())
                Picasso.get()
                    .load(thumbnail)
                    .into(ivFavProgram, object : Callback {
                        override fun onSuccess() {
                            pbImage.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            pbImage.visibility = View.GONE
                            ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the time
     */
    private fun updateTime(time: String?) {
        itemView.apply {
            time?.apply {
                tvTimeDuration.text = this
            }
        }
    }

    /**
     * Here updating the mastered status view
     *
     * @param isMasteredByMe mastered status value
     */
    private fun updateMasteredStatusView(isMasteredByMe: Int?) {
        itemView.apply {
            isMasteredByMe?.apply {
                ivMaster.isSelected = Constants.VALUE.TRUE == this
            }
        }
    }

    /**
     * Here is updating video progress
     *
     * @param videoDuration
     */
    private fun updateVideoProgress(maxDuration: String?, videoDuration: String?) {
        itemView.apply {
            maxDuration?.apply {
                pbProgram.max = getProgressDuration(this).toInt()
            }

            videoDuration?.apply {
                pbProgram.progress = getProgressDuration(this).toInt()
            }
        }
    }

    /**
     * It is called when user clicked heart view
     */
    private fun clickedFavorite() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            model = favoriteResponseDataModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked master view
     */
    private fun clickedMastered() {
        if (favoriteResponseDataModel?.isMasteredByMe == Constants.VALUE.FALSE) {
            favoriteResponseDataModel?.isMasteredByMe = Constants.VALUE.TRUE

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.MASTER,
                model = favoriteResponseDataModel!!,
                position = layoutPosition,
                view = itemView
            )

            updateMasteredStatusView(isMasteredByMe = favoriteResponseDataModel?.isMasteredByMe)
        }
    }

    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(activity: Context) {
        itemView.apply {

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.PLAY_VIDEO,
               // model = favoriteResponseDataModel!!,
                model = null,
                position = layoutPosition,
                view = itemView
            )
        }
    }

    private fun getProgressDuration(duration: String?): Long {
        var videoDuration: String? = null
        videoDuration = if (duration!!.isEmpty())
            "00:00:00"
        else duration

        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = dateFormat.parse(videoDuration)!!
        return date.time / 1000L
    }
}