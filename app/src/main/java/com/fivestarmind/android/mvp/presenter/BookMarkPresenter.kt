package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookMarkPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = BookMarkPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onResponseSuccess(response: BookMarkResponseModel)

        fun onAddFavResponseSuccess(response: CommonResponse)

        fun onViewsSubmittedSuccess(response: CommonResponse)

        fun onFailureResponse(message: String)
    }

 //   fun hitApiForBookMark(token:String, appId: Int, pageNumber:Int,limit:Int) {
    fun hitApiForBookMark(token:String, appId: Int,categoryId:Int,pageNumber:Int,limit:Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
      //  val call = apiService.getBookMarkListing(token = token, appID = appId, page = pageNumber,size = limit)
        val call = apiService.getBookMarkListing(token = token, appID = appId, categoryId = categoryId, page = pageNumber,size = limit )

        Log.e(TAG,"Token: "+ token);
        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<BookMarkResponseModel> {
            override fun onResponse(
                call: Call<BookMarkResponseModel>,
                response: Response<BookMarkResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get BookMark respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error BookMark Body")

                    message = response.message()
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)*/
                    responseCallBack.onFailureResponse(message!!)
                }
            }

            override fun onFailure(call: Call<BookMarkResponseModel>, t: Throwable) {
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