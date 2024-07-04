package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.ProductRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class ProgramsPresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = ProgramsPresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel)

        fun onAllProductResponseSuccess(response: AllProductsResponseModel)

        fun onNewVideosResponseSuccess(response: ArrayList<NewVideosModel>)

        fun onResponseSuccessProductAdded(response: CommonResponse)

        fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel)

        fun onResponseFailure(errorResponse: ErrorResponse)
    }

    fun apiToGetProductCategories(token:String,appId:Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getProductCategories(token=token,appID = appId)

        call.enqueue(object : Callback<ProductCategoryResponseModel> {
            override fun onResponse(
                call: Call<ProductCategoryResponseModel>,
                responseModel: Response<ProductCategoryResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "response success for apiToGetProductCategories = $myResponse"
                    )
                    responseCallBack.onProductCategoriesResponseSuccess(responseModel.body())

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
//                    responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<ProductCategoryResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun apiToGetNewVideos(token: String) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiService.getNewVideos(token = token)

        call.enqueue(object : Callback<ArrayList<NewVideosModel>> {
            override fun onResponse(
                call: Call<ArrayList<NewVideosModel>>,
                responseModel: Response<ArrayList<NewVideosModel>>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "getNewVideos response for Body = $myResponse"
                    )
                    responseCallBack.onNewVideosResponseSuccess(responseModel.body())

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "getNewVideos response for error Body = ${responseModel.errorBody()}"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(
                call: Call<ArrayList<NewVideosModel>>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d(TAG, "getNewVideos Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun apiToGetAllProducts(token: String, productRequestModel: ProductRequestModel) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = productRequestModel)

        Log.d(
            TAG,
            "apiToGetAllProducts request for Body = $myRequest"
        )

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiService.getAllProducts(token = token, productRequestModel = productRequestModel)

        call.enqueue(object : Callback<AllProductsResponseModel> {
            override fun onResponse(
                call: Call<AllProductsResponseModel>,
                responseModel: Response<AllProductsResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "getAllProducts response for Body = $myResponse"
                    )
                    responseCallBack.onAllProductResponseSuccess(responseModel.body())

                } else {
                    val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(
                        TAG,
                        "getAllProducts response for error Body = ${responseModel.errorBody()}"
                    )
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<AllProductsResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "getAllProducts Entered in failure")
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