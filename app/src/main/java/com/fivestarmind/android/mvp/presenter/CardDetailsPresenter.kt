package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.UserPaymentRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.UserPaymentResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class CardDetailsPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = CardDetailsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {
        fun onSuccessResponse(response: UserPaymentResponseModel)
        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun apiPostForUserPayments(token: String, requestUserPayment: UserPaymentRequestModel) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = requestUserPayment)

        Log.d(TAG, "request UserPayments for Body = $myRequest")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userPayment(token, requestUserPayment)

        call.enqueue(object : Callback<UserPaymentResponseModel> {
            override fun onResponse(
                call: Call<UserPaymentResponseModel>,
                response: Response<UserPaymentResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(TAG, "response UserPayments for Body = $myResponse")
                    responseCallBack.onSuccessResponse(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(call: Call<UserPaymentResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}