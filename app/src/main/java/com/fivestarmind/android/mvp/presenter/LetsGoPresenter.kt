package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AppTimeRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.LetsGoResponse
import com.fivestarmind.android.mvp.model.response.VideoViewResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class LetsGoPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = LetsGoPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onResponseSuccess(response: LetsGoResponse)

        fun onAppTimeResponseSuccess(commonResponse: CommonResponse)

        fun onFailureResponse(message: String)
    }

    fun getOrganisationLogo(token:String,appID: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getLogoImage(token=token,appID = appID)
        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<LetsGoResponse> {
            override fun onResponse(
                call: Call<LetsGoResponse>,
                response: Response<LetsGoResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "getOrganisationLogo respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error getOrganisationLogo Body")

                    message = response.message()
                  /*  val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")*/
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(message!!)
                   // responseCallBack.onFailureResponse(message!!)
                }
            }

            override fun onFailure(call: Call<LetsGoResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)
            }
        })
    }


    fun apiToSendAppTime(token: String,appTimeRequestModel: AppTimeRequestModel,appId:Int) {
        Log.d(TAG, "apiToSendAppTime")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.sendAppTime(token, appTimeRequestModel,appId)
        Log.e(TAG,"Request: "+ GsonHelper.convertJavaObjectToJsonString(appTimeRequestModel));
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
                        "apiToSendAppTime respnse= $myResponse"
                    )
                    responseCallBack.onAppTimeResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response  for apiToSendAppTime error ")

                  //  message = response.message()
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error.message)
                  //  responseCallBack.onFailureResponse(message!!)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error.message)
            }
        })
    }

}