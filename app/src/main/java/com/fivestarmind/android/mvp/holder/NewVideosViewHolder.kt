package com.fivestarmind.android.mvp.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.NewVideosModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_program_new_videos.view.*

class NewVideosViewHolder(
    itemView: View,
    private var activity: BaseActivity,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private val TAG = NewVideosViewHolder::class.java.simpleName
    private var newVideosModel = NewVideosModel()

    fun bindView(model: NewVideosModel) {
        this.newVideosModel = model

        itemView.apply {
            newVideosModel?.apply {
                updateProductImage(thumbnail = thumbnail)
            }

            clRoot.setOnClickListener {
                clickedDetail()
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
                    .into(ivProduct, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            ivProduct.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivProduct.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * It is called when user click on detail
     */
    private fun clickedDetail() {
        itemView.apply {
            if (AppHelper.isUserLoggedIn()) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_PHASE_VIDEO,
                    model = newVideosModel,
                    position = layoutPosition,
                    view = itemView
                )
            } else
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_VIDEO,
                    model = newVideosModel,
                    position = layoutPosition,
                    view = itemView
                )
        }
    }
}