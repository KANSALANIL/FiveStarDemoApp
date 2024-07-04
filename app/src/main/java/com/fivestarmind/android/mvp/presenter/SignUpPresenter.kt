package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.SignUpRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.SignInSignUpResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class SignUpPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = SignUpPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onSignUpResponseSuccess(response: SignInSignUpResponseModel.SignInSignUpResponseFirstModel)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun hitApiForSignUp(signUpRequestModel: SignUpRequestModel) {

        val body = GsonHelper.convertJavaObjectToJsonString(model = signUpRequestModel)
        Log.d(TAG, "hitApiForSignUp: body- $body")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userSignUp(signUpRequestModel)

        call.enqueue(object : Callback<SignInSignUpResponseModel.SignInSignUpResponseFirstModel> {
            override fun onResponse(
                call: Call<SignInSignUpResponseModel.SignInSignUpResponseFirstModel>,
                responseModelSignUp: Response<SignInSignUpResponseModel.SignInSignUpResponseFirstModel>
            ) {

                if (responseModelSignUp.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModelSignUp)

                    Log.d(
                        TAG,
                        "response SignUp for Body = $myResponse"
                    )
                    responseCallBack.onSignUpResponseSuccess(responseModelSignUp.body())

                } else {
                    val reader: Reader = responseModelSignUp.errorBody()!!.charStream()
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModelSignUp)

                    Log.d(
                        TAG,
                        "Error login for Body = $myResponse"
                    )
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "response for error Body = ${Gson().toJson(responseModelSignUp.errorBody())}"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(
                call: Call<SignInSignUpResponseModel.SignInSignUpResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }
}