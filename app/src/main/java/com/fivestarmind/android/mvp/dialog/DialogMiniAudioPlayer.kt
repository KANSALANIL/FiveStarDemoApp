package com.fivestarmind.android.mvp.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogBookMarkListener
import com.fivestarmind.android.mvp.activity.AudioPlayActivity
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_mastered.*
import kotlinx.android.synthetic.main.layout_home_content.homeAudioPlayerView
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName


class DialogMiniAudioPlayer(
    var context: BaseActivity
) : BottomSheetDialog(context,R.style.Theme_Design_BottomSheetDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_mini_player)

        dialogView()
       // clickListener()
        initializePlayer()
    }

    private fun dialogView() {
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

        }
        setCancelable(false)
    }

    /**
     * Here is click events on views
     */
    private fun clickListener() {
       // btnCancel.setOnClickListener(clickListener)
      //  btnMastered.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (context.shouldProceedClick())
            when (view.id) {
              //  R.id.btnCancel -> clickedCancel()

              //  R.id.btnPositive -> clickedPositive()
            }
    }

    /**
     * It is called when user click on cancel button
     */
    private fun clickedCancel() {
        dismiss()

       /* listener.onDialogEventListener(
            dialogEventType = DialogEventType.CANCEL,
            requestCode = Constants.App.Number.ZERO,
            position= position,
            model = model
        )*/
    }

    /**
     * It is called when user click on mastered button
     */
    private fun clickedPositive() {
        dismiss()

       /* listener.onDialogEventListener(
            dialogEventType = DialogEventType.MASTERED,
            requestCode = Constants.App.Number.ZERO,
            position= position,
            model = model
        )*/
    }

    private fun initializePlayer() {

        if (AppController.simpleExoplayer!!.isPlaying) {
            homeAudioPlayerView.visibility = View.VISIBLE
            tvAudioTitleName.setText(AppController.audioTitle)
            //   exo_position.visibility =View.VISIBLE
            //     exo_duration.visibility =View.VISIBLE
            //  exo_progress.visibility =View.VISIBLE

            if (null != context && !context.isFinishing) {
                context.runOnUiThread {

                    val uri: String =
                        AppController.simpleExoplayer!!.currentMediaItem?.playbackProperties?.uri.toString()
                    val mediaItem = MediaItem.fromUri(Uri.parse(uri))
                    AppController.simpleExoplayer!!.addMediaItem(mediaItem)

                    homeAudioPlayerView.player = AppController.simpleExoplayer
                    homeAudioPlayerView.showController()

                    val duration: Long = AppController.simpleExoplayer?.currentPosition?.toLong()!!
                    val videoDuration =
                        AppHelper.convertMillisecondsToSeconds(milliseconds = duration)
                    //this.audioDuration = videoDuration.toString()
                    AppController.simpleExoplayer!!.prepare()
                    AppController.simpleExoplayer!!.play()

                }
            }
        } else {
            homeAudioPlayerView.visibility = View.GONE

        }


        if ( AppController.audioImge != null) {
            progressBarExo.visibility = View.VISIBLE

            Picasso.get().load(ApiClient.BASE_URL_MEDIA + AppController.audioImge)
                .into(ivExoIcon, object : Callback {
                    override fun onSuccess() {
                        progressBarExo.visibility = View.GONE
                    }

                    override fun onError(e: Exception) {
                        progressBarExo.visibility = View.GONE
                    }
                })
        } else {
            ivExoIcon.setImageDrawable(
                context.resources.getDrawable(R.drawable.ic_audio)
            )
        }

        homeAudioPlayerView.setOnClickListener{
            context.startActivity(
                Intent(context, AudioPlayActivity::class.java).putExtra(
                    Constants.App.AUDIO_LINK, AppController.audioUriLink
                ).putExtra(
                    Constants.App.POSITION, AppController.position
                ).putExtra(
                    Constants.App.AUDIO_ID, AppController.audioId
                ).putExtra(
                    Constants.App.AUDIO_TITLE, AppController.audioTitle
                ).putExtra(
                    Constants.App.AUDIO_DURATION, AppController.duration
                ).putExtra(
                    Constants.App.AUDIO_ACTIVITY_NAME, AppController.activityHeaderName
                ).putExtra(
                    Constants.App.IS_FAVROITE, AppController.isFavourite
                ).putExtra(
                    Constants.App.AUDIO_THUMPATH, AppController.audioImge
                )
            )


        }
    }
}