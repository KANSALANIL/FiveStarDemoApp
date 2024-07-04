package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.activity.MyProgramsActivity
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.MyProgramsResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Response
import java.io.Reader

class MyProgramsPresenter(
    private val responseCallBack: ResponseCallBack,
    private val activity: MyProgramsActivity
) {

    private var error = ErrorResponse()


    interface ResponseCallBack {
        fun onResponseSuccess(response: MyProgramsResponseModel)

        fun onFailureResponse(errorResponse: String)
    }

    internal fun apiToGetProductResponse(token: String, currentPage: Int, limit: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getProducts(token, currentPage, limit)

        call.enqueue(object : retrofit2.Callback<MyProgramsResponseModel> {

            override fun onResponse(
                call: Call<MyProgramsResponseModel>,
                response: Response<MyProgramsResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d("sss", "Response from programs = $myResponse")

                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    responseCallBack.onFailureResponse(error.errorDescription)
                }
            }

            override fun onFailure(call: Call<MyProgramsResponseModel>, t: Throwable?) {
                responseCallBack.onFailureResponse(activity.getString(R.string.check_your_network_connection))
            }

        })

    }
}