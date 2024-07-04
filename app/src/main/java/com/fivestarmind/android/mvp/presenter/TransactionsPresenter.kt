package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.TransactionsResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class TransactionsPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = TransactionsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onResponseSuccess(response: TransactionsResponseModel)

        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun getJournalList(token: String, currentPage: Int, limit: Int) {
        Log.d(TAG, "getJournalList : token - $token , currentPage - $currentPage")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getTransactionListing(token, currentPage, limit)

        call.enqueue(object : Callback<TransactionsResponseModel> {
            override fun onResponse(
                call: Call<TransactionsResponseModel>,
                response: Response<TransactionsResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response logout for Body = $myResponse"
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

            override fun onFailure(call: Call<TransactionsResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}