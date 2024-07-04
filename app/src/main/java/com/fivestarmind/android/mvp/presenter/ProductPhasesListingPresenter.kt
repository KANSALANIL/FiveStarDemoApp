package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.MembershipResponseModel
import com.fivestarmind.android.mvp.model.response.PhasesListingResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class ProductPhasesListingPresenter(
    private val responseCallBack: ResponseCallBack
) {

    private val TAG = ProductPhasesListingPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onVideoListingSuccess(response: PhasesListingResponseModel)

        fun onResponseSuccessProductAdded(response: CommonResponse)

        fun onMasteredVideoSuccess(response: CommonResponse)

        fun onLikeUnlikeVideoSuccess(response: CommonResponse)

        fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun getVideoList(token: String, phaseId: Int, currentPage: Int, limit: Int) {

        Log.d(TAG, "getVideoList : token - $token , currentPage - $phaseId")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getVideoListing(token, phaseId, currentPage, limit)

        call.enqueue(object : Callback<PhasesListingResponseModel> {
            override fun onResponse(
                call: Call<PhasesListingResponseModel>,
                response: Response<PhasesListingResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response getVideoList = $myResponse"
                    )
                    responseCallBack.onVideoListingSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<PhasesListingResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun hitApiToLikeUnlikeVideo(
        token: String,
        favoriteVideoRequestModel: FavoriteVideoRequestModel
    ) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = favoriteVideoRequestModel)
        Log.d(
            TAG,
            "like Unlike video Request for Body = $myRequest"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.likeUnlikeVideo(
            token = token,
            favoriteVideoRequestModel = favoriteVideoRequestModel
        )

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                responseModel: Response<CommonResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "like Unlike video response for Body = $myResponse"
                    )
                    responseCallBack.onLikeUnlikeVideoSuccess(responseModel.body())

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(
                        TAG,
                        "like Unlike video response for error Body = ${responseModel.errorBody()}"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "like Unlike video Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun hitApiToMasterVideo(token: String, videoId: Int) {
        Log.d(
            TAG,
            "hitApiToMasterVideo Request for Body = $videoId"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.masterVideo(
            token = token,
            videoId = videoId
        )

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                responseModel: Response<CommonResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "hitApiToMasterVideo response for Body = $myResponse"
                    )
                    responseCallBack.onMasteredVideoSuccess(responseModel.body())

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)

                    Log.d(
                        TAG,
                        "hitApiToMasterVideo response for error Body = ${responseModel.errorBody()}"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "hitApiToMasterVideo Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun apiPostToAddProduct(
        token: String,
        addProductToCartRequestModel: AddProductToCartRequestModel
    ) {
        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.productAddToCart(token, addProductToCartRequestModel)

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(TAG, "response for add to Cart for Body = $myResponse")
                    responseCallBack.onResponseSuccessProductAdded(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
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
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(
                call: Call<MembershipResponseModel.MembershipResponseFirstModel>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }
}

