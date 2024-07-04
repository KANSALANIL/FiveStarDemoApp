package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.SuggestionsRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = ContactUsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onContactUsResponseSuccess(response: CommonResponse)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun hitApiToSubmitContactUs(token: String, suggestionsRequestModel: SuggestionsRequestModel) {

        val body = GsonHelper.convertJavaObjectToJsonString(model = suggestionsRequestModel)
        Log.d(TAG, "hitApiToSubmitContactUs: body- $body")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.submitContactUs(token, suggestionsRequestModel)

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
                        "response contact us submit Success = $myResponse"
                    )
                    responseCallBack.onContactUsResponseSuccess(response.body())

                } else {
                    val reader = response.errorBody().charStream()

                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")

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