package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.request.ProductRequestModel
import com.fivestarmind.android.mvp.model.request.TotalHourInAppRequestModel
import com.fivestarmind.android.mvp.model.response.AllProductsResponseModel
import com.fivestarmind.android.mvp.model.response.AppContentResponseModel
import com.fivestarmind.android.mvp.model.response.CartCountResponseModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.FeaturedProductResModel
import com.fivestarmind.android.mvp.model.response.NotificationDetailResponse
import com.fivestarmind.android.mvp.model.response.NotificationResponse
import com.fivestarmind.android.mvp.model.response.ProductCategoryResponseModel
import com.fivestarmind.android.mvp.model.response.SpecialContentResponse
import com.fivestarmind.android.mvp.model.response.TodayQuoteResponse
import com.fivestarmind.android.mvp.model.response.VideoViewResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class HomePresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = HomePresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var errorMessage = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onAppContentResponseSuccess(response: AppContentResponseModel)

        fun onLogoutResponseSuccess(response: CommonResponse)

        fun onResponseFailure(errorResponse: ErrorResponse)

        fun onResFailure(errorMessages: String)

        fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel)

        fun onFeaturedProductResponseSuccess(response: FeaturedProductResModel)

        fun onAllProductResponseSuccess(response: AllProductsResponseModel)

        fun onGetCartCount(cartCount: String)

        fun onLikeUnlikeVideoSuccess(response: CommonResponse)

        fun onMasteredVideoSuccess(response: CommonResponse)

        fun onNotificationResponseSuccess(response: NotificationResponse)

        fun onNotificationDetailResponseSuccess(response: NotificationDetailResponse)

        fun onVideoViewResponseSuccess(response: VideoViewResponseModel)

        fun onAudioViewResponseSuccess(response: VideoViewResponseModel)

        fun onTotalPerformanceAppResponseSuccess(response: VideoViewResponseModel)

        fun onTodayQuoteAppResponseSuccess(response: TodayQuoteResponse)

        fun onSpecialContentAppResponseSuccess(response: SpecialContentResponse)

        fun onDeleteAccountResponseSuccess(response: CommonResponse)

    }

    fun hitApiForLogout(token: String) {
        Log.d(TAG, "hitApiForLogout : token - $token")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.userLogout(token = "Bearer " + token)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response logout for Body = $myResponse"
                    )
                    responseCallBack.onLogoutResponseSuccess(response.body())

                } else if (response.code()==401) {
                    message = response.message()
                    Log.d(
                        TAG,
                        "response logout for Body = $message"
                    )
                    responseCallBack.onResFailure(message!!)

                } else {
                    Log.d(
                        TAG,
                        "response logout for Body>>>>  = ${response.code()}"
                    )
                    message = response.message()

                    responseCallBack.onResFailure(message!!)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)

            }
        })
    }

    fun apiToGetProductCategories(token:String,appId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getProductCategories(token=token,appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

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
                    message = responseModel.message()

                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<ProductCategoryResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }

    fun apiToGetFeaturedProduct(token:String, appId: Int ) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getFeaturedProduct(token=token,appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())


        call.enqueue(object : Callback<FeaturedProductResModel> {
            override fun onResponse(
                call: Call<FeaturedProductResModel>,
                responseModel: Response<FeaturedProductResModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "response success for Body = $myResponse"
                    )
                    responseCallBack.onFeaturedProductResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /*val reader: Reader = responseModel.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")*/
//                    responseCallBack.onResponseFailure(responseModel.errorBody())
                    responseCallBack.onResFailure(message!!)
                }
            }

            override fun onFailure(call: Call<FeaturedProductResModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResFailure(t.message!!)
            }
        })
    }

    fun apiToGetAllProducts(token: String, productRequestModel: ProductRequestModel) {
        val myRequest =
            GsonHelper.convertJavaObjectToJsonString(model = productRequestModel)

        Log.d(
            TAG,
            "getAllProducts Request for Body = $myRequest"
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

    fun apiToGetCartCount(token: String) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getCartCount(token)

        call.enqueue(object : Callback<CartCountResponseModel> {
            override fun onResponse(
                call: Call<CartCountResponseModel>,
                response: Response<CartCountResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(TAG, "response for Remove Product. for Body = $myResponse")
                    responseCallBack.onGetCartCount(response.body().total)

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    responseCallBack.onResponseFailure(error)
                }

            }

            override fun onFailure(call: Call<CartCountResponseModel>, t: Throwable) {
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


    /**
     * here call notification listing api
     */

    fun apiToGetNotificationData(token: String, appId: Int) {
        Log.d(TAG, "apiToGetNotificationData")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getNotificationListing(token = token, appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                responseModel: Response<NotificationResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetNotificationData response: = $myResponse"
                    )
                    responseCallBack.onNotificationResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }


    /**
     * here call notification listing api
     */

    fun apiToGetNotificationDetail(token: String, appId: Int, id: Int) {
        Log.d(TAG, "apiToGetNotificationDetail")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getNotificationDetail(token = token, appID = appId, id = id)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<NotificationDetailResponse> {
            override fun onResponse(
                call: Call<NotificationDetailResponse>,
                responseModel: Response<NotificationDetailResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetNotificationDetail response: = $myResponse"
                    )
                    responseCallBack.onNotificationDetailResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<NotificationDetailResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }


    /**
     * here call video view  api
     */

    fun apiToGetVideoView(token: String, appId: Int) {
        Log.d(TAG, "apiToGetVideoView")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getVideoView(token = token, appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<VideoViewResponseModel> {
            override fun onResponse(
                call: Call<VideoViewResponseModel>,
                responseModel: Response<VideoViewResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetVideoView response: = $myResponse"
                    )
                    responseCallBack.onVideoViewResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<VideoViewResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }

    /**
     * here call video view  api
     */

    fun apiToGetAudioView(token: String, appId: Int) {
        Log.d(TAG, "apiToGetAudioView")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getAudioView(token = token, appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<VideoViewResponseModel> {
            override fun onResponse(
                call: Call<VideoViewResponseModel>,
                responseModel: Response<VideoViewResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetAudioView response: = $myResponse"
                    )
                    responseCallBack.onAudioViewResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<VideoViewResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }


    fun apiToGetTotalhourInApp(
        token: String,
        totalHourInAppRequestModel: TotalHourInAppRequestModel,
        appId: Int
    ) {
        Log.d(TAG, "apiToGetTotalhourInApp")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getTotalHoursInApp(token, totalHourInAppRequestModel, appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<VideoViewResponseModel> {
            override fun onResponse(
                call: Call<VideoViewResponseModel>,
                responseModel: Response<VideoViewResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetTotalhourInApp response: = $myResponse"
                    )
                    responseCallBack.onTotalPerformanceAppResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<VideoViewResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)


            }
        })
    }

    fun  apiToGetQuoteListing(token: String, appId: Int, date: String) {
        Log.d(TAG, "apiToGetQuoteListing")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getQuoteListing(token,appId, date)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<TodayQuoteResponse> {
            override fun onResponse(
                call: Call<TodayQuoteResponse>,
                responseModel: Response<TodayQuoteResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetQuoteListing response: = $myResponse"
                    )
                    responseCallBack.onTodayQuoteAppResponseSuccess(responseModel.body())

                } else if (responseModel.code()==401) {
                    message = responseModel.message()
                    Log.d(
                        TAG,
                        "apiToGetQuoteListing response: = $message"
                    )
                    responseCallBack.onResFailure(message!!)



                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<TodayQuoteResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)

            }
        })
    }


    fun apiToGetSpecialContent(token: String, appId: Int) {
        Log.d(TAG, "apiToGetSpecialContent")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getSpecialContent(token,appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<SpecialContentResponse> {
            override fun onResponse(
                call: Call<SpecialContentResponse>,
                responseModel: Response<SpecialContentResponse>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "apiToGetSpecialContent response: = $myResponse"
                    )
                    responseCallBack.onSpecialContentAppResponseSuccess(responseModel.body())

                } else {
                    message = responseModel.message()
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResFailure(responseModel.message())
                }
            }

            override fun onFailure(call: Call<SpecialContentResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onResFailure(t.message!!)

            }
        })
    }

    fun hitApiForDeleteAccount(token: String, appId:String) {

        Log.d(TAG, "hitApiForDeleteAccount : token - $token")
        Log.d(TAG, "hitApiForDeleteAccount : appId - $appId")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.deleteAccount(token,appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response)

                    Log.d(
                        TAG,
                        "response delete account for Body = $myResponse"
                    )
                    responseCallBack.onDeleteAccountResponseSuccess(response.body())

                } else {

                    responseCallBack.onResFailure(response.message())
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResFailure(t.message!!)

            }
        })
    }

}