package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.activity.AudioPlayActivity
import com.fivestarmind.android.mvp.activity.MenuDetailActivity
import com.fivestarmind.android.mvp.activity.OpenPdfFileActivity
import com.fivestarmind.android.mvp.activity.VideoPlayerActivity
import com.fivestarmind.android.mvp.model.response.FeaturedProductDetailResModel
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class FeaturedProductAdapter(
    private val context: Context,
    private val featuredProductListModel: ArrayList<FeaturedProductDetailResModel>
) : PagerAdapter() {

    private val TAG = FeaturedProductAdapter::class.java.simpleName

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout =
            inflater.inflate(R.layout.item_featured_products, container, false) as ViewGroup
        val ivFeaturedProduct = layout.findViewById<ImageView>(R.id.ivFeaturedProduct)
        val pbViewPager = layout.findViewById<ProgressBar>(R.id.pbViewPager)

        var drawableIcon = 0
        if (featuredProductListModel[position].type!!.contains("pdf")) {
            drawableIcon = R.drawable.ic_slider_pdf
        } else if (featuredProductListModel[position].type!!.contains("video")) {
            drawableIcon = R.drawable.ic_slider_video
        } else if (featuredProductListModel[position].type!!.contains("audio")) {
            drawableIcon = R.drawable.ic_slider_audio
        }else if (featuredProductListModel[position].type!!.contains("image")) {
            drawableIcon = R.drawable.ic_image_placeholder
        }

        if (featuredProductListModel[position].type!!.contains("image")) {
            Picasso.get()
                .load(ApiClient.BASE_URL_MEDIA + featuredProductListModel[position].image)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivFeaturedProduct, object : Callback {
                    override fun onSuccess() {
                        pbViewPager.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        pbViewPager.visibility = View.GONE
                        ivFeaturedProduct.setImageDrawable(context.resources.getDrawable(drawableIcon))

                    }
                })
        } else if (featuredProductListModel[position].type!!.contains("video")
            || featuredProductListModel[position].type!!.contains("pdf")
            || featuredProductListModel[position].type!!.contains("audio")
        ) {
            if (featuredProductListModel[position].thumbpath != null) {

                Picasso.get()
                    .load(ApiClient.BASE_URL_MEDIA + featuredProductListModel[position].thumbpath)
                    .into(ivFeaturedProduct, object : Callback {
                        override fun onSuccess() {
                            pbViewPager.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            pbViewPager.visibility = View.GONE
                            ivFeaturedProduct.setImageDrawable(context.resources.getDrawable(drawableIcon))

                        }
                    })

            } else {
                pbViewPager.visibility = View.GONE

                ivFeaturedProduct.setImageDrawable(context.resources.getDrawable(drawableIcon))

            /*     if (featuredProductListModel[position].tempPath != null) {
                         BackgroundImageTask(
                             ivFeaturedProduct, pbViewPager,
                             ApiClient.BASE_URL_MEDIA + featuredProductListModel[position].tempPath
                         ).execute()
                     } else {

                         if (featuredProductListModel[position].type!!.contains("pdf")) {
                             ivFeaturedProduct.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pdf))
                         }

                     }*/

                /* val bm = AppHelper.retrieveVideoFrameFromVideo(featuredProductListModel[position].tempPath)
                 ivFeaturedProduct.setImageBitmap(bm)*/

            }

        }
        layout.setOnClickListener {
            /* val intent = Intent(context, ProductDetailActivity::class.java)
             intent.putExtra(Constants.App.PRODUCT_ID, featuredProductListModel[position].productId)
             intent.putExtra(Constants.App.IMAGE, featuredProductListModel[position].image)
             context.startActivity(intent)*/

            /*   if (featuredProductListModel[position].type!!.contains("image")) {
                   val intent = Intent(context, OpenPdfFileActivity::class.java)
                   intent.putExtra("type", featuredProductListModel[position].type)
                   context.startActivity(intent)
               } else */
            if (SharedPreferencesHelper.getAuthToken().equals(""))
                return@setOnClickListener

            if (featuredProductListModel[position].type!!.contains("video")) {

                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(
                    Constants.App.VIDEO_LINK,
                    featuredProductListModel[position].tempPath
                )
                intent.putExtra(
                    Constants.App.VIDEO_THUMB_PATH_LINK,
                    featuredProductListModel[position].thumbpath
                )
                intent.putExtra(Constants.App.POSITION, position)

                intent.putExtra(Constants.App.VIDEO_ACTIVITY_NAME, featuredProductListModel[position].product!!.name)

                intent.putExtra(
                    Constants.App.VIDEO_ID, featuredProductListModel[position].productId
                ).putExtra(
                    Constants.App.SPECIAL_CONTENT, "specialContent"
                ).putExtra(
                    Constants.App.VIDEO_ACTIVITY_NAME, featuredProductListModel[position].productName
                )
                context.startActivity(intent)

            } else if (featuredProductListModel[position].type!!.contains("audio")) {

                if (AppController.audioList!=null) {
                    AppController.audioList!!.clear()
                }

                AppController.fromHome = "Home"

             //   SharedPreferencesHelper.saveAudioId(featuredProductListModel[position].productId.toString())


                Log.d(TAG,"featuredProductList__"+featuredProductListModel[position].is_favourite)

                val intent = Intent(context, AudioPlayActivity::class.java)
                intent.putExtra(
                    Constants.App.AUDIO_LINK,
                    featuredProductListModel[position].tempPath
                )
                intent.putExtra(Constants.App.POSITION, position)
                intent.putExtra(
                    Constants.App.AUDIO_TITLE,
                    featuredProductListModel[position].productName
                )
                intent.putExtra(
                    Constants.App.AUDIO_ID,
                    featuredProductListModel[position].productId
                )
                intent.putExtra(
                    Constants.App.AUDIO_DURATION,
                    featuredProductListModel[position].duration
                )
                intent.putExtra(
                    Constants.App.AUDIO_ACTIVITY_NAME,
                    featuredProductListModel[position].product!!.name
                )

                intent.putExtra(
                    Constants.App.AUDIO_THUMPATH,
                    featuredProductListModel[position].thumbpath
                )

                intent.putExtra(
                    Constants.App.IS_FAVROITE,
                    featuredProductListModel[position].is_favourite
                )

                intent .putExtra(
                        Constants.App.SPECIAL_CONTENT, "specialContent"
                    )
                intent.putExtra(Constants.App.SCREEN_TYPE,"Home")
                context.startActivity(intent)

            } else if (featuredProductListModel[position].type!!.contains("pdf")) {
                val intent = Intent(context, OpenPdfFileActivity::class.java)
                intent.putExtra(Constants.App.PDF_LINK, featuredProductListModel[position].pdf)
                    .putExtra(
                        Constants.App.PDF_NAME,
                        featuredProductListModel[position].productName
                    )
                context.startActivity(intent)


            }else if (featuredProductListModel[position].type!!.contains("image")) {
                val intent = Intent(context, MenuDetailActivity::class.java)
                intent.putExtra("type", "Mind Venom")
                    .putExtra("id", featuredProductListModel.get(position).productId)
                context.startActivity(intent)


            }



        }

        container.addView(layout)
        return layout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return featuredProductListModel.size
        //  return 5
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
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
            pb.visibility = View.VISIBLE
            iv.setImageBitmap(bit)
        }
    }


}