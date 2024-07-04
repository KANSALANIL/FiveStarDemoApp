package com.fivestarmind.android.mvp.holder

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.model.response.UsersDataItemListing
import com.fivestarmind.android.retrofit.ApiClient
import kotlinx.android.synthetic.main.item_coaching_staff.view.ivUserProfile
import kotlinx.android.synthetic.main.item_coaching_staff.view.pbUserImage
import kotlinx.android.synthetic.main.item_coaching_staff.view.tvSpecialty
import kotlinx.android.synthetic.main.item_coaching_staff.view.tvUserName

class CoachingStaffViewHolder(
    itemView: View,
    var activity: BaseActivity,
    private var listener: RecyclerViewItemListener,
) : RecyclerView.ViewHolder(itemView) {

    private var usersDataItem: UsersDataItemListing? = null


    fun bindView(model: UsersDataItemListing) {

        usersDataItem = model
        itemView.apply {

            if (usersDataItem!!.name != null) {
                tvUserName.text = usersDataItem!!.name
            }
            if (usersDataItem!!.orgUser!!.get(0)!!.userSubrole != null) {
                if (usersDataItem!!.orgUser!!.get(0)!!.userSubrole!!.name != null) {
                    tvSpecialty.text = "Specialty: " + usersDataItem!!.orgUser!!.get(0)!!.userSubrole!!.name
                }
            }
            pbUserImage.visibility = View.VISIBLE

            Glide.with(context)
                .load(ApiClient.BASE_URL_PROFILE + usersDataItem!!.profileImg)
                .placeholder(R.drawable.ic_user_placeholder).listener(object :
                    RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbUserImage.visibility = View.GONE
                        ivUserProfile.setImageDrawable(context.resources.getDrawable(R.drawable.ic_user_placeholder))
                        return false

                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbUserImage.visibility = View.GONE
                        return false
                    }

                }).diskCacheStrategy(
                    DiskCacheStrategy.ALL
                )
                .into(ivUserProfile)


        }

        itemView.setOnClickListener {

            clickedFavorite()

        }


    }

    /**
     * It is called when user clicked heart view
     */
    private fun clickedFavorite() {
        listener.onRecyclerViewItemClick(
            itemClickType = ItemClickType.DETAIL,
            model = usersDataItem!!,
            position = layoutPosition,
            view = itemView
        )
    }


}