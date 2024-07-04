package com.fivestarmind.android.mvp.holder

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.activity.ProductDetailActivity
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_series.view.*

class ProductPhaseViewHolder(
    itemView: View,
    private var activity: BaseActivity,
    private var listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var phasesModel: PhaseVideoModel? = null

    private var productImage: String? = null

    fun bindView(model: PhaseVideoModel, productImage: String?) {
        this.phasesModel = model
        this.productImage = productImage

        itemView.apply {
            phasesModel?.apply {
                when (listType) {
                    context.getString(R.string.without_phase) -> updateWithoutPhaseVideoUI()
                    context.getString(R.string.with_phase) -> updateWithPhaseVideoUI()
                }
            }

            // Handle click event on view
            clRoot.setOnClickListener {
                if (Constants.App.ProceedClick.shouldProceedClick()) {
                    clickedDetail(activity = activity)
                }
            }

            ivFavorite.setOnClickListener {
                if (Constants.App.ProceedClick.shouldProceedClick()) {
                    clickedFavorite()
                }
            }
        }
    }

    private fun updateWithoutPhaseVideoUI() {
        phasesModel?.apply {
            updateTitleForWithoutPhaseVideo()

            updateProductImage(thumbnail = thumbnail)
            updateFavoriteStatusView(isMyFav = isMyFav)
        }
    }

    private fun updateWithPhaseVideoUI() {
        phasesModel?.apply {
            itemView.apply {
                ivPlayVideo.visibility = View.INVISIBLE
            }

            updateProductTitle(productTitle = title)
            updateProductSubTitle(subTitle = subtitle)

            if (thumbnail.isEmpty()) {
                if (videoListing.isNotEmpty())
                    updateProductImage(thumbnail = videoListing[Constants.App.Number.ZERO].thumbnail)
                else updateProductImage(thumbnail = "")
            } else
                updateProductImage(thumbnail = thumbnail)

            updateVideoCount(videoCount = videoListing.size)
        }
    }

    /**
     * Here is updating the title of the without phase video
     */
    private fun updateTitleForWithoutPhaseVideo() {
        itemView.apply {
            tvTitle.text = phasesModel!!.title

            tvSubTitle.visibility = View.INVISIBLE
            tvVideosCount.visibility = View.INVISIBLE

            ivPlayVideo.visibility = View.VISIBLE
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

            tvSubTitle.visibility = View.VISIBLE
            tvVideosCount.visibility = View.VISIBLE
            ivFavorite.visibility = View.INVISIBLE
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
                tvSubTitle.text = this
            }
        }
    }

    /**
     * Here is updating the product image
     *
     * @param thumbnail
     */
    private fun updateProductImage(thumbnail: String) {
        itemView.apply {
            if (thumbnail.isNotEmpty())
                Picasso.get()
                    .load(thumbnail)
                    .into(ivProductImage, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            ivProductImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivProductImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the product video count
     *
     * @param videoCount
     */
    private fun updateVideoCount(videoCount: Int?) {
        itemView.apply {
            videoCount?.apply {
                tvVideosCount.text = AppHelper.formatVideoLeft(
                    context = context,
                    video = this
                )
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
                ivFavorite.isSelected = Constants.VALUE.TRUE == this
            }
        }
    }

    /**
     * It is called when user clicked detail
     */
    private fun clickedDetail(activity: BaseActivity) {
        itemView.apply {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra(Constants.App.PRODUCT_ID, phasesModel?.productId)
            intent.putExtra(Constants.App.IMAGE, productImage)
            activity.startActivity(intent)
        }
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
}