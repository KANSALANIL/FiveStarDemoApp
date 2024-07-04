package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.activity.AddJournalActivity
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.Reader

class AddJournalPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = AddJournalPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {
        fun onSuccessResponse(response: CommonResponse)
        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun apiPostToCreateJournalWithImage(
        token: String,
        file: File,
        date: String,
        note: String
    ) {

        val reqFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val fileToUpload = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val date = RequestBody.create(MediaType.parse("multipart/form-data"), date)
        val notes = RequestBody.create(MediaType.parse("multipart/form-data"), note)

        Log.d(TAG, "apiPostToCreateJournal: body- fileToUpload -- $fileToUpload")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.createJournalWithImage(
            token = token, image = fileToUpload, date = date, note = notes
        )

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(
                        TAG,
                        "response Create Journal Success = $myResponse"
                    )

                    Log.d(TAG, "response for create Journal for Body = ${response.body()}")
                    responseCallBack.onSuccessResponse(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun apiPostToCreateJournalWithVideo(
        activity: AddJournalActivity,
        token: String,
        file: File,
        coverImageFile: File,
        date: String,
        note: String
    ) {

        Log.d(TAG, " apiPostToCreateJournalWithVideo: body- token -- $token")
        Log.d(TAG, " apiPostToCreateJournalWithVideo: body- fileToUpload -- $file")
        Log.d(TAG, " apiPostToCreateJournalWithVideo: body- coverImageFile -- $coverImageFile")
        Log.d(TAG, " apiPostToCreateJournalWithVideo: body- date -- $date")
        Log.d(TAG, " apiPostToCreateJournalWithVideo: body- note -- $note")
        Log.d(
            TAG, "file: ext- ${file.extension}"
        )

        val reqFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val fileToUpload = MultipartBody.Part.createFormData("video", file.name, reqFile)

        val reqCoverImageFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), coverImageFile)
        val videoCoverFileToUpload =
            MultipartBody.Part.createFormData("cover_image", coverImageFile.name, reqCoverImageFile)

        val date = RequestBody.create(MediaType.parse("multipart/form-data"), date)
        val notes = RequestBody.create(MediaType.parse("multipart/form-data"), note)

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.createJournalWithVideo(
            token = token,
            video = fileToUpload,
            cover_image = videoCoverFileToUpload,
            date = date,
            note = notes
        )

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(
                        TAG,
                        "response Create Journal Success = $myResponse"
                    )

                    Log.d(TAG, "response for create Journal for Body = ${response.body()}")
                    responseCallBack.onSuccessResponse(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "response Create Journal failure = ${
                            GsonHelper.convertJavaObjectToJsonString(
                                model = error
                            )
                        }"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }
}