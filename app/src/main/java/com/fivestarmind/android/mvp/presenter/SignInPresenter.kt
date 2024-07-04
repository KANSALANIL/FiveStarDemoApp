package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.SignInRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.UserLoginResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class SignInPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = SignInPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var messsage: String? = null

    interface ResponseCallBack {

        fun onLoginResponseSuccess(response:UserLoginResponseModel)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun hitApiForSignIn(signInRequestModel: SignInRequestModel) {

        val body = GsonHelper.convertJavaObjectToJsonString(model = signInRequestModel)
        Log.d(TAG, "hitApiForSignIn: body- $body")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userLogin(signInRequestModel)
        Log.e(TAG,"Url: "+ call.request().url().toString());

        call.enqueue(object : Callback<UserLoginResponseModel> {
            override fun onResponse(
                call: Call<UserLoginResponseModel>,
                responseModelLogin: Response<UserLoginResponseModel>
            ) {

                if (responseModelLogin.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModelLogin)

                    Log.d(
                        TAG,
                        "response login for Body = $myResponse"
                    )
                    responseCallBack.onLoginResponseSuccess(responseModelLogin.body())

                } else {
                    //messsage = responseModelLogin.message()

                    if (responseModelLogin.code()==404){
                   //    error.message = " "

                        Log.d(TAG,"responseModelLogin:  "+responseModelLogin.message())
                        error.message = ""
                        error.message = "User does not exist."
                        responseCallBack.onResponseFailure(error)
                     //   responseCallBack.onResponseFailure(error)
                    }else {

                        val reader: Reader = responseModelLogin.errorBody()!!.charStream()
                        val myResponse =
                            GsonHelper.convertJavaObjectToJsonString(model = responseModelLogin)

                        Log.d(
                            TAG,
                            "Error login for Body = $myResponse"
                        )
                        error = Gson().fromJson(reader, ErrorResponse::class.java)
                        Log.d(
                            TAG,
                            "response for error Body = ${Gson().toJson(responseModelLogin.errorBody())}"
                        )
                        //  responseCallBack.onResponseFailure(responseModel.errorBody())
                        responseCallBack.onResponseFailure(error)

                    }
                }
            }

            override fun onFailure(
                call: Call<UserLoginResponseModel>,
                t: Throwable
            ) {
                error.message = t.message!!
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }
}