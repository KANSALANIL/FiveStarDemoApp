package com.fivestarmind.android.mvp.holder

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.activity.ProductDetailActivity
import com.fivestarmind.android.mvp.model.response.AllProductsDataModel
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_program.view.*

class ProductViewHolder(
    itemView: View,
    var activity: BaseActivity,
    private var recyclerViewItemListener: RecyclerViewItemListener,
    private var isFromHome: Boolean
) :
    RecyclerView.ViewHolder(itemView) {

    private var phaseVideoList: ArrayList<PhaseVideoModel>? = null

    private var productModel: AllProductsDataModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (Constants.App.ProceedClick.shouldProceedClick())
            when (view.id) {
                R.id.clRoot -> clickedDetail()
            }
    }

    init {
        itemView.apply {
            clRoot.setOnClickListener(clickListener)
        }
    }

    fun bindView(model: AllProductsDataModel) {
        this.productModel = model

        itemView.apply {
            productModel?.apply {
                updateProductName(productName = productName)
                updateProductImage(thumbnail = image)

                updateVideoCount(videoCount = totalVideos, pdfCount = totalPdfs)
            }
        }
    }

    /**
     * Here is updating the product name
     */
    private fun updateProductName(productName: String?) {
        itemView.apply {
            productName?.apply {
                tvProgramName.text = this
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
                    .into(ivProgram, object : Callback {
                        override fun onSuccess() {
                            progressBarMyPrograms.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBarMyPrograms.visibility = View.GONE
                            ivProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
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
                tvVideoCount.text = context.getString(
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
     * It is called when user clicked detail
     */
    private fun clickedDetail() {
        itemView.apply {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra(Constants.App.PRODUCT_ID, productModel?.id)
            intent.putExtra(Constants.App.IMAGE, productModel?.image)
            activity.startActivity(intent)
        }
    }
}