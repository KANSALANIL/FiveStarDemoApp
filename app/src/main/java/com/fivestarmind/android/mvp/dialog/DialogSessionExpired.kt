package com.fivestarmind.android.mvp.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.sesstion_expired.btnPositive
import kotlinx.android.synthetic.main.sesstion_expired.tvMessage


class DialogSessionExpired(
    private var listener: DialogSesstionExpiredListener,
    private var message: String,
    var context: BaseActivity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        Log.d("Dialog>>","DialogSessionExpired")
        setContentView(R.layout.sesstion_expired)
        tvMessage.setText(message)
        dialogView()
        clickListener()
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
                        R.color.transparent
                    )
                )
            )
        }
    }

    /**
     * Here is click events on views
     */
    private fun clickListener() {
        btnPositive.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                //  R.id.btnCancel -> clickedCancel()

                R.id.btnPositive -> clickedPositive()
            }
    }

    /**
     * It is called when user click on cancel button
     */
    private fun clickedCancel() {
        dismiss()

        listener.onDialogSesstionExpiredListener(
            dialogEventType = DialogEventType.CANCEL,
        )
    }

    /**
     * It is called when user click on mastered button
     */
    private fun clickedPositive() {


      //  dismiss()

        listener.onDialogSesstionExpiredListener(
            dialogEventType = DialogEventType.POSITIVE
        )
    }
}