package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.model.response.TodayQuoteItem
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_today_quote.view.ivQuoteImage
import kotlinx.android.synthetic.main.item_today_quote.view.ivTextQuote
import kotlinx.android.synthetic.main.item_today_quote.view.pbQuoteImage
import kotlinx.android.synthetic.main.item_today_quote.view.tvQuote
import kotlinx.android.synthetic.main.item_today_quote.view.tvQuoteName


class QuoteHolder(
    itemView: View,
    private val listener: RecyclerViewItemListener
) : RecyclerView.ViewHolder(itemView) {

    private var TAG = QuoteHolder::class.java.simpleName
    var quoteTextData: String = ""
    fun bindView(
        todayQuoteList: TodayQuoteItem,
        activity: Context,

        ) {
        itemView.apply {


            if (todayQuoteList.type != null && todayQuoteList.type.equals("image")) {
                tvQuoteName.visibility = View.INVISIBLE
                tvQuote.visibility = View.INVISIBLE

                if (todayQuoteList.image != null && todayQuoteList.image!!.isNotEmpty()) {
                    setQuoteImage(activity, todayQuoteList.image!!)
                }

            } else {
                if (todayQuoteList.quote != null && todayQuoteList.quote!!.length > 0) {
                    tvQuote.visibility = View.VISIBLE
                    ivQuoteImage.visibility = View.GONE
                    itemView.pbQuoteImage.visibility = View.GONE

                    /*  htmlToStringFilter(todayQuoteList.quote).let {
                           SpannableStringBuilder_(
                               it,
                               activity
                           )
                       }*/

                    //Here set quote alignment in view
                    if (todayQuoteList.quote!!.contains("center", true)) {
                        itemView.tvQuote.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    } else if (todayQuoteList.quote!!.contains("left", true)) {
                        itemView.tvQuote.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    } else if (todayQuoteList.quote!!.contains("right", true)) {
                        itemView.tvQuote.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                    } else if (todayQuoteList.quote!!.contains("<u>", true)) {
                        itemView.tvQuote.paint?.isUnderlineText = true
                    }
                    //Here set quote view
                    itemView.tvQuote.text = htmlToStringFilter(todayQuoteList.quote)
                    //Here call Quote Background Image funtion
                    setQuoteBackgroundImage(todayQuoteList)
                }

                if (todayQuoteList.quoteBy != null) {
                    tvQuoteName.visibility = View.VISIBLE
                    tvQuoteName.text = todayQuoteList.quoteBy
                }

            }
        }
    }

    /**
     * Here call set Quote Background Image funtion
     *   */
    private fun setQuoteBackgroundImage(todayQuoteList: TodayQuoteItem) {

        var drawableIcon = 0

        //todayQuoteList.color = "orange"

        if (todayQuoteList.color != null && todayQuoteList.color!!.isNotEmpty()) {
            if (todayQuoteList.color!!.equals("red", true)) {
                drawableIcon = R.drawable.ic_red_quote

            } else if (todayQuoteList.color!!.equals("blue", true)) {
                drawableIcon = R.drawable.ic_blue_quote

            } else if (todayQuoteList.color!!.equals("orange", true)) {
                drawableIcon = R.drawable.ic_orange_qoute

            } else if (todayQuoteList.color!!.equals("green", true)) {
                drawableIcon = R.drawable.ic_green_quote

            } else if (todayQuoteList.color!!.equals("purple", true)) {
                drawableIcon = R.drawable.ic_purple_quote

            } else if (todayQuoteList.color!!.equals("yellow", true)) {
                drawableIcon = R.drawable.ic_yellow_quote

            }
            itemView.ivTextQuote.visibility = View.VISIBLE

            itemView.ivTextQuote.setImageResource(drawableIcon)

        } else {
            itemView.ivTextQuote.visibility = View.GONE
        }


        Log.d(TAG, "QuoteLength>> " + htmlToStringFilter(todayQuoteList.quote).length)
        if (htmlToStringFilter(todayQuoteList.quote).length > 150) {

            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                itemView.tvQuote,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
        } else {

            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                itemView.tvQuote,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE
            )

        }

    }

    fun setQuoteImage(activity: Context, image: String) {
        itemView.ivQuoteImage.visibility = View.VISIBLE
        itemView.pbQuoteImage.visibility = View.VISIBLE
        itemView.ivTextQuote.visibility = View.GONE

           Picasso.get()
               .load(ApiClient.BASE_URL_MEDIA + image)
               .placeholder(R.drawable.ic_image_placeholder)
               //   .into(ivOrganisationLogo, object : Callback {
               .into(itemView.ivQuoteImage, object : Callback {
                   override fun onSuccess() {
                       itemView.pbQuoteImage.visibility = View.GONE
                   }

                   override fun onError(e: Exception?) {
                       itemView.pbQuoteImage.visibility = View.GONE
                       itemView.ivQuoteImage.setImageDrawable(
                           activity.resources.getDrawable(
                               R.drawable.ic_image_placeholder
                           )
                       )
                   }
               })

        /*Glide.with(activity)
            .load(ApiClient.BASE_URL_MEDIA + image)
            .placeholder(R.drawable.ic_image_placeholder).listener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemView.pbQuoteImage.visibility = View.GONE
                    itemView.ivQuoteImage.setImageDrawable(
                        activity.resources.getDrawable(
                            R.drawable.ic_image_placeholder
                        )
                    )
                    return false

                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    itemView.pbQuoteImage.visibility = View.GONE
                    return false
                }

            }).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
            .into(itemView.ivQuoteImage)*/
    }

    /**
     * here remove HTML tags from a String
     */
    fun htmlToStringFilter(textToFilter: String?): String {
        var text: String? = null

        if (textToFilter != null) {
            text = Html.fromHtml(textToFilter).toString()
        } else {
            text = ""
        }
        Log.d(TAG, ">>>" + text)
        // text = text.replace("<br/>".toRegex(), " ")
        // text = text.replace("\\s".toRegex(), " ")

        return text
    }

    private fun noTrailingwhiteLines(text: CharSequence): CharSequence {
        var text = text
        while (text[text.length - 1] == '\n') {
            text = text.subSequence(0, text.length - 1)
        }
        return text
    }

    fun SpannableStringBuilder_(yourText: String, context: Context) {
        Log.d(TAG, "Text_Length: " + yourText.length)
        /*  if (yourText.length > 50) {
              quoteTextData = "  " + yourText + "  "

          } else {
              quoteTextData = "  " + yourText + "  "

          }*/


        quoteTextData = "  " + yourText
        Log.d(TAG, "SpannableStringBuilder_: " + quoteTextData)

        val textQuoteSpan = SpannableStringBuilder(quoteTextData)

        // You will need to play with the size to figure out what works
        val imageHight = context.resources.getDimension(R.dimen.quotation_hight).toInt()
        val imageWidth = context.resources.getDimension(R.dimen.quotation_width).toInt()

        // Use AppCompatResources to get drawable for Android.M if required
        val image = context.resources.getDrawable(R.drawable.ic_start_quote, null)
        image.setBounds(0, 0, imageWidth, imageHight)

        val imageSpan = ImageSpan(image, ImageSpan.ALIGN_CENTER)

// This part is where you would have to do a little calculation to figure out the exact position you want
// to place the image at. I have given `positionToPlaceImageAt` just as a placeholder
        textQuoteSpan.setSpan(
            imageSpan,
            0,
            1,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )


        val imageEnd = context.resources.getDrawable(R.drawable.ic_end_quote, null)
        imageEnd.setBounds(0, 0, imageWidth, imageHight)

        val imageSpanEnd = ImageSpan(imageEnd, ImageSpan.ALIGN_BASELINE)

// This part is where you would have to do a little calculation to figure out the exact position you want
// to place the image at. I have given `positionToPlaceImageAt` just as a placeholder
        textQuoteSpan.setSpan(
            imageSpanEnd,
            textQuoteSpan.length - 1,
            textQuoteSpan.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        Log.d(TAG, "SpannableStringBuilder>>>>:123 " + textQuoteSpan)

        itemView.tvQuote.text = textQuoteSpan

    }


    private fun appendImage(yourText: String, context: Context) {
        //   itemView.tvQuote.setTransformationMethod(null)
        val ssb = SpannableStringBuilder()
        //ss.setSpan(ImageSpan(bmp, ImageSpan.ALIGN_BASELINE), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        // ssb.append(" ")
        val imageHight = context.resources.getDimension(R.dimen.quotation_hight).toInt()
        val imageWidth = context.resources.getDimension(R.dimen.quotation_width).toInt()

        ssb.append("  " + yourText + "  ")

        val image = context.resources.getDrawable(R.drawable.ic_start_quote, null)
        image.setBounds(0, 0, imageWidth, imageHight)

        val imageSpan = ImageSpan(image, ImageSpan.ALIGN_BASELINE)
        //  ssb.setSpan(ImageSpan(context,R.drawable.ic_start_quote),0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        ssb.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        //  ssb.append("  "+yourText+ "  ")

        val imageEnd = context.resources.getDrawable(R.drawable.ic_end_quote, null)
        imageEnd.setBounds(0, 0, imageWidth, imageHight)

        val imageSpanEnd = ImageSpan(imageEnd, ImageSpan.ALIGN_BASELINE)
        //  ssb.setSpan(ImageSpan(context,R.drawable.ic_end_quote),0,1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        ssb.setSpan(imageSpanEnd, ssb.length - 1, ssb.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        itemView.tvQuote.append(ssb)
    }

}

