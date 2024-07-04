package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class BookMarkAllCategoryPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = BookMarkAllCategoryPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {
        fun onResponseSuccess(response: BookmarkAllCategoryResModel)
        fun onFailureResponse(message: String)
    }

    /**
     * call AddFavourite api
     */
    fun hitApiToBookmarkAllCategory(token: String, appId:Int) {

        Log.d(TAG, "hitApiToBookmarkAllCategory token --- $token")
        Log.d(TAG, "hitApiToBookmarkAllCategory appId --- $appId")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getBookmarkAllCategory(token = token, appId = appId)

        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<BookmarkAllCategoryResModel> {
            override fun onResponse(
                call: Call<BookmarkAllCategoryResModel>,
                response: Response<BookmarkAllCategoryResModel>
            ) {
                Log.d(
                    TAG,
                    "hitApiToBookmarkAllCategory message_response= ${response.message()}"
                )
                if (response.isSuccessful) {

                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "hitApiToBookmarkAllCategory response= $myResponse"
                    )
                    responseCallBack.onResponseSuccess(response.body())

                } else {
                    responseCallBack.onFailureResponse(response.message())
                }
            }

            override fun onFailure(call: Call<BookmarkAllCategoryResModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message.toString())
            }
        })
    }

}