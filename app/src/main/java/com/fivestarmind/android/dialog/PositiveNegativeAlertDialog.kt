package com.fivestarmind.android.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.dialog.base.BaseDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.dialog_positive_negative_alert.*

/**
 * This is the dialog class used for showing alert with only positive button.
 *
 * @param baseActivity instance of BaseActivity
 * @param requestCode
 * @param message
 * @param negativeButtonText
 * @param positiveButtonText
 * @param listener
 */
class PositiveNegativeAlertDialog(
    private val baseActivity: BaseActivity,
    var requestCode: Int,
    var message: String?,
    var negativeButtonText: String,
    var positiveButtonText: String,
    var listener: DialogListener?
) : BaseDialog(
    baseActivity = baseActivity,
    styleResId = R.style.DialogCenterStyle
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_positive_negative_alert)

        initDialog()

        init()
        setListeners()
    }

    /**
     * Configuring dialog's properties
     */
    private fun initDialog() {
        setCancelable(false)

        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.blackTransparent80
                    )
                )
            )
        }
    }

    /**
     * Setting listeners to views
     */
    private fun setListeners() {
        btnNegative.setOnClickListener(clickListener)
        btnPositive.setOnClickListener(clickListener)
    }

    /**
     * Initializing the objects and data to initial state
     */
    private fun init() {
        updateDataInUI()
    }

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (baseActivity.shouldProceedClick())
            when (view.id) {
                R.id.btnNegative -> clickedNegativeButton()

                R.id.btnPositive -> clickedPositiveButton()
            }
    }

    /**
     *  This is called when user tap on Negative button
     */
    private fun clickedNegativeButton() {
        dismiss()

        listener?.onDialogEventListener(
            dialogEventType = DialogEventType.NEGATIVE,
            requestCode = requestCode,
            model = null
        )
    }

    /**
     *  This is called when user tap on Positive button
     */
    private fun clickedPositiveButton() {
        dismiss()

        listener?.onDialogEventListener(
            dialogEventType = DialogEventType.POSITIVE,
            requestCode = requestCode,
            model = null
        )
    }

    /**
     * Here updating views w.r.t. data
     */
    private fun updateDataInUI() {
        updateButtonText()
        updateMessageText()
    }

    /**
     *  Here updating the button text view with positive response
     */
    private fun updateButtonText() {
        btnNegative.text = negativeButtonText
        btnPositive.text = positiveButtonText
    }

    /**
     *  Here updating the message text view with data
     */
    private fun updateMessageText() {
        tvMessage.text = message
    }
}
