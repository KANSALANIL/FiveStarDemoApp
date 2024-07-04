package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.media.MediaMetadataRetriever
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.BookMarkDataItem
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_favorite.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BookMarkHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var bookMarkDataItem: BookMarkDataItem? = null
    private var TAG = BookMarkHolder::class.java.simpleName

    fun bindView(
        activity: Context,
        model: BookMarkDataItem
    ) {
        bookMarkDataItem = model

        if (model?.type!!.contains("pdf") || model?.type!!.contains("image")) {
            itemView.ivPlayVideo.setVisibility(View.GONE)
            itemView.tvTimeDuration.setVisibility(View.GONE)
            itemView.pbProgram.setVisibility(View.GONE)
        } else {
            itemView.ivPlayVideo.setVisibility(View.VISIBLE)
            itemView.tvTimeDuration.setVisibility(View.VISIBLE)
            itemView.pbProgram.setVisibility(View.VISIBLE)
            //set duration

            if(bookMarkDataItem?.duration!=null) {
                updateDurationTime(AppHelper.getTimeFromat(bookMarkDataItem!!.duration!!))
            } else {
               // itemView.tvTimeDuration.setText(" ")

                itemView.tvTimeDuration.visibility =View.GONE
            }

            //  updateFavProductImage(thumbpath = bookMarkDataItem?.duration!!)

        }


        if (model.videoView == null || model.videoView!!.duration == null) {

            updateVideoProgress(model.duration, "00:00:00")

        }else{
            if (model.videoView != null) {

                updateVideoProgress(model.duration, model.videoView!!.duration)

            }

        }

        bookMarkDataItem?.apply {

            updateFavProductName(productName = title)
            updateDescription(description = htmlToStringFilter(description))

            var drawableIcon = 0
            if (bookMarkDataItem!!.type!!.contains("pdf")) {
                drawableIcon = R.drawable.ic_pdf
            } else if (bookMarkDataItem!!.type!!.contains("video")) {
                drawableIcon = R.drawable.ic_video
            }else if (bookMarkDataItem!!.type!!.contains("audio")){
             //   drawableIcon = R.drawable.ic_audio
                drawableIcon = R.drawable.ic_mp3

            }else if (bookMarkDataItem!!.type!!.contains("image")){
                drawableIcon = R.drawable.ic_image_placeholder

            }

            if (thumbpath!=null) {
                updateFavProductImage(thumbpath = thumbpath!!,drawableIcon)
            } else if (image!=null){

                updateFavProductImage(thumbpath = image!!,drawableIcon)

            }
            else{

                itemView.ivFavProgram.setImageDrawable(activity.resources.getDrawable(drawableIcon))

            }


            itemView.ivBookMark.isSelected = isFavourite

        }

        // Handle click event on view
        itemView.apply {
            ivBookMark.setOnClickListener {

                clickedFavorite()

                // selectOrUnselectBookMark(ivBookMark)

            }

            clRoot.setOnClickListener {
                clickedDetail(activity = activity)
            }
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


    private fun updateDescription(description: String?) {
        itemView.apply {
            description?.apply {
                tvDiscription.text = this
            }

        }

    }

    private fun updateDurationTime(duration: String?) {
        itemView.apply {
            duration?.apply {
                tvTimeDuration.text = this
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
        } else {
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
    private fun updateFavProductImage(thumbpath: String,drawableIcon:Int) {

        itemView.apply {

            if (!thumbpath.isNullOrEmpty()){
                pbImage.visibility = View.VISIBLE

                Picasso.get()
                    .load(ApiClient.BASE_URL_MEDIA + thumbpath)
                    .into(ivFavProgram, object : Callback {
                        override fun onSuccess() {
                            pbImage.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            pbImage.visibility = View.GONE
                            ivFavProgram.setImageDrawable(context.resources.getDrawable(drawableIcon))
                        }
                    })
            } else {
                ivFavProgram.setImageDrawable(context.resources.getDrawable(drawableIcon))
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

        itemView.ivBookMark.isSelected = false

        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.LIKE_UNLIKE,
            model = bookMarkDataItem!!,
            position = adapterPosition,
            view = itemView
        )
    }


    /**
     * It is called when user click on detail
     */
    private fun clickedDetail(activity: Context) {
        itemView.apply {

            if (bookMarkDataItem!!.type!!.contains("video")) {

                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_VIDEO,
                    model = bookMarkDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            }else if(bookMarkDataItem!!.type!!.contains("pdf")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_PDF,
                    model = bookMarkDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            }else if(bookMarkDataItem!!.type!!.contains("audio")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.PLAY_AUDIO,
                    model = bookMarkDataItem!!,
                    position = layoutPosition,
                    view = itemView
                )
            } else if(bookMarkDataItem!!.type!!.contains("image")) {
                listener.onRecyclerViewItemClick(
                    itemClickType = ItemClickType.OPEN_IMAGE,
                    model = bookMarkDataItem!!,
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
    fun htmlToStringFilter(textToFilter: String?): String? {
        var text:String? = null;

         if(textToFilter!=null){
             text=  Html.fromHtml(textToFilter).toString()
         }else{
             text=""
         }
        return text;
    }

}