package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_images.view.ivItemImage
import kotlinx.android.synthetic.main.item_images.view.progressBar
import kotlinx.android.synthetic.main.item_my_favorite.view.clBookmarks
import kotlinx.android.synthetic.main.item_my_favorite.view.clRoot
import kotlinx.android.synthetic.main.item_my_favorite.view.ivBookMark
import kotlinx.android.synthetic.main.item_my_favorite.view.ivFavProgram
import kotlinx.android.synthetic.main.item_my_favorite.view.ivMaster
import kotlinx.android.synthetic.main.item_my_favorite.view.ivPlayVideo
import kotlinx.android.synthetic.main.item_my_favorite.view.pbImage
import kotlinx.android.synthetic.main.item_my_favorite.view.pbProgram
import kotlinx.android.synthetic.main.item_my_favorite.view.tvDiscription
import kotlinx.android.synthetic.main.item_my_favorite.view.tvFavProgramName
import kotlinx.android.synthetic.main.item_my_favorite.view.tvTimeDuration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class AllVideoListHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var seeAllDataItem: SeeAllDataItem? = null
    private var TAG = AllVideoListHolder::class.java.simpleName

    fun bindView(
        activity: Context,
        model: SeeAllDataItem
    ) {
        seeAllDataItem = model

        itemView.ivBookMark.isSelected = seeAllDataItem!!.isFavourite


        seeAllDataItem?.apply {

            if (title != null) {
                updateFavProductName(productName = title!!)
            }
            if (description != null) {
                updateFavProductDiscription(productDiscription = htmlToStringFilter(description!!))
            }

            var drawableIcon = 0
            if (type!!.contains("pdf")) {
                drawableIcon = R.drawable.ic_pdf
            } else if (type!!.contains("video")) {
                drawableIcon = R.drawable.ic_video
            } else if (type!!.contains("audio")) {
             //  drawableIcon = R.drawable.ic_audio
               drawableIcon = R.drawable.ic_mp3
            }


            if (type!!.contains("image")) {
                if (image != null) {

                    updateFavProductImage(image = image!!, drawableIcon)
                }

            } else {

                if (thumbpath != null) {

                    updateFavProductImage(image = thumbpath!!, drawableIcon)
                } else {

                    itemView.ivFavProgram.setImageDrawable(
                        activity.resources.getDrawable(
                            drawableIcon
                        )
                    )

                }
            }

            if (videoView == null || videoView!!.duration == null) {

                updateVideoProgress(duration, "00:00:00")

            } else {
                if (videoView != null) {

                    updateVideoProgress(duration, videoView!!.duration)

                }

            }
        }

        // Handle click event on view
        itemView.apply {
            clBookmarks.setOnClickListener {

                clickedFavorite()
                //  selectOrUnselectBookMark(ivBookMark)

            }
            clRoot.setOnClickListener {
                clickedDetail(activity = activity)
            }

        }

        if (seeAllDataItem?.type!!.contains("pdf") || seeAllDataItem?.type!!.contains("image")) {
            itemView.ivPlayVideo.visibility = View.GONE
            itemView.tvTimeDuration.visibility = View.GONE
            itemView.pbProgram.visibility = View.GONE
        } else {
            itemView.ivPlayVideo.visibility = View.VISIBLE
            itemView.tvTimeDuration.visibility = View.VISIBLE
            itemView.pbProgram.visibility = View.VISIBLE

            if (seeAllDataItem?.duration != null) {
                updateTime(AppHelper.getTimeFromat(seeAllDataItem!!.duration!!))
            } else {

                // itemView.tvTimeDuration.setText(" ")


                //itemHolder.tvTimeDuration.setText(" ");
                itemView.tvTimeDuration.visibility = View.GONE

            }

            /* itemView.apply {
                 seeAllDataItem?.duration?.apply {
                     tvTimeDuration.text = this
                 }
             }
 */

        }
    }


    /**
     * here get Video Duration from video url
     * @param url
     * @return
     */
    fun getVideoDuration(url: String?): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(url, HashMap())
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillisec = time!!.toLong()
        retriever.release()
        val duration = convertMillieToHMmSs(timeInMillisec) //use this duration
        Log.d(TAG, "Videos_duration: $duration")
        return duration
    }

    /**
     * here convertMillieToHMmSs
     * @param millie
     * @return
     */
    fun convertMillieToHMmSs(millie: Long): String {
        val seconds = millie / 1000
        val second = seconds % 60
        val minute = seconds / 60 % 60
        val hour = seconds / (60 * 60) % 24
        val result = ""
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
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
     * Here is updating the favorite product name
     */
    private fun updateFavProductDiscription(productDiscription: String?) {
        itemView.apply {
            productDiscription?.apply {
                tvDiscription.text = this
            }
        }
    }


    /**
     * Here is updating the favorite product image
     */
    private fun updateFavProductImage(image: String, drawerIcon: Int) {

        itemView.apply {
            pbImage!!.visibility = View.VISIBLE

            Picasso.get()
                .load(ApiClient.BASE_URL_MEDIA + image)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivFavProgram, object : Callback {
                    override fun onSuccess() {
                        pbImage.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        pbImage.visibility = View.GONE
                        ivFavProgram.setImageDrawable(context.resources.getDrawable(drawerIcon))

                    }
                })


         /*   Glide.with(context)
                .load(ApiClient.BASE_URL_MEDIA + image).placeholder(drawerIcon).listener(object :
                    RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbImage.visibility = View.GONE
                        ivFavProgram.setImageDrawable(context.resources.getDrawable(drawerIcon))
                        return false

                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbImage.visibility = View.GONE
                        return false
                    }

                }).diskCacheStrategy(
                    DiskCacheStrategy.ALL
                )
                .into(ivFavProgram)*/


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
        Log.d("Update", "===> " + maxDuration + " || " + videoDuration)

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
            model = seeAllDataItem!!,
            position = layoutPosition,
            view = itemView
        )
    }

    /**
     * It is called when user clicked master view
     */
    /*    private fun clickedMastered() {
            if (favoriteResponseDataModel?.isMasteredByMe == Constants.VALUE.FALSE) {
                favoriteResponseDataModel?.isMasteredByMe = Constants.VALUE.TRUE

                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.MASTER,
                    // model = favoriteResponseDataModel!!,
                    model = null,
                    position = layoutPosition,
                    view = itemView
                )

                updateMasteredStatusView(isMasteredByMe = favoriteResponseDataModel?.isMasteredByMe)
            }
        }*/

    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(activity: Context) {
        itemView.apply {
            if (seeAllDataItem!!.type!!.contains("video")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_VIDEO,
                    model = seeAllDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (seeAllDataItem!!.type!!.contains("pdf")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_PDF,
                    model = seeAllDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (seeAllDataItem!!.type!!.contains("audio")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_AUDIO,
                    model = seeAllDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if (seeAllDataItem!!.type!!.contains("image")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_IMAGE,
                    model = seeAllDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            }
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

    /**
     * here remove HTML tags from a String
     */
    fun htmlToStringFilter(textToFilter: String?): String {
        return Html.fromHtml(textToFilter).toString()
    }


    internal class BackgroundImageTask(var iv: ImageView, var pb: ProgressBar, var temUrl: String) :
        AsyncTask<Void?, Void?, Bitmap?>() {
        override fun doInBackground(vararg p0: Void?): Bitmap? {
            var bit1: Bitmap? = null
            try {
                bit1 = AppHelper.retrieveVideoFrameFromVideo(temUrl)
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            return bit1
        }

        override fun onPostExecute(bit: Bitmap?) {
            pb.visibility = View.GONE
            iv.setImageBitmap(bit)
        }
    }

}