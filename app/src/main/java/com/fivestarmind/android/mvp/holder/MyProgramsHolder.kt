package com.fivestarmind.android.mvp.holder

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.activity.MyProgramsActivity
import com.fivestarmind.android.mvp.activity.ProductDetailActivity
import com.fivestarmind.android.mvp.model.response.MyProgramsResponseDataModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_program.view.*

class MyProgramsHolder(itemView: View, private var activity: MyProgramsActivity) :
    RecyclerView.ViewHolder(itemView) {

    fun bindView(
        model: MyProgramsResponseDataModel
    ) {
        model.apply {
            updateProductName(productName = productName)
            updateProductImage(thumbnail = thumbnail)
            updateVideoCount(count = videoCount)
        }

        // Handle click event on view
        itemView.setOnClickListener {
            clickedDetail(model = model)
        }
    }

    /**
     * Here is updating the product name
     */
    private fun updateProductName(productName: String) {
        itemView.apply {
            tvProgramName.text = productName
        }
    }

    /**
     * Here is updating the product image
     */
    private fun updateProductImage(thumbnail: String) {
        itemView.apply {
            progressBarMyPrograms.visibility = View.VISIBLE

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
                })
        }
    }

    /**
     * Here is updating the video count
     */
    private fun updateVideoCount(count: Int?) {
        itemView.apply {
            tvVideoCount.text = AppHelper.formatVideoLeft(
                context = context,
                video = count
            )
        }
    }

    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(model: MyProgramsResponseDataModel) {
        if (Constants.App.ProceedClick.shouldProceedClick()) {
            val intent = Intent(activity, ProductDetailActivity::class.java)
            intent.putExtra(Constants.App.PRODUCT_ID, model.productId.toString())
            intent.putExtra(Constants.App.IMAGE, model.thumbnail)
            activity.startActivity(intent)
        }
    }
}