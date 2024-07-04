package com.fivestarmind.android.mvp.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.WebViewType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.activity.WebViewActivity
import com.fivestarmind.android.mvp.model.response.MembershipTypePackagesResponseModel
import kotlinx.android.synthetic.main.dialog_add_product_to_cart_or_upgrade_membership.btnCancel
import kotlinx.android.synthetic.main.dialog_add_product_to_cart_or_upgrade_membership.btnPositive
import kotlinx.android.synthetic.main.dialog_individual_subscription_detail.*


class DialogIndividualSubscriptionDetail(
    private var listener: DialogListener,
    private var membershipTypePackagesResponseModel: MembershipTypePackagesResponseModel,
    var context: BaseActivity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_individual_subscription_detail)

        dialogView()
        clickListener()

        init()
    }

    private fun dialogView() {
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        context,
                        R.color.blackTransparent80
                    )
                )
            )
        }
    }

    /**
     * Here is click events on views
     */
    private fun clickListener() {
        btnCancel.setOnClickListener(clickListener)
        btnPositive.setOnClickListener(clickListener)
        tvTermsConditions.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                R.id.btnCancel -> clickedCancel()

                R.id.btnPositive -> clickedPositive()

                R.id.tvTermsConditions -> clickedTermsConditions()
            }
    }

    /**
     * It is called when user click on cancel button
     */
    private fun clickedCancel() {
        dismiss()

        listener.onDialogEventListener(
            dialogEventType = DialogEventType.CANCEL,
            requestCode = Constants.App.Number.ZERO,
            model = null
        )
    }

    /**
     * It is called when user click on positive button
     */
    private fun clickedPositive() {
        dismiss()

        listener.onDialogEventListener(
            dialogEventType = DialogEventType.POSITIVE,
            requestCode = Constants.App.Number.SIXTEEN,
            model = membershipTypePackagesResponseModel
        )
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        updatePrice()
        updateTime()
        updateDescription()
    }

    /**
     * Here is updating the price text in price view
     */
    private fun updatePrice() {
        membershipTypePackagesResponseModel?.apply {
            tvPrice.text = context.getString(
                R.string.format_price,
                membershipFee.toString()
            )
        }
    }

    /**
     * Here is updating the time in view
     */
    private fun updateTime() {
        membershipTypePackagesResponseModel?.apply {
            tvTime.text = context.getString(
                R.string.format_for,
                duration
            )
        }
    }

    /**
     * Here is updating the description in view
     */
    private fun updateDescription() {
        membershipTypePackagesResponseModel?.apply {
            when (duration) {
                "1" -> tvDescription.text = context.getString(
                    R.string.format_individual_membership_description_for_month,
                    duration
                )

                else -> tvDescription.text = context.getString(
                    R.string.format_individual_membership_description_for_months,
                    duration
                )
            }
        }
    }

    /**
     * This is called when user clicked terms and conditions
     */
    private fun clickedTermsConditions() {
        callToOpenWebViewActivity(webViewType = WebViewType.TERMS_AND_CONDITIONS)
    }

    /**
     * Here is call to open web url
     */
    private fun callToOpenWebViewActivity(webViewType: WebViewType) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(Constants.App.WEBVIEW_TYPE, webViewType)
        context.startActivity(intent)
    }
}