package com.fivestarmind.android.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import kotlinx.android.synthetic.main.layout_progress_dialog.*

class ProgressDialog(private val mContext: Context, private val message: String) :
    Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_progress_dialog)

        initDialog()
        initTextView()
    }

    private fun initDialog() {

        setCancelable(false)
//        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setDimAmount(0.0f)
        progressBar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(
                mContext,
                R.color.blue
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }

    private fun initTextView() {

        if (message.isNotEmpty())
            tvProgressText.text = message
    }
}