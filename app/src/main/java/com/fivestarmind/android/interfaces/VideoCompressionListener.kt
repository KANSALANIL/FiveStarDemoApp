package com.fivestarmind.android.interfaces

import java.io.File

/**
 * Interface definition for a callback to be invoked when a video compressor gives response.
 */
interface VideoCompressionListener {

    /**
     * Called when a video compressor gives success response
     *
     * @param compressedVideoFile
     */
    fun onVideoCompressionSuccess(compressedVideoFile: File)

    /**
     * Called when a video compressor gives failure response
     */
    fun onVideoCompressionFailure()

}