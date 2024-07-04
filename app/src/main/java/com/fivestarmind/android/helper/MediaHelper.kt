package com.fivestarmind.android.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.iceteck.silicompressorr.SiliCompressor
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.VideoCompressionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * This is the helper class which contains all the methods or functions regarding Images.
 */
object MediaHelper {
    private var TAG = javaClass.simpleName

    private var videoCompressAsyncTask: VideoCompressAsyncTask? = null

    /**
     *  Here creating an image directory
     *
     *  @param context
     *  @return
     */
    private fun getImageTempDirectory(context: Context): File? {
        val storageDir =
            File(
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/${
                    context.getString(
                        R.string.five_star_mind
                    )
                }"
            )

        try {
            // Make sure the directory exists.
            storageDir.mkdirs()
        } catch (e: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "getImageTempDirectory: mkdirs error- ${e.message}")
        }

        return if (storageDir.isDirectory)
            storageDir
        else
            null
    }

    /**
     *  Here creating an image file
     *
     *  @param context
     *  @return
     */
    private fun getImageTempFile(context: Context): File? {
        var file: File? = null
        val storageDir: File? = getImageTempDirectory(context)
        val imageFileName =
            "${context.getString(R.string.five_star_mind)}_"

        if (storageDir == null)
            return null

        try {
            file =
                File.createTempFile(
                    imageFileName, // prefix
                    Constants.Api.MEDIA_TYPE.EXTENSION_JPG, // suffix
                    storageDir // directory
                )
        } catch (e: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "getTempFile: createTempFile error- ${e.message}")
        }

        return file
    }

    /**
     *  Here creating an video directory
     *
     *  @param context
     *  @return
     */
    internal fun getVideoTempDirectory(context: Context): File? {
        val storageDir =
            File(
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)}/${
                    context.getString(
                        R.string.five_star_mind
                    )
                }"
            )

        try {
            // Make sure the directory exists.
            storageDir.mkdirs()
        } catch (e: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "getVideoTempDirectory: mkdirs error- ${e.message}")
        }

        return if (storageDir.isDirectory)
            storageDir
        else
            null
    }

    /**
     *  Here compressing quality and resizing the image
     *
     *  @param activity
     *  @param bitmap
     *  @param maxSize
     *  @param compressionQuality
     */
    internal fun resizeImage(
        activity: Activity,
        bitmap: Bitmap?,
        maxSize: Int,
        compressionQuality: Int
    ): File? {
        if (null == bitmap)
            return null

        val newBitmap: Bitmap = resizeBitmap(bitmap = bitmap, maxSize = maxSize)

        val outStream = ByteArrayOutputStream()
        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        newBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            compressionQuality,
            outStream
        )

        // we save the file, at least until we have made use of it
        val f = getImageTempFile(context = activity)
        //write the bytes in file
        val fo = FileOutputStream(f)
        fo.write(outStream.toByteArray())
        // remember close de FileOutput
        fo.close()

        return f
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var newBitmap: Bitmap = bitmap

        if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val ratio = min(
                a = maxSize.toDouble() / bitmap.width.toDouble(),
                b = maxSize.toDouble() / bitmap.height.toDouble()
            )

            val width = (ratio * bitmap.width).roundToInt()
            val height = (ratio * bitmap.height).roundToInt()

            newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            bitmap.recycle()
        }

        return newBitmap
    }

    /**
     *  Here creating a cover image from video
     *
     *  @param context
     *  @param videoUri
     *  @return video's cover image in bitmap format
     */
    internal fun getCoverImageFromVideo(context: Context, videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        var bitmap: Bitmap? = null

        Log.d(TAG, "getCoverImageFromVideo: videoUri- $videoUri")

        try {
            retriever.apply {
                setDataSource(context, videoUri)

                frameAtTime?.apply {
//                    bitmap = resizeBitmap(this, maxSize)
                    bitmap = this
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getCoverImageFromVideo: ${e.printStackTrace()}")
        } finally {
            retriever.release()
        }

        return bitmap
    }

    /**
     *  Here compressing video
     */
    internal fun compressVideo(
        context: Context,
        inputFile: File,
        outputDirectory: File,
        videoCompressionListener: VideoCompressionListener
    ) {
        videoCompressAsyncTask?.apply {
            if (!isCancelled) cancel(true)
        }

        videoCompressAsyncTask = VideoCompressAsyncTask(context, videoCompressionListener).apply {
            execute(inputFile.path, outputDirectory.path)
        }
    }

    private class VideoCompressAsyncTask(
        context: Context,
        listener: VideoCompressionListener
    ) : AsyncTask<String?, String?, String?>() {

        private var context: Context? = null
        private var listener: VideoCompressionListener? = null

        init {
            this.context = context
            this.listener = listener
        }

        override fun doInBackground(vararg paths: String?): String? {
            Log.d(TAG, "VideoCompressAsyncTask: doInBackground")
            if (paths[0] == null || paths[1] == null)
                return null

            val imageFile = File(paths[0]!!)
            val length = imageFile.length() / 1024f // Size in KB
            val size: String =
                if (length >= 1024) "${(length / 1024f)} MB" else "$length KB"

            Log.d(TAG, "VideoCompressAsyncTask: original_size- $size")

            return SiliCompressor.with(context).compressVideo(paths[0], paths[1])
        }

        override fun onPostExecute(compressedFilePath: String?) {
            super.onPostExecute(compressedFilePath)
            Log.d(TAG, "VideoCompressAsyncTask: onPostExecute")

            if (compressedFilePath == null) {
                listener?.onVideoCompressionFailure()
                return
            }

            val imageFile = File(compressedFilePath)
            val length = imageFile.length() / 1024f // Size in KB
            val size: String =
                if (length >= 1024) (length / 1024f).toString() + " MB" else "$length KB"

            Log.d(TAG, "VideoCompressAsyncTask: compressed_size- $size")

            listener?.onVideoCompressionSuccess(compressedVideoFile = imageFile)

            context = null
            listener = null
            videoCompressAsyncTask = null
        }

        override fun onCancelled(result: String?) {
            super.onCancelled(result)
            Log.d(TAG, "VideoCompressAsyncTask: onCancelled")

            listener?.onVideoCompressionFailure()

            context = null
            listener = null
            videoCompressAsyncTask = null
        }
    }
}