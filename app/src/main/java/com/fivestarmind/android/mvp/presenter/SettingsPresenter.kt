package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.UpdateSettingsRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SettingsResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class SettingsPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = SettingsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onResponseSuccess(response: SettingsResponse)

        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun getSettingsStatus(token: String) {

        Log.d(TAG, "getSettingsStatus : token - $token")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getSettings(token)

        call.enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "Get Settings Status for Body = $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun updateSettings(token: String, updateSettingsRequestModel: UpdateSettingsRequestModel) {

        val body = GsonHelper.convertJavaObjectToJsonString(model = updateSettingsRequestModel)
        Log.d(TAG, "updateSettings: body- $body")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.updateSettings(
            token = token,
            updateSettingsRequestModel = updateSettingsRequestModel
        )

        call.enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "updateSettings for Body = $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

}