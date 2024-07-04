package com.fivestarmind.android.interfaces

import com.fivestarmind.android.mvp.model.request.*
import com.fivestarmind.android.mvp.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("app-content")
    fun getAppContent(): Call<AppContentResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("auth/registration")
    fun userSignUp(@Body requestSignUp: SignUpRequestModel): Call<SignInSignUpResponseModel.SignInSignUpResponseFirstModel>

    /*@Headers("Content-Type:application/json")
    @POST("auth/delete")
    fun deleteAccount(@Header("Authorization") token: String): Call<CommonResponse>
*/

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("auth/email")
    fun callForForgotPassword(@Body forgotPasswordRequestModel: ForgotPasswordRequestModel): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @PUT("auth/changePassword")
    fun callForChangePassword(
        @Header("Authorization") token: String, @Body requestChangePassword: RequestChangePassword
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("journal")
    fun getJournalListing(
        @Header("Authorization") token: String, @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<CommonListingResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("suggestions")
    fun submitSuggestions(
        @Header("Authorization") token: String,
        @Body suggestionsRequestModel: SuggestionsRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json")
    @Multipart
    @POST("profile")
    fun updateUserProfileWithoutImage(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("userPhone") userPhone: RequestBody,
        @Part("user_status") userStatus: RequestBody,
        @Part("club_college") clubCollege: RequestBody,
        @Part("state") state: RequestBody
    ): Call<UpdateProfileResponseModel>

    @Headers("Accept:application/json")
    @Multipart
    @POST("journal/create")
    fun createJournalWithImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("date") date: RequestBody,
        @Part("note") note: RequestBody
    ): Call<CommonResponse>

    @Headers("Accept:application/json")
    @Multipart
    @POST("journal/create")
    fun createJournalWithVideo(
        @Header("Authorization") token: String,
        @Part video: MultipartBody.Part,
        @Part cover_image: MultipartBody.Part,
        @Part("date") date: RequestBody,
        @Part("note") note: RequestBody
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("product/featured")
    fun getFeaturedProduct(@Header("Authorization") token: String): Call<FeaturedProductResponseModel>


    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("category/products")
    fun getAllProducts(
        @Header("Authorization") token: String, @Body productRequestModel: ProductRequestModel
    ): Call<AllProductsResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("getNewVideos")
    fun getNewVideos(@Header("Authorization") token: String): Call<ArrayList<NewVideosModel>>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("cart/count")
    fun getCartCount(@Header("Authorization") token: String): Call<CartCountResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("product/{productId}")
    fun productDetail(
        @Header("Authorization") token: String,
        @Path("productId") productId: String
    ): Call<ProgramDetailResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("cart/add")
    fun productAddToCart(
        @Header("Authorization") token: String,
        @Body addProductToCartRequestModel: AddProductToCartRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("user/settings")
    fun getSettings(@Header("Authorization") token: String): Call<SettingsResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("user/settings")
    fun updateSettings(
        @Header("Authorization") token: String,
        @Body updateSettingsRequestModel: UpdateSettingsRequestModel
    ): Call<SettingsResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("user/products")
    fun getProducts(
        @Header("Authorization") token: String, @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<MyProgramsResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("userPayment")
    fun userPayment(
        @Header("Authorization") token: String, @Body requestUserPayment: UserPaymentRequestModel
    ): Call<UserPaymentResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("cart")
    fun getCartProduct(@Header("Authorization") token: String): Call<CartResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("cart/remove/{productId}")
    fun removeProduct(
        @Header("Authorization") token: String, @Path("productId") productId: Int
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("coupon")
    fun applyCoupon(
        @Header("Authorization") token: String, @Body requestCouponModel: CouponRequestModel
    ): Call<CouponResponseModel.CouponResponseFirstModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("coupons")
    fun getCouponList(@Header("Authorization") token: String): Call<CouponResponseModel.CouponResponseSecondModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("favorite-unfavorite")
    fun likeUnlikeVideo(
        @Header("Authorization") token: String,
        @Body favoriteVideoRequestModel: FavoriteVideoRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("favorite/listing")
    fun getFavoriteVideos(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Call<FavoriteResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("transaction")
    fun getTransactionListing(
        @Header("Authorization") token: String, @Query("page") page: Int, @Query("limit") limit: Int
    ): Call<TransactionsResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("productVideo/{videoId}/mastered")
    fun masterVideo(
        @Header("Authorization") token: String,
        @Path("videoId") videoId: Int
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("getPhaseVideos/{phaseId}")
    fun getVideoListing(
        @Header("Authorization") token: String,
        @Path("phaseId") phaseId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<PhasesListingResponseModel>

    @Headers("Accept:application/json")
    @POST("productVideo/duration")
    fun saveVideoDuration(
        @Header("Authorization") token: String, @Body videosRequestModel: VideosRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("IAP-Packages/listing")
    fun getMembershipsListing(): Call<ArrayList<MembershipPackagesResponseModel>>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("user/membership")
    fun updateMembership(
        @Header("Authorization") token: String, @Body requestMembershipModel: RequestMembershipModel
    ): Call<MembershipResponseModel.MembershipResponseFirstModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("user/membership")
    fun getUserMembership(
        @Header("Authorization") token: String, @Query("current_time") currentTime: Long
    ): Call<MembershipResponseModel.MembershipResponseFirstModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @DELETE("user/membership")
    fun cancelUserMembership(
        @Header("Authorization") token: String,
    ): Call<MembershipResponseModel.MembershipResponseFirstModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("user/design-assessment")
    fun updateDesignAssessment(
        @Header("Authorization") token: String,
        @Body designAssessmentRequestModel: DesignAssessmentRequestModel
    ): Call<DesignAssessmentResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("user/develop-assessment")
    fun updateDevelopAssessment(
        @Header("Authorization") token: String,
        @Body designAssessmentRequestModel: DesignAssessmentRequestModel
    ): Call<DesignAssessmentResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("user/display-assessment")
    fun updateDisplayAssessment(
        @Header("Authorization") token: String,
        @Body designAssessmentRequestModel: DesignAssessmentRequestModel
    ): Call<DesignAssessmentResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("contact-us")
    fun submitContactUs(
        @Header("Authorization") token: String,
        @Body suggestionsRequestModel: SuggestionsRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("user/membership-coupon")
    fun applyMembershipCoupon(
        @Header("Authorization") token: String,
        @Query("coupon_code") couponCode: String,
        @Query("time_zone") timeZone: String
    ): Call<MembershipCouponResponseModel.MembershipCouponResponseFirstModel>

    // Api updated end url
    //103 live id 24

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("auth/login")
    fun userLogin(@Body requestLogin: SignInRequestModel): Call<UserLoginResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org-data/{appID}")
    fun getLogoImage(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
    ): Call<LetsGoResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/org-files?page=1&size=100")
    fun getFeaturedProduct(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int
    ): Call<FeaturedProductResModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/category?page=1&size=100")
    fun getProductCategories(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int
    ): Call<ProductCategoryResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/cat/{id}/cat-files")
    fun getMenuDetail(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("id") id: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<MenuDetailListResponse>

    @Headers("Content-Type:application/json", "Accept:application/json")
    @GET("auth/logout")
    fun userLogout(@Header("Authorization") token: String): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("org/{appID}/favourite")
    fun addFavourite(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Body addFavouriteRequest: AddFavouriteRequestModel
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/file/{id}/detail")
    fun getFavourite(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("id") audioId: Int
    ): Call<GetProductFileResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/prod/{id}/prod-files")
    fun getAllCategoryDataList(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("id") productId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<SeeAllResModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    //  @GET("org/{appID}/favourite")
    @GET("org/{appID}/favourite/category/{categoryId}/files")
    fun getBookMarkListing(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BookMarkResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/user")
    fun getUserProfile(
        @Header("Authorization") token: String, @Path("appID") appID: Int
    ): Call<UserProfilleResponseModel>

    @Headers("Accept:application/json")
    @Multipart
    @POST("org/{appID}/user")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Part avatar: MultipartBody.Part?,
        @Part("name") name: RequestBody
        //   @Part("userPhone") userPhone: RequestBody,
        //    @Part("user_status") userStatus: RequestBody,
        //  @Part("club_college") clubCollege: RequestBody,
        //   @Part("state") state: RequestBody
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("org/{appID}/prod-video/{videoId}/view")
    fun videoViews(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("videoId") videoId: Int,
        @Body videoDurationRequestModel: VideoDurationRequestModel
    ): Call<CommonResponse>

    // call product detail api
    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/file/{id}/detail")
    fun getProductDetail(
        @Header("Authorization") token: String, @Path("appID") appID: Int, @Path("id") imageId: Int
    ): Call<ProductImageDetailResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/notification")
    fun getNotificationListing(
        @Header("Authorization") token: String, @Path("appID") appID: Int
    ): Call<NotificationResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/notification/{id}")
    fun getNotificationDetail(
        @Header("Authorization") token: String, @Path("appID") appID: Int, @Path("id") id: Int
    ): Call<NotificationDetailResponse>


    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/performance/video-view")
    fun getVideoView(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int
    ): Call<VideoViewResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/performance/audio-view")
    fun getAudioView(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int
    ): Call<VideoViewResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("org/{appId}/performance/total-min")
    fun getTotalHoursInApp(
        @Header("Authorization") token: String,
        @Body totalHourInAppRequestModel: TotalHourInAppRequestModel,
        @Path("appId") appId: Int
    ): Call<VideoViewResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("org/{appId}/app-time")
    fun sendAppTime(
        @Header("Authorization") token: String,
        @Body appTimeRequestModel: AppTimeRequestModel,
        @Path("appId") appId: Int
    ): Call<CommonResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appId}/quote")
    fun getQuoteListing(
        @Header("Authorization") token: String,
        @Path("appId") appId: Int,
        @Query("date") date: String,
    ): Call<TodayQuoteResponse>


    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appId}/special-content")
    fun getSpecialContent(
        @Header("Authorization") token: String,
        @Path("appId") appId: Int
    ): Call<SpecialContentResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appId}/favourite/cat-files")
    fun getBookmarkAllCategory(
        @Header("Authorization") token: String,
        @Path("appId") appId: Int
    ): Call<BookmarkAllCategoryResModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/tag")
    fun getTagListing(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
    ): Call<TagResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/tag/{tagID}/files-count")
    fun getTagAllCategoryListing(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("tagID") tagId: Int,
    ): Call<TagAllCategoryResModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/tag/{tagID}/cat/{categoryId}/files")
    fun getParticularCategoryListing(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Path("tagID") tagId: Int,
        @Path("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<SeeAllResModel>

    //contact center api
    @Headers("Accept:application/json", "Content-Type:application/json")
    @GET("org/{appID}/contact-center/user-roles")
    fun getUserRole(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int
    ): Call<UserRoleResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    //org/57/contact-center/users
    @GET("org/{appID}/contact-center/users")
    fun getUsersList(
        @Header("Authorization") token: String,
        @Path("appID") appID: Int,
        @Query("filter") filter: String,
        @Query("search") search: String
    ): Call<UsersListingResponseModel>

    @Headers("Accept:application/json", "Content-Type:application/json")
    //org/57/contact-center/users
    @GET("threads")
    fun getThreadsList(
        @Header("Authorization") token: String,
        /*  @Query("filter") filter: String,
          @Query("search") search: String*/
    ): Call<MessagesListingResponseModel>


    //Chat Api

    //org/57/contact-center/users
    @GET("threads/user")
    fun getChatUserDetail(
        @Header("Authorization") token: String,
        @Query("id") userId: Int,
    ): Call<ThreadIdResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
   // @GET("threads/show")
    @GET("threads/messages")
    fun getChatByThreadID(
        @Header("Authorization") token: String,
        @Query("id") threadId: String,
       @Query("oldest") messageId: String
    ): Call<ThreadIdResponse>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST("threads/messages")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Body sendChatDataModel: SendChatDataModel
    ): Call<String>

    @Headers("Accept:application/json", "Content-Type:application/json")
    @DELETE("org/{id}/user")
    fun deleteAccount(
        @Header("Authorization") token: String,
        @Path("id") AppID: String): Call<CommonResponse>
}
