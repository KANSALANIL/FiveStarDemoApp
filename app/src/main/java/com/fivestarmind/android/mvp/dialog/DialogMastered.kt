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
import com.fivestarmind.android.mvp.model.request.RequestMasterVideo
import kotlinx.android.synthetic.main.dialog_mastered.*


class DialogMastered(
    private var listener: DialogListener,
    private var requestMasterVideo: RequestMasterVideo,
    var context: BaseActivity
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_mastered)

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
        btnMastered.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                R.id.btnCancel -> clickedCancel()

                R.id.btnMastered -> clickedMastered()
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
            model = requestMasterVideo
        )
    }

    /**
     * It is called when user click on mastered button
     */
    private fun clickedMastered() {
        dismiss()

        listener.onDialogEventListener(
            dialogEventType = DialogEventType.MASTERED,
            requestCode = Constants.App.Number.ZERO,
            model = requestMasterVideo
        )
    }
}