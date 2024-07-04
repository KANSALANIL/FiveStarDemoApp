package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.VideosRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class VideoDurationPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = VideoDurationPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onSaveVideosDurationSuccess(response: CommonResponse)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun hitApiToSaveDuration(
        token: String,
        videosRequestModel: VideosRequestModel
    ) {
        val myResponse =
            GsonHelper.convertJavaObjectToJsonString(model = videosRequestModel)

        Log.d(
            TAG,
            "request SaveDuration  = $myResponse"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.saveVideoDuration(
            token = token,
            videosRequestModel = videosRequestModel
        )

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response SaveDuration  = $myResponse"
                    )
                    responseCallBack.onSaveVideosDurationSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Error login for Body = $myResponse"
                    )
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }
}