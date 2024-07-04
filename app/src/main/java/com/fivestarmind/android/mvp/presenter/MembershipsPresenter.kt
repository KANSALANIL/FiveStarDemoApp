package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.RequestMembershipModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader
import java.util.*
import kotlin.collections.ArrayList

class MembershipsPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = MembershipsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onMembershipPackagesResponseSuccess(packagesResponse: ArrayList<MembershipPackagesResponseModel>)

        fun onMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onSuccessCouponResponse(response: MembershipCouponResponseModel.MembershipCouponResponseFirstModel)

        fun onFailureResponse(errorResponse: ErrorResponse)
    }

    fun getUserMembership(currentTime: Long) {
        Log.d(TAG, "Current time ---- $currentTime")

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

    fun getMembershipList() {
        Log.d(TAG, "getMembershipList")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getMembershipsListing()

        call.enqueue(object : Callback<ArrayList<MembershipPackagesResponseModel>> {
            override fun onResponse(
                call: Call<ArrayList<MembershipPackagesResponseModel>>,
                packagesResponse: Response<ArrayList<MembershipPackagesResponseModel>>
            ) {

                if (packagesResponse.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = packagesResponse)

                    Log.d(
                        TAG,
                        "getMembershipList response = $myResponse"
                    )
                    responseCallBack.onMembershipPackagesResponseSuccess(packagesResponse.body())

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
                call: Call<ArrayList<MembershipPackagesResponseModel>>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun updateMembership(requestMembershipModel: RequestMembershipModel, token: String) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = requestMembershipModel)
        Log.d(TAG, "updateMembership Request --- $myRequest")
        Log.d(TAG, "updateMembership Token --- $token")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.updateMembership(
            token = token,
            requestMembershipModel = requestMembershipModel
        )

        call.enqueue(object : Callback<MembershipResponseModel.MembershipResponseFirstModel> {
            override fun onResponse(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                response: Response<MembershipResponseModel.MembershipResponseFirstModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "updateMembership response = $myResponse"
                    )
                    responseCallBack.onMembershipResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
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

    fun apiToApplyMembershipCoupon(token: String, couponCode: String) {
        Log.d(
            TAG,
            "token = $token"
        )

        Log.d(
            TAG,
            "Request for coupon applied to Product. for Body = $couponCode"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.applyMembershipCoupon(token, couponCode, TimeZone.getDefault().getID())

        call.enqueue(object : Callback<MembershipCouponResponseModel.MembershipCouponResponseFirstModel> {
            override fun onResponse(
                call: Call<MembershipCouponResponseModel.MembershipCouponResponseFirstModel>,
                response: Response<MembershipCouponResponseModel.MembershipCouponResponseFirstModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response for coupon applied to membership. for Body = $myResponse"
                    )
                    responseCallBack.onSuccessCouponResponse(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = $error")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(
                call: Call<MembershipCouponResponseModel.MembershipCouponResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}