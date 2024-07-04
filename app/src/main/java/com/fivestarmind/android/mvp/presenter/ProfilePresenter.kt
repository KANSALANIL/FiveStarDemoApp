package com.fivestarmind.android.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.interfaces.ApiInterface
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.UserProfilleResponseModel
import com.fivestarmind.android.retrofit.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.Reader

class ProfilePresenter(private val responseCallBack: ResponseCallBack) {

    private val TAG = ProfilePresenter::class.java.simpleName
    private var error = ErrorResponse()

    interface ResponseCallBack {

        fun onDeleteAccountResponseSuccess(response: CommonResponse)

        fun onProfileResponseSuccess(response: UserProfilleResponseModel)

        fun onProfileUpdateResponse(response: CommonResponse)

        fun onResponseFailure(errorResponse: ErrorResponse)

        fun onResponseUpdateProfileFailure(string: String)

    }

    fun apiToGetProfile(token: String,appId: Int) {

        Log.d(TAG, "apiToGetProfile: body- $token")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.getUserProfile(token = token, appID = appId)
        Log.e(TAG, "Url: " + call.request().url().toString())

        call.enqueue(object : Callback<UserProfilleResponseModel> {
            override fun onResponse(
                call: Call<UserProfilleResponseModel>,
                response: Response<UserProfilleResponseModel>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(
                        TAG,
                        "response get profile Success = $myResponse"
                    )

                    responseCallBack.onProfileResponseSuccess(response.body())

                } else {
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    responseCallBack.onResponseFailure(error)
                    //responseCallBack.onResponseFailure("error")
                }
            }

            override fun onFailure(call: Call<UserProfilleResponseModel>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }

    fun apiHitToUpdateWithProfileImage(
        token: String,
        appId:Int,
        file: File?,
        userName: String,
        //  phone: String,
        //  status: String,
        //  clubCollege: String?,
        //  state: String?
    ) {
        /*     val userPhone = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         phone
     )

     val userStatus = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         status
     )

     val club_college = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         if (clubCollege.isNullOrEmpty()) "" else clubCollege
     )

     val userState = RequestBody.create(
         MediaType.parse("multipart/form-data"),
         if (state.isNullOrEmpty()) "" else state
         )
         */

        val reqFile: RequestBody = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            file
        )


        val fileToUpload = MultipartBody.Part.createFormData(
            "profile_img",
            file!!.name, reqFile
        )


        val name = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            userName
        )
        Log.d(TAG, "apiHitToUpdateWithProfileImage: body- File ---- ${file.name}")
        Log.d(TAG, "apiHitToUpdateWithProfileImage: body- fileToUpload ---- $fileToUpload")
        Log.d(TAG, "apiHitToUpdateWithProfileImage: body- token ---- $token")
        Log.d(TAG, "apiHitToUpdateWithProfileImage: body- name ---- $name")


        val apiService = ApiClient.client.create(ApiInterface::class.java)


        val call = apiService.updateUserProfile(
            token = token, appID = appId, avatar = fileToUpload, name = name
        )


        //  userPhone = userPhone, userStatus = userStatus,
        // clubCollege = club_college, state = userState
        Log.e(TAG, "Url: " + call.request().url().toString())


        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(
                        TAG,
                        "response update profile Success = $myResponse"
                    )

                    responseCallBack.onProfileUpdateResponse(response.body())

                } else {
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResponseUpdateProfileFailure("error")
                }
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseUpdateProfileFailure(t!!.message!!)
            }
        })
    }


    fun apiHitToUpdateWithOutProfileImage(
        token: String,
        appId:Int,
        userName: String,
    ) {

        val name = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            userName
        )

        Log.d(TAG, "apiHitToUpdateProfile: body- token ---- $token")
        Log.d(TAG, "apiHitToUpdateProfile: body- name ---- $name")


        val apiService = ApiClient.client.create(ApiInterface::class.java)


        val call = apiService.updateUserProfile(
            token = token, appID = appId,avatar = null, name = name
        )

        Log.e(TAG, "Url: " + call.request().url().toString())


        call.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {

                if (response.isSuccessful) {
                    val myResponse =
                        GsonHelper.convertJavaObjectToJsonString(model = response.body())

                    Log.d(
                        TAG,
                        "response update profile Success = $myResponse"
                    )

                    responseCallBack.onProfileUpdateResponse(response.body())

                } else {
                    /*val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                    //  responseCallBack.onResponseFailure(responseModel.errorBody())*/
                    responseCallBack.onResponseUpdateProfileFailure("error")
                }
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseUpdateProfileFailure("error")
            }
        })
    }

    /* fun apiHitToUpdateProfileWithoutImage(
         token: String,
         userName: String,
         phone: String,
         status: String,
         clubCollege: String?,
         state: String?
     ) {

         val name = RequestBody.create(
             MediaType.parse("multipart/form-data"),
             userName
         )

         val userPhone = RequestBody.create(
             MediaType.parse("multipart/form-data"),
             phone
         )

         val userStatus = RequestBody.create(
             MediaType.parse("multipart/form-data"),
             status
         )

         val club_college = RequestBody.create(
             MediaType.parse("multipart/form-data"),
             if (clubCollege.isNullOrEmpty()) "" else clubCollege
         )

         val userState = RequestBody.create(
             MediaType.parse("multipart/form-data"),
             if (state.isNullOrEmpty()) "" else state
         )

         Log.d(TAG, "apiHitToUpdateProfileWithoutImage: ")

         val apiService = ApiClient.client.create(ApiInterface::class.java)
         val call = apiService.updateUserProfileWithoutImage(
             token = token, name = name,
             userPhone = userPhone, userStatus = userStatus,
             clubCollege = club_college, state = userState
         )

         call.enqueue(object : Callback<UpdateProfileResponseModel> {
             override fun onResponse(
                 call: Call<UpdateProfileResponseModel>,
                 response: Response<UpdateProfileResponseModel>
             ) {

                 if (response.isSuccessful) {
                     val myResponse =
                         GsonHelper.convertJavaObjectToJsonString(model = response.body())

                     Log.d(
                         TAG,
                         "response update profile Success = $myResponse"
                     )

                     responseCallBack.onProfileUpdateResponse(response.body())

                 } else {
                     val reader: Reader = response.errorBody()!!.charStream()
                     error = Gson().fromJson(reader, ErrorResponse::class.java)
                     Log.d(TAG, "response for error Body = ${response.errorBody().charStream()}")
                     //  responseCallBack.onResponseFailure(responseModel.errorBody())
                     responseCallBack.onResponseFailure(error)
                 }
             }

             override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                 Log.d(TAG, "Entered in failure")
                 responseCallBack.onResponseFailure(error)
             }
         })
     }
 */
/*    fun hitApiForDeleteAccount(token: String) {
        Log.d(TAG, "hitApiForDeleteAccount : token - $token")

        val apiService = ApiClient.client.create(ApiInterface::class.java)
        val call = apiService.deleteAccount(token)

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
                    val reader: Reader = response.errorBody()!!.charStream()
                    error = Gson().fromJson(reader, ErrorResponse::class.java)
                    Log.d(TAG, "response for error Body = ${response.errorBody()}")
                    responseCallBack.onResponseFailure(error)
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                // Log error here since request failed
                Log.d(TAG, "Entered in failure")
                responseCallBack.onResponseFailure(error)
            }
        })
    }*/

}