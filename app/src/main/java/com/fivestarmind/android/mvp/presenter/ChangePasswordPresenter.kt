package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.RequestChangePassword
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class ChangePasswordPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = ChangePasswordPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onChangePasswordResponseSuccess(msg: CommonResponse)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun apiPutForChangePassword(token: String, requestChangePassword: RequestChangePassword) {

        val body = GsonHelper.convertJavaObjectToJsonString(model = requestChangePassword)
        Log.d(TAG, "apiPutForChangePassword: body- $body")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.callForChangePassword(token, requestChangePassword)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    Log.d(TAG, "response change password for Body = ${response.body()}")
                    responseCallBack.onChangePasswordResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
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