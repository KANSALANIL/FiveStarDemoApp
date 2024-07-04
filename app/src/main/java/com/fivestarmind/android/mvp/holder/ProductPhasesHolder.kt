package com.fivestarmind.android.mvp.holder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.ProductPhaseListingActivity
import com.fivestarmind.android.mvp.model.response.FavoriteResponseDataModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_product_phase_list.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductPhasesHolder(
    itemView: View, private var activity: ProductPhaseListingActivity,
    private val listener: RecyclerViewItemListener
) :
    RecyclerView.ViewHolder(itemView) {

    private var videoModel: FavoriteResponseDataModel? = null
    private var TAG = ProductPhasesHolder::class.java.simpleName

    fun bindView(
        model: FavoriteResponseDataModel
    ) {
        this.videoModel = model

        videoModel?.apply {
            when (type) {
                activity.getString(R.string.type_video) -> updateWithPhaseVideoUI(model = model)
                activity.getString(R.string.type_pdf) -> updatePdfUI(model = model)
            }

            itemView.apply {
                clRoot.setOnClickListener {
                    clickedDetail(model = model)
                }

                ivHeart.setOnClickListener {
                    if (Constants.App.ProceedClick.shouldProceedClick())
                        clickedFavorite()
                }

                ivMaster.setOnClickListener {
                    if (Constants.App.ProceedClick.shouldProceedClick())
                        clickedMastered()
                }
            }
        }
    }

    /**
     * Here is updating the pdf UI
     */
    private fun updatePdfUI(model: FavoriteResponseDataModel) {
        hidePhaseVideoUI()
        model.apply {
            updateVideoName(productName = title)
            if (!thumbpath.isNullOrEmpty())
                updateVideoImage(thumbnail = thumbpath)
            else itemView.ivPhase.setImageResource(R.drawable.ic_pdf)
        }

        itemView.ivPhase.scaleType = ImageView.ScaleType.CENTER_CROP;
    }

    /**
     * Here is updating the Phase Video UI
     */
    private fun updateWithPhaseVideoUI(model: FavoriteResponseDataModel) {
        showPhaseVideoUI()

        model.apply {
            updateVideoName(productName = title)

            if (!thumbpath.isNullOrEmpty())
                updateVideoImage(thumbnail = thumbpath)
            else updateVideoImage(thumbnail = thumbnail)

            updateFavoriteStatusView(isMyFav = isMyFav)
            updateTime(time = duration)
            updateMasteredStatusView(isMasteredByMe = isMasteredByMe)
            if (videoView == null) updateVideoProgress(
                maxDuration = duration,
                videoDuration = "00:00:00"
            )
            else updateVideoProgress(
                maxDuration = duration,
                videoDuration = videoView!!.duration!!
            )
        }
    }

    /**
     * show Video UI
     */
    private fun showPhaseVideoUI() {
        itemView.ivPlayVideo.visibility = View.VISIBLE
        itemView.ivMaster.visibility = View.VISIBLE
        itemView.ivHeart.visibility = View.VISIBLE
        itemView.tvTime.visibility = View.VISIBLE
        itemView.pbProgram.visibility = View.VISIBLE
    }

    /**
     * hide Video UI
     */
    private fun hidePhaseVideoUI() {
        itemView.ivPlayVideo.visibility = View.INVISIBLE
        itemView.ivMaster.visibility = View.INVISIBLE
        itemView.ivHeart.visibility = View.INVISIBLE
        itemView.tvTime.visibility = View.INVISIBLE
        itemView.pbProgram.visibility = View.INVISIBLE

    }

    /**
     * Here is updating the favorite product name
     */
    private fun updateVideoName(productName: String?) {
        itemView.apply {
            productName?.apply {
                tvName.text = this
            }
        }
    }

    /**
     * Here is updating the favorite product image
     */
    private fun updateVideoImage(thumbnail: String?) {
        itemView.apply {
            progressBarMyPrograms.visibility = View.VISIBLE

            if (!thumbnail.isNullOrEmpty())
                Picasso.get()
                    .load(thumbnail)
                    .into(ivPhase, object : Callback {
                        override fun onSuccess() {
                            progressBarMyPrograms.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBarMyPrograms.visibility = View.GONE
                            ivPhase.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivPhase.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the time
     */
    private fun updateTime(time: String?) {
        itemView.apply {
            time?.apply {
                tvTime.text = this
            }
        }
    }

    /**
     * Here updating the favorite status view
     *
     * @param isMyFav favorite status value
     */
    private fun updateFavoriteStatusView(isMyFav: Int?) {
        itemView.apply {
            isMyFav?.apply {
                ivHeart.isSelected = Constants.VALUE.TRUE == this
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
     * It is called when user click on detail
     */
    private fun clickedDetail(model: FavoriteResponseDataModel) {
        itemView.apply {
            model.apply {

                when (type) {
                    activity.getString(R.string.type_video) -> if (AppHelper.isUserLoggedIn()) {
                        callToPlayVideo()
                    } else listener.onRecyclerViewItemClick(
                        itemClickType = ItemClickType.PLAY_VIDEO,
                        model = videoModel!!,
                        position = layoutPosition,
                        view = itemView
                    )

                    activity.getString(R.string.type_pdf) -> listener.onRecyclerViewItemClick(
                        itemClickType = ItemClickType.PDF,
                        model = videoModel!!,
                        position = layoutPosition,
                        view = itemView
                    )


                }


            }
        }
    }

    /**
     * It is called when user clicked heart view
     */
    private fun clickedFavorite() {
        videoModel?.apply {
            isMyFav =
                if (isMyFav == Constants.VALUE.TRUE) Constants.VALUE.FALSE
                else Constants.VALUE.TRUE

            updateFavoriteStatusView(isMyFav = isMyFav)
        }

        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            model = videoModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked master view
     */
    private fun clickedMastered() {
        if (videoModel?.isMasteredByMe == Constants.VALUE.FALSE) {
            videoModel?.isMasteredByMe = Constants.VALUE.TRUE

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.MASTER,
                model = videoModel!!,
                position = layoutPosition,
                view = itemView
            )

            updateMasteredStatusView(isMasteredByMe = videoModel?.isMasteredByMe)
        }
    }

    private fun getProgressDuration(duration: String): Long {
        var videoDuration: String? = null
        videoDuration = if (duration.isEmpty())
            "00:00:00"
        else duration

        val format: DateFormat = SimpleDateFormat("HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = format.parse(videoDuration)!!
        return date.time / 1000L
    }

    private fun callToPlayVideo() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.PLAY_PHASE_VIDEO,
            model = videoModel!!,
            position = layoutPosition,
            view = itemView
        )
    }
}