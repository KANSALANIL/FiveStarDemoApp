package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.DateTimeHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.MyFavoriteAdapter
import com.fivestarmind.android.mvp.dialog.DialogAddProductToCartOrUpgradeMembership
import com.fivestarmind.android.mvp.dialog.DialogMastered
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.request.RequestMasterVideo
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.MyFavoritePresenter
import kotlinx.android.synthetic.main.activity_my_favorite.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class MyFavoriteActivity : BaseActivity(), MyFavoritePresenter.ResponseCallBack,
    RecyclerViewItemListener {

    override val TAG = MyFavoriteActivity::class.java.simpleName
    private var linearLayoutManager: LinearLayoutManager? = null

    private var presenter: MyFavoritePresenter? = null
    private val favoriteList = arrayListOf<FavoriteResponseDataModel>()
    private var favoriteModel = FavoriteResponseDataModel()

    private var loadMore = false
    private var currentPage = 0

    private var myFavoriteAdapter:MyFavoriteAdapter? = null

    private var favVideoPosition: Int? = null
    private var masteredVideoId: Int? = null
    private var masterAfterVideoCompletion: Boolean? = null

    private var addProductToCartRequestModel = AddProductToCartRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_favorite)

        setToolbarTitle()
        initRecyclerView()

        initPresenter()
        init()

        setClickListener()
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.book_mark)
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {

      //  myFavoriteAdapter = MyFavoriteAdapter(this, this)
        linearLayoutManager = LinearLayoutManager(this)
        rvFavorite.layoutManager = linearLayoutManager

        rvFavorite.apply {
            layoutManager = linearLayoutManager
            adapter = myFavoriteAdapter
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = MyFavoritePresenter(this, this)
    }

    private fun init() {
        callToHitApiForGetFavoriteVideosProcess()
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        if (shouldProceedClick())
            ivBackMenu.setOnClickListener { onBackPressed() }
    }

    /**
     * Here connecting to server and uses UserProducts API
     */
    private fun callToHitApiForGetFavoriteVideosProcess() {
        /*if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter!!.hitApiToGetMyFavoriteVideos(
                SharedPreferencesHelper.getAuthToken(),
                ++currentPage
            )

            addOnScrollListener()
        }*/
    }

    /**
     * It is called when user click on like/unlike view
     *
     * @param favoriteResponseDataModel
     * @param position
     */
    private fun clickedLikeUnlike(
        favoriteResponseDataModel: FavoriteResponseDataModel,
        position: Int
    ) {
        favVideoPosition = position

        val favoriteVideoRequestModel = FavoriteVideoRequestModel(
            productVideoId = favoriteResponseDataModel.id,
            action = Constants.App.Number.ZERO
        )

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter!!.hitApiToLikeUnlikeVideo(
                token = SharedPreferencesHelper.getAuthToken(),
                favoriteVideoRequestModel = favoriteVideoRequestModel
            )
        }
    }

    /**
     * It is called when user click on the mastered icon view.
     *
     * @param video_id
     * @param position
     */
    private fun callToOpenMasteredDialog(
        video_id: String,
        position: Int
    ) {
        masteredVideoId = video_id.toInt()

        val requestMasterVideo = RequestMasterVideo().apply {
            videoId = video_id
        }

        DialogMastered(
            this@MyFavoriteActivity,
            requestMasterVideo = requestMasterVideo,
            context = this@MyFavoriteActivity
        ).show()
    }

    /**
     * It is called when user click on the mastered text.
     *
     * @param requestMasterVideo
     */
    private fun clickedMastered(videoId: Int?) {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            if (videoId != null)
                presenter!!.hitApiToMasterVideo(
                    token = SharedPreferencesHelper.getAuthToken(),
                    videoId = videoId.toInt()
                )
        }
    }

    /**
     * It is called when user click on the cancel text.
     */
    private fun clickedCancel(requestMasterVideo: RequestMasterVideo?) {
        if (null != requestMasterVideo) {
            currentPage = 0
            favoriteList?.clear()

            callToHitApiForGetFavoriteVideosProcess()
        }
    }

    private fun addOnScrollListener() {
        rvFavorite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val total = linearLayoutManager!!.itemCount
                    val lastVisibleItemCount =
                        linearLayoutManager!!.findLastVisibleItemPosition()
                    if (loadMore) {
                        if (total > 0)
                            if (total - 1 == lastVisibleItemCount) {
                                loadMore = false
                                presenter!!.hitApiToGetMyFavoriteVideos(
                                    SharedPreferencesHelper.getAuthToken(),
                                    ++currentPage
                                )
                                pbLoadMore.visibility = View.VISIBLE
                            }
                    } else {
                        pbLoadMore.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun removeDataFromList() {
        favoriteList.removeAt(favVideoPosition!!)
        myFavoriteAdapter!!.notifyItemRemoved(favVideoPosition!!)
        myFavoriteAdapter!!.updateResponseList(favoriteList)

        if (favoriteList.size == 0) tvNoDataFound.visibility = View.VISIBLE
    }

    /**
     * Here is updating the purchase type
     */
    private fun getPurchaseType(favoriteResponseDataModel: FavoriteResponseDataModel) {
        favoriteModel = favoriteResponseDataModel

        val model  = SharedPreferencesHelper.getUserInfoModel()
      //  model!!.proAccess = favoriteModel.pro_access

     //   SharedPreferencesHelper.storeUserInfoModel(model)

        when {

            favoriteResponseDataModel.msg == "" && favoriteResponseDataModel.productInfo.purchaseType!=3->
                showDialogToBuyMembership()

            favoriteResponseDataModel.msg == "Expired Membership." && favoriteResponseDataModel.membership.amountPaid < 1 ->
                showDialogToBuyMembership()

            favoriteResponseDataModel.msg == "Expired Membership." && favoriteResponseDataModel.membership.amountPaid > 0 ->
                showDialogToBuyMembership()

           /* SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1 ->
                clickedToPlayVideos(
                    favoriteResponseDataModel = favoriteResponseDataModel
                )*/

            favoriteResponseDataModel.alreadyBuy == true -> clickedToPlayVideos(
                favoriteResponseDataModel = favoriteResponseDataModel
            )

            favoriteResponseDataModel.productInfo.purchaseType == Constants.App.Number.ONE -> {
                checkConditionForPurchaseTypeOne(favoriteResponseDataModel = favoriteResponseDataModel)
            }

            favoriteResponseDataModel.productInfo.purchaseType == Constants.App.Number.TWO -> {
                checkConditionForPurchaseTypeTwo(favoriteResponseDataModel = favoriteResponseDataModel)
            }

            favoriteResponseDataModel.productInfo.purchaseType == Constants.App.Number.THREE -> {
                checkConditionForPurchaseTypeThree(favoriteResponseDataModel = favoriteResponseDataModel)
            }
        }
    }

    private fun checkConditionForPurchaseTypeOne(favoriteResponseDataModel: FavoriteResponseDataModel) {
        when {
            favoriteResponseDataModel.existInCart == true ->
                DialogAddProductToCartOrUpgradeMembership(
                    this@MyFavoriteActivity,
                    addToCart = getString(R.string.view_cart),
                    context = this@MyFavoriteActivity
                ).show()

            else -> DialogAddProductToCartOrUpgradeMembership(
                this@MyFavoriteActivity,
                addToCart = getString(R.string.add_to_cart),
                context = this@MyFavoriteActivity
            ).show()
        }
    }

    private fun checkConditionForPurchaseTypeTwo(favoriteResponseDataModel: FavoriteResponseDataModel) {
        if (favoriteResponseDataModel.isSummerProgramSubscribed == 1) {
            checkConditionForPurchaseTypeThree(favoriteResponseDataModel = favoriteModel)
        } else {
            if (isInternetConnected(shouldShowToast = true)) {
                showProgressDialog("")
                presenter?.getUserMembership(AppHelper.getCurrentTimestampInSeconds())
            }
        }
    }

    private fun showDialogToBuyMembership() {
        DialogAddProductToCartOrUpgradeMembership(
            this@MyFavoriteActivity,
            addToCart = getString(R.string.upgrade),
            context = this@MyFavoriteActivity
        ).show()
    }

    private fun checkConditionForPurchaseTypeThree(favoriteResponseDataModel: FavoriteResponseDataModel) {
        clickedToPlayVideos(favoriteResponseDataModel = favoriteResponseDataModel)
    }

    /**
     * It is called to show pdf view
     */
    private fun showPdfView(pdfUrl: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
        startActivity(browserIntent)
    }

    /**
     * It is called when user clicked on phase videos
     */
    private fun clickedToPlayVideos(favoriteResponseDataModel: FavoriteResponseDataModel) {
        val getLink = getVideoUrl(favoriteResponseDataModel)
        Log.d(TAG, "Video Link  => $getLink")

        if (getLink == null)
            showToast(message = "No video found")
        else {
           /* val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.App.VIDEO_LINK, getLink)
            intent.putExtra(Constants.App.VIDEO_ID, favoriteResponseDataModel.id)
            intent.putExtra(Constants.App.IS_MASTERED, favoriteResponseDataModel.isMasteredByMe)
            startActivityForResult(
                intent
                    .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                Constants.App.RequestCode.VIDEO_PLAYER
            )*/
        }
    }

    private fun getVideoUrl(model: FavoriteResponseDataModel): String? {
        Log.d(TAG, "Video response  => ${Gson().toJson(model)}")

        if (model.preset720p == null && model.preset1080p == null) {
            when {
                model.preset480p != null -> return model.preset480p
                model.preset360p != null -> return model.preset360p
                model.preset240p != null -> return model.preset240p
                model.iphone4s != null -> return model.iphone4s
            }
        } else {
            if (model.preset720p != null)
                return model.preset720p

            if (model.preset1080p != null)
                return model.preset1080p
        }

        return null
    }

    /**
     * It is called when user clicked add to cart
     */
    private fun clickedAddToCart() {
        apiHitAddProductToCart()
    }

    /**
     * Call for api add program into card
     */
    private fun apiHitAddProductToCart() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter?.apiPostToAddProduct(
                SharedPreferencesHelper.getAuthToken(),
                addProductToCartRequestModel = addProductToCartRequestModel.apply {
                    product_id = favoriteModel.productInfo.id.toString()
                })
        }
    }

    /**
     * It is called when user clicked on view cart
     */
    private fun clickedViewCart() {
        startActivityForResult(
            Intent(this@MyFavoriteActivity, CartActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.MY_FAVORITE_SCREEN
        )
    }

    /**
     * It is called when user clicked upgrade membership
     */
    private fun clickedUpgradeMembership() {
        startActivityForResult(
            Intent(this@MyFavoriteActivity, MembershipsActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.MY_FAVORITE_SCREEN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.VIDEO_PLAYER -> {
//                        val isMastered: Int? =
//                                data?.getSerializableExtra(Constants.App.IS_MASTERED) as Int?
//
//                        // check is video not already mastered
//                        if (isMastered == Constants.VALUE.FALSE) {
//                            masterAfterVideoCompletion = true
//
//                            val videoId: Int? =
//                                    data?.getSerializableExtra(Constants.App.VIDEO_ID) as Int?
//
////                            callToOpenMasteredDialog(video_id = videoId.toString(), position = 0)
//                        } else {
                        currentPage = 0
                        favoriteList.clear()

                        callToHitApiForGetFavoriteVideosProcess()
//                        }
                    }

                    Constants.App.RequestCode.MY_FAVORITE_SCREEN -> {
                        currentPage = 0
                        favoriteList.clear()

                        callToHitApiForGetFavoriteVideosProcess()
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                when (requestCode) {
                    Constants.App.RequestCode.VIDEO_PLAYER -> {
                        currentPage = 0
                        favoriteList.clear()

                        callToHitApiForGetFavoriteVideosProcess()
                    }

                    else -> {
                        currentPage = 0
                        favoriteList.clear()

                        callToHitApiForGetFavoriteVideosProcess()
                    }
                }
            }
        }
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.LIKE_UNLIKE ->
                clickedLikeUnlike(
                    favoriteResponseDataModel = model as FavoriteResponseDataModel,
                    position = position
                )

            ItemClickType.MASTER -> {
                val favModel: FavoriteResponseDataModel = model as FavoriteResponseDataModel

                clickedMastered(videoId = favModel.id)
            }

            //ItemClickType.PLAY_VIDEO ->
              //  getPurchaseType(favoriteResponseDataModel = model as FavoriteResponseDataModel)
         //   startActivity(Intent(this@MyFavoriteActivity,VideoPlayerActivity::class.java))

            else ->
                Log.e(TAG, "onRecyclerViewItemClick: itemClickType- INVALID")
        }
    }

    override fun onDialogEventListener(
        dialogEventType: DialogEventType,
        requestCode: Int,
        model: Any?
    ) {
        when (dialogEventType) {
//            DialogEventType.MASTERED -> clickedMastered(
//                requestMasterVideo = model as RequestMasterVideo
//            )

            DialogEventType.CANCEL -> clickedCancel(
                requestMasterVideo = model as RequestMasterVideo?
            )

            DialogEventType.ADD_TO_CART -> clickedAddToCart()

            DialogEventType.VIEW_CART -> clickedViewCart()

            DialogEventType.UPGRADE_MEMBERSHIP -> clickedUpgradeMembership()

            else -> super.onDialogEventListener(
                dialogEventType = dialogEventType,
                requestCode = requestCode,
                model = model
            )
        }
    }

    /**
     * When successful response or data retrieved from Api get user membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                membershipResponseModel.data.apply {
                    if (this == null) {
                        showDialogToBuyMembership()
                    } else if (isCanceled == 1) {
                        if (DateTimeHelper.isFutureTimeStampForSeconds(endDate)) {
                            clickedToPlayVideos(favoriteResponseDataModel = favoriteModel)
                        } else {
                            showDialogToBuyMembership()
                        }
                    } else {
                        clickedToPlayVideos(favoriteResponseDataModel = favoriteModel)
                    }
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from master video api
     *
     * @param response success response
     */
    override fun onMasteredVideoSuccess(response: CommonResponse) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {
                    hideProgressDialog()

                    showToast(response.message)

                    currentPage = 0
                    favoriteList.clear()

                    callToHitApiForGetFavoriteVideosProcess()
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from like/unlike video api
     *
     * @param response success response
     */
    override fun onLikeUnlikeVideoSuccess(response: CommonResponse) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {
                    hideProgressDialog()

                    removeDataFromList()
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from favorite listing api
     *
     * @param response success response
     */
    override fun onFavoriteListingResponseSuccess(response: FavoriteResponseModel) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {

                    hideProgressDialog()
                    currentPage = response.currentPage!!

                    if (response.nextPageUrl != null)
                        loadMore = true

                    if (response.data!!.size > 0) {
                        favoriteList.addAll(response.data)
                    } else
                        tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * When success response from api add program into card
     *
     * @param response Success Response
     */
    override fun onResponseSuccessProductAdded(response: CommonResponse) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {
                    hideProgressDialog()

                    currentPage = 0
                    callToHitApiForGetFavoriteVideosProcess()

                    showToast(response.message)
                }
            }
        }
    }

    /**
     * When error occurred in api response
     *
     * @param errorResponse Error Response
     */
    override fun onResponseFailure(errorResponse: String) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(errorResponse)
                }
            }
        }
    }

    /**
     * When error occurred in response of api get user memberships
     *
     * @param errorResponse for Error Message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@MyFavoriteActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyFavoriteActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
