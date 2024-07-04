package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.MenuDetailListResponse
import com.fivestarmind.android.mvp.model.response.ProductImageDetailResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuDetailListPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = MenuDetailListPresenter::class.java.simpleName
    private var message = String

    interface ResponseCallBack {

        fun onResponseSuccess(response: MenuDetailListResponse)

        fun onResponseSuccess(response: ProductImageDetailResponseModel)

        fun onAddFavResponseSuccess(response: CommonResponse)

        fun onViewsSubmittedSuccess(response: CommonResponse)

        fun onFailureResponse(messages: String)
    }

    fun getMenuDetailListApi(token: String, appId:Int, id: Int,pageNumber:Int,limit:Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getMenuDetail(token = token, appID = appId, id = id, page= pageNumber, size=limit)
        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<MenuDetailListResponse> {
            override fun onResponse(
                call: Call<MenuDetailListResponse>,
                response: Response<MenuDetailListResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get MenuDetail respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    responseCallBack.onFailureResponse(response.message())
                }
            }

            override fun onFailure(call: Call<MenuDetailListResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }

    /**
     * call AddFavourite api
     */
    fun hitApiToAddFavourite(token: String, appId:Int,addFavouriteRequest: AddFavouriteRequestModel) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = addFavouriteRequest)
        Log.d(TAG, "hitApiToAddFavourite Request --- $myRequest")
        Log.d(TAG, "hitApiToAddFavourite token --- $token")


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


    //Product detail api
    fun getProductImageDetailApi(token: String, appId: Int, imageId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getProductDetail(token = token, appID = appId, imageId = imageId)

        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<ProductImageDetailResponseModel> {
            override fun onResponse(
                call: Call<ProductImageDetailResponseModel>,
                response: Response<ProductImageDetailResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get ProductDetail response= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    responseCallBack.onFailureResponse(response.message())
                }
            }

            override fun onFailure(call: Call<ProductImageDetailResponseModel>, t: Throwable) {
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
        val call = apiService.videoViews(
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