package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.CouponRequestModel
import com.fivestarmind.android.mvp.model.response.CartResponseModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.CouponResponseModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class CartPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = CartPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onSuccessResponse(response: CartResponseModel)

        fun onFailureResponse(errorResponse: ErrorResponse)

        fun onSuccessToRemoveProduct(response: CommonResponse, productId: Int)

        fun onSuccessCouponResponse(response: CouponResponseModel.CouponResponseFirstModel)

        fun onSuccessCouponResponseList(response: CouponResponseModel.CouponResponseSecondModel)
    }

    fun getCartProductDetails(token: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getCartProduct(token)

        call.enqueue(object : Callback<CartResponseModel> {
            override fun onResponse(
                call: Call<CartResponseModel>,
                response: Response<CartResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(TAG, "response Cart Detail for Body = $myResponse")
                    responseCallBack.onSuccessResponse(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(call: Call<CartResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun apiToRemoveProductFromCart(token: String, productId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.removeProduct(token, productId)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(TAG, "response for Remove Product. for Body = $myResponse")
                    responseCallBack.onSuccessToRemoveProduct(response.body(), productId)

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun apiToApplyCoupon(token: String, requestCouponModel: CouponRequestModel) {
        val apiToApplyCouponRequest =
            GsonHelper.convertJavaObjectToJsonString(model = requestCouponModel)
        Log.d(
            TAG,
            "Request for coupon applied to Product. for Body = $apiToApplyCouponRequest"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.applyCoupon(token, requestCouponModel)

        call.enqueue(object : Callback<CouponResponseModel.CouponResponseFirstModel> {
            override fun onResponse(
                call: Call<CouponResponseModel.CouponResponseFirstModel>,
                response: Response<CouponResponseModel.CouponResponseFirstModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response for coupon applied to Product. for Body = $myResponse"
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
                call: Call<CouponResponseModel.CouponResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }

    fun apiToGetCouponsList(accessToken: String) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getCouponList(accessToken)

        call.enqueue(object : Callback<CouponResponseModel.CouponResponseSecondModel> {
            override fun onResponse(
                call: Call<CouponResponseModel.CouponResponseSecondModel>,
                response: Response<CouponResponseModel.CouponResponseSecondModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(TAG, "response apiToGetCouponsList = $myResponse")
                    responseCallBack.onSuccessCouponResponseList(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    responseCallBack.onFailureResponse(error)
                }
            }

            override fun onFailure(
                call: Call<CouponResponseModel.CouponResponseSecondModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onFailureResponse(error)
            }
        })
    }
}