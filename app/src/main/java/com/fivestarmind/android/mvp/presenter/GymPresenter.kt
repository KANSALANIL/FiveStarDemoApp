package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.ProductCategoryResponseModel
import com.fivestarmind.android.mvp.model.response.SeeAllResModel
import com.fivestarmind.android.mvp.model.response.TagAllCategoryResModel
import com.fivestarmind.android.mvp.model.response.TagResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class GymPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = GymPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel)


        fun onResFailure(errorMessages: String)

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

}