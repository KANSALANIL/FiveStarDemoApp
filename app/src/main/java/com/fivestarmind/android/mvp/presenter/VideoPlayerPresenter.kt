package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.GetProductFileResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoPlayerPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = VideoPlayerPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String = ""

    interface ResponseCallBack {

        fun onViewsSubmittedSuccess(response: CommonResponse)
        fun onAddFavResponseSuccess(response: CommonResponse)
        fun onGetFavResponseSuccess(getProductFileResponse: GetProductFileResponse)


        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun hitApiForVideoView(
        token: String,
        appId: Int,
        videoId: Int,
        videoDurationRequestModel: VideoDurationRequestModel
    ) {

        Log.d(TAG, "hitApiForVideoView: videoId- $videoId")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.videoViews(
            token = token,
            appID = appId,
            videoId = videoId,
            videoDurationRequestModel = videoDurationRequestModel
        )
        Log.e(TAG, "Url: " + call.request().url().toString())

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
                        "response VideoView for Body = $myResponse"
                    )
                    responseCallBack.onViewsSubmittedSuccess(response.body())

                } else {


                    message = response.message()
                    error.message = message
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Error VideoView for Body = $myResponse"
                    )
                    error = Gson().fromJson(reader, ErrorResponse::class.java)*/
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                error.message = t.message!!
                responseCallBack.onResponseFailure(error)
            }
        })
    }


    /**
     * call AddFavourite api
     */
    fun hitApiToAddFavourite(
        token: String,
        appId: Int,
        addFavouriteRequest: AddFavouriteRequestModel
    ) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = addFavouriteRequest)
        Log.d(TAG, "hitApiToAddFavourite Request --- $myRequest")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.addFavourite(
            token = token,
            appID = appId,
            addFavouriteRequest = addFavouriteRequest
        )

        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                Log.d(
                    TAG,
                    "hitApiToAddFavourite message_response= ${response.message()}"
                )
                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "hitApiToAddFavourite response= $myResponse"
                    )
                    responseCallBack.onAddFavResponseSuccess(response.body())

                } else {

                    error.message = response.message()
                    /* val reader: Reader = response.errorBody()!!.charStream()
                     val myResponse =
                         GsonHelper.convertJavaObjectToJsonString(model = response)

                     Log.d(
                         TAG,
                         "Error Add Favourite for Body = $myResponse"
                     )
                     error = Gson().fromJson(reader, ErrorResponse::class.java)*/
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")

                error.message = t.message!!
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    /**
     * call AddFavourite api
     */
    fun hitApiToGetFavourite(token: String, appId: Int, audioId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getFavourite(token = token, appID = appId, audioId = audioId)

        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<GetProductFileResponse> {
            override fun onResponse(
                call: Call<GetProductFileResponse>,
                response: Response<GetProductFileResponse>
            ) {
                Log.d(
                    TAG,
                    "hitApiToGetFavourite message_response= ${response.message()}"
                )
                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "hitApiToAddFavourite response= $myResponse"
                    )

                    responseCallBack.onGetFavResponseSuccess(response.body())

                } else {

                    message = response.message()
                    error.message = message
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Error Add Favourite for Body = $myResponse"
                    )
                    error = Gson().fromJson(reader, ErrorResponse::class.java)*/
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<GetProductFileResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                error.message = t.message!!

                responseCallBack.onResponseFailure(error)
            }

        })
    }
}