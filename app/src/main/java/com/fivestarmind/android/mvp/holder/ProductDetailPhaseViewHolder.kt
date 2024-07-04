package com.fivestarmind.android.mvp.holder

import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.ProductDetailActivity
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_product_phase.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailPhaseViewHolder(
    itemView: View, private var activity: ProductDetailActivity,
    private val listener: RecyclerViewItemListener
) :
    BaseViewHolder(
        itemView = itemView,
        listener = listener
    ) {

    private var phasesModel: PhaseVideoModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (Constants.App.ProceedClick.shouldProceedClick())
            when (view.id) {
                R.id.ivHeart -> clickedFavorite()
                R.id.ivMaster -> clickedMastered()
            }
    }

    init {
        itemView.apply {
            ivHeart.setOnClickListener(clickListener)
            ivMaster.setOnClickListener(clickListener)
        }
    }

    internal fun bindView(model: PhaseVideoModel?) {
        this.phasesModel = model

        itemView.apply {
            phasesModel?.apply {
                when (listType) {
                    context.getString(R.string.without_phase) -> updateWithoutPhaseVideoUI()
                    context.getString(R.string.with_phase) -> updateWithPhaseVideoUI()
                }
            }
        }

        itemView.setOnClickListener {
            clickedDetail()
        }
    }

    /**
     * Here is updating the view w.r.t without phase of type video
     */
    private fun updateWithoutPhaseVideoUI() {
        phasesModel?.apply {
            if (type == itemView.context.getString(R.string.type_pdf)) updateWithoutPhasePdfUI()
            else {
                showUIForWithoutPhaseView()

                updateProductTitle(productTitle = title)
                updateVideoDuration(duration = duration)

                if (thumbpath.isNullOrEmpty()) {
                    updateProductImage(thumbnail = thumbnail)
                } else {
                    updateProductImage(thumbnail = thumbpath)
                }

                updateFavoriteStatusView(isMyFav = isMyFav)
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
        }
    }

    /**
     * Here is updating the view w.r.t with phase of type video
     */
    private fun updateWithPhaseVideoUI() {
        showUIForWithPhaseView()

        phasesModel?.apply {
            updateProductTitle(productTitle = title)
            updateProductSubTitle(subTitle = subtitle)

            if (thumbpath.isNullOrEmpty()) {
                if (thumbnail.isEmpty()) {
                    if (videoListing.isNotEmpty()) {
                       if(videoCount != 0) {
                           if (videoListing[Constants.App.Number.ZERO].thumbpath.isNullOrEmpty()) updateProductImage(
                               thumbnail = videoListing[Constants.App.Number.ZERO].thumbnail
                           )
                           else updateProductImage(thumbnail = videoListing[Constants.App.Number.ZERO].thumbpath)
                       } else {
                           if (videoListing[Constants.App.Number.ZERO].thumbpath.isNullOrEmpty())
                               itemView.ivPhase.setImageResource(R.drawable.ic_pdf)
                           else updateProductImage(thumbnail = videoListing[Constants.App.Number.ZERO].thumbpath)
                       }
                    } else updateProductImage(thumbnail = "")
                } else {
                    updateProductImage(thumbnail = thumbnail)
                }
            } else {
                updateProductImage(thumbnail = thumbpath)
            }

            updateVideoCount(videoCount = videoCount, pdfCount = pdfCount)
        }
    }

    /**
     * Here is updating the view w.r.t for without phase of type pdf
     */
    private fun updateWithoutPhasePdfUI() {
        showPdfUI()

        itemView.apply {
            phasesModel?.apply {
                if (!thumbpath.isNullOrEmpty()) {
                    updateProductImage(thumbnail = thumbpath)
                } else ivPhase.setImageResource(R.drawable.ic_pdf)
                updateProductTitle(productTitle = title)
            }
        }
    }

    private fun showPdfUI() {
        itemView.apply {
            tvTime.visibility = View.INVISIBLE
            ivHeart.visibility = View.GONE
            ivMaster.visibility = View.GONE
            pbVideo.visibility = View.INVISIBLE
            ivPlayVideo.visibility = View.INVISIBLE

            tvSubtitle.visibility = View.INVISIBLE
            tvVideo.visibility = View.INVISIBLE
        }
    }

    private fun showUIForWithoutPhaseView() {
        itemView.apply {
            tvTime.visibility = View.VISIBLE
            ivHeart.visibility = View.VISIBLE
            ivMaster.visibility = View.VISIBLE
            pbVideo.visibility = View.VISIBLE
            ivPlayVideo.visibility = View.VISIBLE

            tvSubtitle.visibility = View.INVISIBLE
            tvVideo.visibility = View.INVISIBLE
        }
    }

    private fun showUIForWithPhaseView() {
        itemView.apply {
            tvSubtitle.visibility = View.VISIBLE
            tvVideo.visibility = View.VISIBLE

            tvTime.visibility = View.INVISIBLE
            ivHeart.visibility = View.INVISIBLE
            ivMaster.visibility = View.INVISIBLE
            pbVideo.visibility = View.INVISIBLE
            ivPlayVideo.visibility = View.INVISIBLE
        }
    }

    /**
     * Here is updating the product title
     *
     * @param productTitle
     */
    private fun updateProductTitle(productTitle: String?) {
        itemView.apply {
            productTitle?.apply {
                tvTitle.text = this
            }
        }
    }

    /**
     * Here is updating the product subTitle
     *
     * @param subTitle
     */
    private fun updateProductSubTitle(subTitle: String?) {
        itemView.apply {
            subTitle?.apply {
                tvSubtitle.text = this
            }
        }
    }

    /**
     * Here is updating the product image
     *
     * @param thumbnail
     */
    private fun updateProductImage(thumbnail: String?) {
        itemView.apply {
            if (thumbnail!!.isNotEmpty())
                Picasso.get()
                    .load(thumbnail)
                    .into(ivPhase, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            ivPhase.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    })
            else ivPhase.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
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
     * Here is updating the product video count
     *
     * @param videoCount
     */
    private fun updateVideoCount(videoCount: Int?, pdfCount: Int?) {
        itemView.apply {
            videoCount?.apply {
                tvVideo.text = context.getString(
                    R.string.format_video_pdf, AppHelper.formatVideoCount(
                        context = context,
                        video = this
                    ), AppHelper.formatPdfCount(
                        context = context,
                        pdfCount = pdfCount
                    )
                )
            }
        }
    }

    /**
     * Here is updating the product subTitle
     *
     * @param duration
     */
    private fun updateVideoDuration(duration: String?) {
        itemView.apply {
            duration?.apply {
                tvTime!!.text = this
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
                pbVideo.max = getProgressDuration(this).toInt()
            }

            videoDuration?.apply {
                pbVideo.progress = getProgressDuration(this).toInt()
            }
        }
    }

    /**
     * It is called when user clicked detail
     */
    private fun clickedDetail() {
        itemView.apply {
            if (AppHelper.isUserLoggedIn()) {
                phasesModel?.apply {
                    when (listType) {
                        context.getString(R.string.without_phase) -> {
                            if (type == itemView.context.getString(R.string.type_pdf)) clickedPdfView()
                            else clickedWithoutPhaseVideo()
                        }

                        context.getString(R.string.with_phase) -> clickedWithPhaseVideo()
                    }
                }
            } else listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.PLAY_VIDEO,
                model = phasesModel!!,
                position = layoutPosition,
                view = itemView
            )
        }
    }

    /**
     * It is called when user click on without phase video view
     */
    private fun clickedWithoutPhaseVideo() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.PLAY_WITHOUT_PHASE_VIDEO,
            model = phasesModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user click on with phase video view
     */
    private fun clickedWithPhaseVideo() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.PLAY_PHASE_VIDEO,
            model = phasesModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user click on PDF view
     */
    private fun clickedPdfView() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.PDF,
            model = phasesModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked heart view
     */
    private fun clickedFavorite() {
        phasesModel?.apply {
            isMyFav =
                if (isMyFav == Constants.VALUE.TRUE) Constants.VALUE.FALSE
                else Constants.VALUE.TRUE

            updateFavoriteStatusView(isMyFav = isMyFav)
        }

        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            model = phasesModel!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked master view
     */
    private fun clickedMastered() {
        if (phasesModel?.isMasteredByMe == Constants.VALUE.FALSE) {
            phasesModel?.isMasteredByMe = Constants.VALUE.TRUE

            listener.onRecyclerViewItemClick(
                itemClickType = ItemClickType.MASTER,
                model = phasesModel!!,
                position = layoutPosition,
                view = itemView
            )

            updateMasteredStatusView(isMasteredByMe = phasesModel?.isMasteredByMe)
        }
    }

    private fun getProgressDuration(duration: String): Long {
        var videoDuration: String? = null
        videoDuration = if (duration.isEmpty())
            "00:00:00"
        else duration

        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = dateFormat.parse(videoDuration)!!
        return date.time / 1000L
    }
}