package com.fivestarmind.android.mvp.dialog

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.core.content.FileProvider
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.activity.BaseActivity
import kotlinx.android.synthetic.main.dialog_choose_image.*
import java.io.File


class DialogChooseImage(
    var context: BaseActivity,
    var file: File,
    var showOptionForVideo: Boolean
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_choose_image)

        dialogView()
        updateUI()

        clickListener()
    }

    private fun dialogView() {
        if (window != null) {
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setGravity(Gravity.BOTTOM)
        }

     /*   rootLayout.layoutParams.height =
            (context.resources.displayMetrics.heightPixels.toFloat() / 1.9).toInt()*/
    }

    /**
     * Updating UI w.r.t view
     */
    private fun updateUI() {
        if (showOptionForVideo) {
            view3.visibility = View.VISIBLE
            tvTakeVideo.visibility = View.VISIBLE
            view4.visibility = View.VISIBLE
            tvChooseVideo.visibility = View.VISIBLE
        } else {
            view3.visibility = View.GONE
            tvTakeVideo.visibility = View.GONE
            view4.visibility = View.GONE
            tvChooseVideo.visibility = View.GONE
        }
    }

    /**
     * Here is click events on views
     */
    private fun clickListener() {
        tvTakePhoto.setOnClickListener(clickListener)
        tvPhotoLibrary.setOnClickListener(clickListener)
        tvTakeVideo.setOnClickListener(clickListener)
        tvChooseVideo.setOnClickListener(clickListener)
        tvCancelPopup.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
                R.id.tvTakePhoto -> {
                    dismiss()
                    captureImage()
                }

                R.id.tvPhotoLibrary -> {
                    dismiss()
                    pickImage()
                }

                R.id.tvTakeVideo -> clickedTakeVideo()

                R.id.tvChooseVideo -> clickedChooseVideo()

                R.id.tvCancelPopup -> dismiss()
            }
    }

    /**
     * Here is call for Intent to open Camera
     */
    private fun captureImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val apkURI = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivityForResult(cameraIntent, Constants.App.RequestCode.CAMERA)
    }

    /**
     * Here is call for Intent to open external storage images
     */
    private fun pickImage() {
        val uri = Uri.parse(Environment.getDownloadCacheDirectory().path.toString())
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, Uri.fromFile(file))
        galleryIntent.setDataAndType(uri, "image/*")
        context.startActivityForResult(galleryIntent, Constants.App.RequestCode.GALLERY)
    }

    /**
     * It is called when user clicked on take video view
     */
    private fun clickedTakeVideo() {
        dismiss()

        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // First check if camera is available in the device
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            context.startActivityForResult(intent, Constants.App.RequestCode.SELECT_VIDEO)
        }
    }

    /**
     * It is called when user clicked on choose video view
     */
    private fun clickedChooseVideo() {
        dismiss()

        val uri = Uri.parse(Environment.getDownloadCacheDirectory().path.toString())
        val galleryVideoIntent = Intent(Intent.ACTION_GET_CONTENT, Uri.fromFile(file))
        galleryVideoIntent.setDataAndType(uri, "video/*")
        context.startActivityForResult(galleryVideoIntent, Constants.App.RequestCode.SELECT_VIDEO)
    }
}