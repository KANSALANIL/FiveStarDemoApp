package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SeeAllResModel
import com.fivestarmind.android.mvp.model.response.TagAllCategoryResModel
import com.fivestarmind.android.mvp.model.response.TagResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class FilterPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = FilterPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onResponseSuccess(response: TagResponseModel)

        fun onTagAllCategoryResponseSuccess(response: TagAllCategoryResModel)

        fun onFailureResponse(message: String)
    }

    fun hitApitoGetTagListing(token: String, appID: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getTagListing(token = token, appID = appID)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<TagResponseModel> {
            override fun onResponse(
                call: Call<TagResponseModel>, response: Response<TagResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse = GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG, "hitApitoGetTagListing respnse= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error hitApitoGetTagListing Body")
                    error.message = response.message()
                    //message = response.message()
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error.message)*/
                     responseCallBack.onFailureResponse(error.message)
                }
            }

            override fun onFailure(call: Call<TagResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                error.message= t.message!!
                responseCallBack.onFailureResponse(error.message)
            }
        })
    }


 fun hitApitoGetTagAllCategory(token: String, appID: Int,tagId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getTagAllCategoryListing(token = token, appID = appID, tagId = tagId)

        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<TagAllCategoryResModel> {
            override fun onResponse(
                call: Call<TagAllCategoryResModel>, response: Response<TagAllCategoryResModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse = GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG, "hitApitoGetTagListing respnse= $myResponse"
                    )
                    responseCallBack.onTagAllCategoryResponseSuccess(response.body())

                } else {
                    Log.d(TAG, "response for error hitApitoGetTagListing Body")

                    error.message = response.message()
                    responseCallBack.onFailureResponse(error.message)
                    // responseCallBack.onFailureResponse(message!!)
                }
            }

            override fun onFailure(call: Call<TagAllCategoryResModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                error.message =t.message!!
                responseCallBack.onFailureResponse(error.message)
            }
        })
    }

}