package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.DesignAssessmentRequestModel
import com.fivestarmind.android.mvp.model.response.DesignAssessmentResponseModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class SelfAssessmentPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = SelfAssessmentPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {
        fun onUpdateAssessmentSuccess(designAssessmentResponseModel: DesignAssessmentResponseModel)

        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun hitApiToUpdateAssessment(
        designAssessmentRequestModel: DesignAssessmentRequestModel,
        selfAssessmentType: Int
    ) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = designAssessmentRequestModel)
        Log.d(TAG, "hitApiToUpdateAssessment Request --- $myRequest")

        val apiService = ApiClient.client.create(ApiInterface::class.java)

        val call = when (selfAssessmentType) {
            AppConst.NUMBER.ZERO ->
                apiService.updateDesignAssessment(
                    token = SharedPreferencesHelper.getAuthToken(),
                    designAssessmentRequestModel = designAssessmentRequestModel
                )

            AppConst.NUMBER.ONE -> apiService.updateDevelopAssessment(
                token = SharedPreferencesHelper.getAuthToken(),
                designAssessmentRequestModel = designAssessmentRequestModel
            )

            AppConst.NUMBER.TWO -> apiService.updateDisplayAssessment(
                token = SharedPreferencesHelper.getAuthToken(),
                designAssessmentRequestModel = designAssessmentRequestModel
            )

            else -> apiService.updateDesignAssessment(
                token = SharedPreferencesHelper.getAuthToken(),
                designAssessmentRequestModel = designAssessmentRequestModel
            )
        }

        call.enqueue(object : Callback<DesignAssessmentResponseModel> {
            override fun onResponse(
                call: Call<DesignAssessmentResponseModel>,
                response: Response<DesignAssessmentResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "UpdateAssessment response = $myResponse"
                    )
                    responseCallBack.onUpdateAssessmentSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(
                call: Call<DesignAssessmentResponseModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}