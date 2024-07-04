package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SeeAllResModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class SeeAllCategoryListDataPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = SeeAllCategoryListDataPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onResponseSuccess(response: SeeAllResModel)

        fun onAddFavResponseSuccess(response: CommonResponse)
        fun onViewsSubmittedSuccess(response: CommonResponse)

        fun onFailureResponse(message: String)
    }

    fun hitSeeAllCategoryDataList(token:String, appId:Int, productId:Int, pageNumber:Int, pageSize:Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getAllCategoryDataList(token = token, appID = appId,productId= productId,page= pageNumber, size = pageSize)

        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<SeeAllResModel> {
            override fun onResponse(
                call: Call<SeeAllResModel>,
                response: Response<SeeAllResModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get LetsGo respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error Lets Go Body")

                    message = response.message()

                    responseCallBack.onFailureResponse(message!!)
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)*/
                }
            }

            override fun onFailure(call: Call<SeeAllResModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }

    /**
     * call AddFavourite api
     */
    fun hitApiToAddFavourite(token: String, appId:Int, addFavouriteRequest: AddFavouriteRequestModel) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = addFavouriteRequest)
        Log.d(TAG, "hitApiToAddFavourite Request --- $myRequest")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.addFavourite(token = token, appID = appId, addFavouriteRequest = addFavouriteRequest)

        Log.e(TAG,"Url: "+ call.request().url().toString());

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
                    responseCallBack.onFailureResponse(response.message())
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }

    fun hitSeeParticularCategoryDataList(token:String, appId:Int, tagId:Int, categoryId:Int, pageNumber:Int, pageSize:Int,) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getParticularCategoryListing(token = token, appID = appId, tagId = tagId, categoryId = categoryId,page= pageNumber, size = pageSize)

        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<SeeAllResModel> {
            override fun onResponse(
                call: Call<SeeAllResModel>,
                response: Response<SeeAllResModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get LetsGo respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error Lets Go Body")

                    message = response.message()

                    responseCallBack.onFailureResponse(message!!)
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)*/
                }
            }

            override fun onFailure(call: Call<SeeAllResModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }


    fun hitApiForVideoView(
        token: String,
        appId: Int,
        videoId: Int,
        videoDurationRequestModel: VideoDurationRequestModel
    ) {

        Log.d(TAG, "hitApiForAudioView: videoId- $videoId")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        var call = apiService.videoViews(
            token = token,
            appID= appId,
            videoId = videoId,
            videoDurationRequestModel = videoDurationRequestModel
        )
            Log.e(TAG,"Url: "+ call.request().url().toString());

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

                    Log.d(
                        TAG,
                        "response VideoView>>> = ${response.code()
                        }"
                    )
       /*             val reader: Reader = response.errorBody()!!.charStream()
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Error VideoView for Body = $myResponse"
                    )
                    error = Gson().fromJson(reader, ErrorResponse::class.java)*/
                    responseCallBack.onFailureResponse(response.message())
                }
            }

            override fun onFailure(
                call: Call<CommonResponse>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }


}