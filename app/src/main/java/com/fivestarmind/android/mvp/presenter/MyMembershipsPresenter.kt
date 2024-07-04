package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.MembershipResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class MyMembershipsPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = MyMembershipsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {
        fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onCancelUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun getUserMembership(currentTime: Long) {
        Log.d(TAG, "Current time ---- $currentTime")
        Log.d(TAG, "token ---- ${SharedPreferencesHelper.getAuthToken()}")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getUserMembership(
            token = SharedPreferencesHelper.getAuthToken(),
            currentTime = currentTime
        )

        call.enqueue(object : Callback<MembershipResponseModel.MembershipResponseFirstModel> {
            override fun onResponse(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                packagesResponse: Response<MembershipResponseModel.MembershipResponseFirstModel>
            ) {
                if (packagesResponse.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = packagesResponse)

                    Log.d(
                        TAG,
                        "getUserMembership response = $myResponse"
                    )
                    responseCallBack.onUserMembershipResponseSuccess(packagesResponse.body())

                } else {
                    val reader: Reader = packagesResponse.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "response for error Body = ${packagesResponse.errorBody().charStream()}"
                    )
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun cancelUserMembership() {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.cancelUserMembership(token = SharedPreferencesHelper.getAuthToken())

        call.enqueue(object : Callback<MembershipResponseModel.MembershipResponseFirstModel> {
            override fun onResponse(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                packagesResponse: Response<MembershipResponseModel.MembershipResponseFirstModel>
            ) {
                if (packagesResponse.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = packagesResponse)

                    Log.d(
                        TAG,
                        "cancelUserMembership response = $myResponse"
                    )
                    responseCallBack.onCancelUserMembershipResponseSuccess(packagesResponse.body())

                } else {
                    val reader: Reader = packagesResponse.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "response for error Body = ${packagesResponse.errorBody().charStream()}"
                    )
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}