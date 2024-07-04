package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import kotlinx.android.synthetic.main.activity_video_player.*

class JournalVideoPlayerActivity : BaseActivity() {

    override var TAG = JournalVideoPlayerActivity::class.java.simpleName

    private var simpleExoplayer: SimpleExoPlayer? = null

    private var videoLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        getDataFromIntent()
        initializePlayer()

        setListeners()
    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.VIDEO_LINK) && null != intent.getStringExtra(Constants.App.VIDEO_LINK)) {
            videoLink = intent.getStringExtra(Constants.App.VIDEO_LINK)!!
        }
    }

    /**
     * Here is initializing video player
     */
    private fun initializePlayer() {
        if (null != baseContext && !isFinishing) {
            runOnUiThread {
                simpleExoplayer = SimpleExoPlayer.Builder(this).build()
                val mediaItem: MediaItem = MediaItem.fromUri(videoLink!!)
                simpleExoplayer!!.setMediaItem(mediaItem)
                playerView.player = simpleExoplayer
                simpleExoplayer!!.play()
                simpleExoplayer!!.prepare()
            }
        }
    }

    /**
     * Here is setting listeners for callback
     */
    private fun setListeners() {
        simpleExoplayer?.addListener(listener)
    }

    private var listener: Player.Listener = object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            if (playWhenReady && playbackState == Player.STATE_READY) {
                Log.d(TAG, "video is playing")
            }
//                else if (playWhenReady) {
//                    // might be idle (plays after prepare()),
//                    // buffering (plays when data available)
//                    Log.d(TAG, "video is loading ")
//                    // or ended (plays when seek away from end)
//                }
            else if (playbackState == Player.STATE_ENDED) {
                Log.d(TAG, "video is ended ")
//                finish()
            } else {
                Log.d(TAG, "video paused ")
            }
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, " onBackPressed")
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        simpleExoplayer?.removeListener(listener)
        simpleExoplayer?.release()
    }
}
