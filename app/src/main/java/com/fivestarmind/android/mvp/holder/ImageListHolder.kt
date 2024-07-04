package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.FilesItem
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_images.view.clRoot
import kotlinx.android.synthetic.main.item_images.view.ivBookMark
import kotlinx.android.synthetic.main.item_images.view.ivItemImage
import kotlinx.android.synthetic.main.item_images.view.progressBar
import kotlinx.android.synthetic.main.item_images.view.tvTitleName


class ImageListHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private lateinit var filesItemData: FilesItem
    private var TAG = ImageListHolder::class.java.simpleName
    fun bindView(
        activity: Context,
        filesItem: FilesItem,

        ) {
        filesItemData = filesItem

        filesItemData.apply {
            updateFavProductName(productName = title)

            if (image != null) {
                updateFavProductImage(itemImage = image!!)
            } else {
                updateFavProductImage(itemImage = "")
            }
        }
        itemView.ivBookMark.isSelected = filesItemData.isFavourite

        // Handle click event on view
        itemView.apply {

            clRoot.setOnClickListener {
                //clickedDetail(activity = activity)
            }

            ivBookMark.setOnClickListener {
                //  selectOrUnselectBookMark(ivBookMark)
                clickedBookMark()
            }
        }
    }


    private fun updateFavProductImage(itemImage: String) {

        itemView.apply {
            progressBar.visibility = View.VISIBLE

            // if (itemImage!!.isNotEmpty())


            Picasso.get()
                .load(ApiClient.BASE_URL_MEDIA + itemImage)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivItemImage, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                        //ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        //  ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))

                    }
                })

            //    ivItemImage.scaleType = ImageView.ScaleType.FIT_XY

            //else ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    private fun updateFavProductName(productName: String?) {
        //  tvTitleName
        itemView.apply {
            productName?.apply {
                tvTitleName.text = filesItemData.title
            }
        }
    }

    /**
     * here call selector of bookmar
     */
    private fun selectOrUnselectBookMark(imageView: ImageView?) {
        if (!imageView!!.isSelected) {
            imageView.isSelected = true
            imageView.setImageResource(R.drawable.ic_select_bookmark)
        } else {
            imageView.isSelected = false
            imageView.setImageResource(R.drawable.ic_unselected_book_mark)

        }
    }


    /**
     * It is called when user clicked heart view
     */
    private fun clickedBookMark() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            model = filesItemData,
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
                model = filesItemData,
                position = layoutPosition,
                view = itemView
            )
        }
    }


}