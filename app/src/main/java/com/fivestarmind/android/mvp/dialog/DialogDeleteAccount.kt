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
import kotlinx.android.synthetic.main.dialog_delete_account.*

class DialogDeleteAccount(
    private var listener: DialogListener,
    var context: BaseActivity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_delete_account)

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
                        R.color.blackTransparent_80
                    )
                )
            )
        }
    }

    /**
     * Here is click events on views
     */
    private fun clickListener() {
        btnGoBack.setOnClickListener(clickListener)
        btnPositive.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                R.id.btnGoBack -> clickedGoBack()

                R.id.btnPositive -> clickedPositive()
            }
    }

    /**
     * It is called when user click on goBack button
     */
    private fun clickedGoBack() {
        dismiss()

    }

    /**
     * It is called when user click on positive button
     */
    private fun clickedPositive() {
        dismiss()

        listener.onDialogEventListener(
            dialogEventType = DialogEventType.POSITIVE,
            requestCode = Constants.App.RequestCode.DELETE_ACCOUNT,
            model = null
        )
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {

    }

}