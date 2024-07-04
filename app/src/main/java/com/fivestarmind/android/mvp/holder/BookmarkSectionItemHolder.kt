package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.BookMarkSectionItemAdapter
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem
import com.fivestarmind.android.mvp.model.response.Product
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_bookmarks_all_category.view.cvBookmarkItem
import kotlinx.android.synthetic.main.item_bookmarks_all_category.view.ivBookmarkImageItem
import kotlinx.android.synthetic.main.item_bookmarks_all_category.view.pbBookmarkItem

class BookmarkSectionItemHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var TAG = BookmarkSectionItemHolder::class.java.simpleName

    private var model: BookmarkFilesItem? = null
    var mAudioList: ArrayList<SeeAllDataItem>? = null
    var mAudioFilesItem: SeeAllDataItem? = null
    var product: Product? = null

    fun bindView(
        activity: Context,
        bookmarkFilesItem: BookmarkFilesItem,
        bookmarkFilesItemList: ArrayList<BookmarkFilesItem>

    ) {
        model = bookmarkFilesItem
        mAudioList = ArrayList()

        bookmarkFilesItem.apply {

            var drawableIcon = 0
            if (type!!.contains("pdf")) {
                drawableIcon = R.drawable.ic_pdf
            } else if (type.contains("video")) {
                drawableIcon = R.drawable.ic_video
            } else if (type.contains("audio")) {
                //drawableIcon = R.drawable.ic_audio
                drawableIcon = R.drawable.ic_mp3
            }


            if (type.contains("image")) {
                if (image != null) {

                    updateFavProductImage(image = image, drawableIcon)
                }

            } else {

                if (thumbpath != null) {

                    updateFavProductImage(image = thumbpath!!, drawableIcon)
                } else {

                    itemView.ivBookmarkImageItem.setImageDrawable(
                        activity.resources.getDrawable(
                            drawableIcon
                        )
                    )

                }
            }
        }


        itemView.apply {
            cvBookmarkItem.setOnClickListener {
                clickedDetail(activity, bookmarkFilesItemList)
            }


        }
        // progressBarItem
    }

    /**
     * Here is updating the favorite product image
     */
    private fun updateFavProductImage(image: String, drawerIcon: Int) {

        itemView.apply {
            pbBookmarkItem!!.visibility = View.VISIBLE

            //   if (thumbnail.isNotEmpty())
            Picasso.get()
                .load(ApiClient.BASE_URL_MEDIA + image)
                .into(ivBookmarkImageItem, object : Callback {
                    override fun onSuccess() {
                        pbBookmarkItem.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        pbBookmarkItem.visibility = View.GONE
                        ivBookmarkImageItem.setImageDrawable(
                            context.resources.getDrawable(
                                drawerIcon
                            )
                        )
                    }
                })


            //else ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }


    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(
        activity: Context,
        bookmarkFilesItemList: ArrayList<BookmarkFilesItem>
    ) {
        itemView.apply {
            if (model!!.type!!.contains("video")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_VIDEO,
                    model = model!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (model!!.type!!.contains("pdf")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_PDF,
                    model = model!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (model!!.type!!.contains("audio")) {
                if (AppController.simpleExoplayer!!.playbackState == 4) {
                    AppController.audioStateEnd = "true"
                    AppController.simpleExoplayer!!.seekTo(0)
                    AppController.simpleExoplayer!!.playWhenReady = true
                } else {
                    AppController.audioStateEnd = "false"

                }

                AppController.audioList!!.clear()
                mAudioList!!.clear()

                for (i in 0 until bookmarkFilesItemList.size) {
                    mAudioFilesItem = SeeAllDataItem()
                    product = Product()
                    if (bookmarkFilesItemList.get(i).thumbpath != null) {
                        mAudioFilesItem!!.thumbpath = bookmarkFilesItemList.get(i).thumbpath
                    }
                    mAudioFilesItem!!.id = bookmarkFilesItemList.get(i).id
                    mAudioFilesItem!!.tempPath = bookmarkFilesItemList.get(i).tempPath
                    mAudioFilesItem!!.isFavourite = bookmarkFilesItemList.get(i).isFavourite!!
                    mAudioFilesItem!!.title = bookmarkFilesItemList.get(i).title

                    product!!.name = bookmarkFilesItemList.get(i).product?.name
                    mAudioFilesItem!!.product = product

                    mAudioList!!.add(mAudioFilesItem!!)

                }

                AppController.audioList!!.addAll(mAudioList!!)


                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_AUDIO,
                    model = model!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (model!!.type!!.contains("image")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_IMAGE,
                    model = model!!,
                    position = layoutPosition,
                    view = itemView
                )
            }
        }
    }

}