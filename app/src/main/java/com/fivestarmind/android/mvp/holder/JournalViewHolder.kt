package com.fivestarmind.android.mvp.holder

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.activity.JournalListingActivity
import com.fivestarmind.android.mvp.activity.JournalVideoPlayerActivity
import com.fivestarmind.android.mvp.model.response.JournalListingResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_journal.view.*

class JournalViewHolder(
    private var itemView: View,
    private var activity: JournalListingActivity
) :
    RecyclerView.ViewHolder(itemView) {

    private var TAG = javaClass.simpleName
    private var model: JournalListingResponseModel? = null

    fun bindView(journalModel: JournalListingResponseModel, position: Int) {
        this.model = journalModel

        model?.apply {
            if (mediaType == itemView.context.getString(R.string.image)) updateJournalImage()
            else updateJournalCoverImage()

            showHideVideoIcon(status = video.isNotEmpty())
            updateDate()
            updateDescription()
        }

        itemView.setOnClickListener {
            clickedDetail()
        }
    }

    /**
     * Here is updating the journal image
     */
    private fun updateJournalImage() {
        itemView.apply {
            progressBarJournal.visibility = View.VISIBLE

            if (model?.image!!.isNotEmpty())
                Picasso.get()
                    .load(ApiClient.BASE_URL_MEDIA + model?.image)
                    .resize(200, 200)
                    .into(ivJournal, object : Callback {
                        override fun onSuccess() {
                            progressBarJournal.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBarJournal.visibility = View.GONE
                            ivJournal.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivJournal.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the journal image
     */
    private fun updateJournalCoverImage() {
        itemView.apply {
            progressBarJournal.visibility = View.VISIBLE

            if (model?.coverImage!!.isNotEmpty())
                Picasso.get()
                    .load(ApiClient.BASE_URL_MEDIA + model?.coverImage)
                    .resize(200, 200)
                    .into(ivJournal, object : Callback {
                        override fun onSuccess() {
                            progressBarJournal.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBarJournal.visibility = View.GONE
                            ivJournal.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivJournal.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the date in view
     */
    private fun updateDate() {
        itemView.apply {
            tvDate.text = model?.jounalDate
        }
    }

    /**
     * Here is updating the description in view
     */
    private fun updateDescription() {
        itemView.apply {
            tvDescription.text = model?.note
        }
    }

    private fun showHideVideoIcon(status: Boolean) {
        itemView.apply {
            ivPlayVideo.visibility = if (status) View.VISIBLE else View.INVISIBLE
        }
    }

    /**
     * It is called when user click on view
     */
    private fun clickedDetail() {
        if (!model?.video.isNullOrEmpty()) {
            Log.d(TAG, "clickedDetail - ${ApiClient.BASE_URL_MEDIA + model?.video}")

            itemView.apply {
                val intent = Intent(activity, JournalVideoPlayerActivity::class.java)
                intent.putExtra(Constants.App.VIDEO_LINK, ApiClient.BASE_URL_MEDIA + model?.video)

                activity.startActivity(intent)
            }
        }
    }
}