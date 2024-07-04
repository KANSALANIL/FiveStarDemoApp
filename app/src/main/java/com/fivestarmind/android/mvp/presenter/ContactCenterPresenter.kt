package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.ProductCategoryResponseModel
import com.fivestarmind.android.mvp.model.response.SeeAllResModel
import com.fivestarmind.android.mvp.model.response.TagAllCategoryResModel
import com.fivestarmind.android.mvp.model.response.TagResponseModel
import com.fivestarmind.android.mvp.model.response.UserRoleResponseModel
import com.fivestarmind.android.mvp.model.response.UsersListingResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Reader

class ContactCenterPresenter(private var responseCallBack: ResponseCallBack) {

    private var TAG = ContactCenterPresenter::class.java.simpleName
    private var error = ErrorResponse()
    private var message: String? = null

    interface ResponseCallBack {

        fun onUserRoleResponseSuccess(response: UserRoleResponseModel)
        fun onUsersListingResponseSuccess(response: UsersListingResponseModel)
        fun onFailureResponse(message: String)
        fun onUsersListingFailureResponse(message: String)
    }

    /**
     * here call UserRoleApi
     */
    fun hitGetUserRoleApi(token:String,appId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getUserRole(token = token,appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<UserRoleResponseModel> {
            override fun onResponse(
                call: Call<UserRoleResponseModel>,
                responseModel: Response<UserRoleResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "response success for apiToGetProductCategories = $myResponse"
                    )
                    responseCallBack.onUserRoleResponseSuccess(responseModel.body())

                } else {
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<UserRoleResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onFailureResponse(t.message!!)


            }
        })
    }

    fun hitGetUsersListingApi(token:String,appId: Int,filter: String,search: String) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getUsersList(token = token,appID = appId,filter = filter,search = search)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<UsersListingResponseModel> {
            override fun onResponse(
                call: Call<UsersListingResponseModel>,
                responseModel: Response<UsersListingResponseModel>
            ) {

                if (responseModel.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = responseModel)

                    Log.d(
                        TAG,
                        "response success for GetUsersListingApi = $myResponse"
                    )
                    responseCallBack.onUsersListingResponseSuccess(responseModel.body())

                } else {
                    /* val reader: Reader = responseModel.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${responseModel.errorBody()}")
 //                    responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onUsersListingFailureResponse(responseModel.message())
                }
            }

            override fun onFailure(call: Call<UsersListingResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                //  responseCallBack.onResponseFailure(error)
                responseCallBack.onUsersListingFailureResponse(t.message!!)


            }
        })
    }
}