package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.MessagesListingResponseModel
import com.fivestarmind.android.mvp.model.response.SendChatDataModel
import com.fivestarmind.android.mvp.model.response.ThreadIdResponse
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesListingPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = MessagesListingPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onMessagesListingResponseSuccess(response: MessagesListingResponseModel)
        fun onGetThreadIdResponseSuccess(response: ThreadIdResponse)
        fun onGetChatListingResponseSuccess(response: ThreadIdResponse)

        fun onSendChatResponseSuccess(message: String)
        fun onFailureResponse(message: String)
    }

    fun hitMessagesListingApi(token:String) {

        val apiService = ApiClient.chatClient.create(ApiInterface::class.java)
        val call = apiService.getThreadsList(token = token)
        Log.e(TAG, "Url: " + call.request().url().toString())
        Log.e(TAG, "token: " + token)

        call.enqueue(object : Callback<MessagesListingResponseModel> {
            override fun onResponse(
                call: Call<MessagesListingResponseModel>,
                responseModel: Response<MessagesListingResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)
                    Log.d(
                        TAG,
                        "response success for hitMessagesListingApi = $myResponse"
                    )
                    responseCallBack.onMessagesListingResponseSuccess(responseModel.body())

                } else {
                    responseCallBack.onFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<MessagesListingResponseModel>, t: Throwable) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)


            }
        })
    }


    fun hitGetThreadIdApi(token:String,userId:Int) {

        val apiService = ApiClient.chatClient.create(ApiInterface::class.java)
        val call = apiService.getChatUserDetail(token = token, userId = userId)
        Log.e(TAG, "Url: " + call.request().url().toString())
        Log.e(TAG, "token: " + token)
        Log.e(TAG, "userId: " + userId)

        call.enqueue(object : Callback<ThreadIdResponse> {
            override fun onResponse(
                call: Call<ThreadIdResponse>,
                responseModel: Response<ThreadIdResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)
                    Log.d(
                        TAG,
                        "response success for hitGetThreadIdApi = $myResponse"
                    )
                    responseCallBack.onGetThreadIdResponseSuccess(responseModel.body())

                } else {
                    responseCallBack.onFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<ThreadIdResponse>, t: Throwable) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)


            }
        })
    }

    fun hitGetChatListing(token:String, threadId:String,messageId:String) {

        val apiService = ApiClient.chatClient.create(ApiInterface::class.java)
        val call = apiService.getChatByThreadID(token = token, threadId = threadId,messageId = messageId)
        Log.e(TAG, "Url: " + call.request().url().toString())
        Log.e(TAG, "token: " + token)
        Log.e(TAG, "threadId: " + threadId)
        Log.e(TAG, "messageId: " + messageId)

        call.enqueue(object : Callback<ThreadIdResponse> {
            override fun onResponse(
                call: Call<ThreadIdResponse>,
                responseModel: Response<ThreadIdResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)
                    Log.d(
                        TAG,
                        "response success for hitGetChatListing = $myResponse"
                    )
                    responseCallBack.onGetChatListingResponseSuccess(responseModel.body())

                } else {
                    responseCallBack.onFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<ThreadIdResponse>, t: Throwable) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)


            }
        })
    }


    fun hitSendMessageApi(token:String,sendChatDataModel: SendChatDataModel) {

        val apiService = ApiClient.chatClient.create(ApiInterface::class.java)
        val call = apiService.sendMessage(token = token, sendChatDataModel = sendChatDataModel)
        Log.e(TAG, "Url: " + call.request().url().toString())
        Log.e(TAG, "sendChatDataModel: " +  Gson().toJson(sendChatDataModel))
        Log.e(TAG, "token: " + token)

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                responseModel: Response<String>
            ) {

                if (responseModel.code() == 204) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)
                    Log.d(
                        TAG,
                        "response success for hitSendMessageApi = $myResponse"
                    )
                    responseCallBack.onSendChatResponseSuccess("Successful send message")

                } else {
                    responseCallBack.onFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(t.message!!)


            }
        })
    }

}