package com.fivestarmind.android.mvp.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.dialog_add_product_to_cart_or_upgrade_membership.*


class DialogAddProductToCartOrUpgradeMembership(
    private var listener: DialogListener,
    private var addToCart: String?,
    var context: BaseActivity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_product_to_cart_or_upgrade_membership)

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
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                R.id.btnCancel -> clickedCancel()

                R.id.btnPositive -> clickedPositive()
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

        val dialogType = when (addToCart) {
            context.getString(R.string.add_to_cart) -> DialogEventType.ADD_TO_CART
            context.getString(R.string.view_cart) -> DialogEventType.VIEW_CART
            else -> DialogEventType.UPGRADE_MEMBERSHIP
        }

        listener.onDialogEventListener(
            dialogEventType = dialogType,
            requestCode = Constants.App.Number.ZERO,
            model = null
        )
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        updateDescription()
        updatePositiveButtonText()
    }

    /**
     * Here is updating the description text in description view
     */
    private fun updateDescription() {
        tvDescription.text =
            when (addToCart) {
                context.getString(R.string.add_to_cart), context.getString(R.string.view_cart) -> context.getString(
                    R.string.please_purchase_product_to_watch_video
                )
                else -> context.getString(R.string.please_upgrade_membership_to_watch_this_video)
            }
    }

    /**
     * Here is updating the positive button text in positive button view
     */
    private fun updatePositiveButtonText() {
        btnPositive.text =  when (addToCart) {
            context.getString(R.string.add_to_cart) -> context.getString(R.string.add_to_cart)
            context.getString(R.string.view_cart) -> context.getString(R.string.view_cart)
            else -> context.getString(R.string.upgrade)
        }
    }
}