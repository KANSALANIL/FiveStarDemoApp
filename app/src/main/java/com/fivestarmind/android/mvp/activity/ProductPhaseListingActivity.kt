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
import com.google.gson.reflect.TypeToken
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.DateTimeHelper
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.ProductPhasesAdapter
import com.fivestarmind.android.mvp.dialog.DialogAddProductToCartOrUpgradeMembership
import com.fivestarmind.android.mvp.dialog.DialogMastered
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.request.RequestMasterVideo
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.ProductPhasesListingPresenter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_phase_listing.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar_title_with_video_pdf.tvToolbarTitle
import kotlinx.android.synthetic.main.layout_toolbar_title_with_video_pdf.tvToolbarVideoPdf


class ProductPhaseListingActivity : BaseActivity(), ProductPhasesListingPresenter.ResponseCallBack,
    RecyclerViewItemListener {

    override val TAG = ProductPhaseListingActivity::class.java.simpleName
    private var linearLayoutManager: LinearLayoutManager? = null

    private var videoList: ArrayList<FavoriteResponseDataModel>? = null
    private var productPhasesAdapter: ProductPhasesAdapter? = null

    private var phaseModel: PhasesModel? = null
    private var phaseVideoModel: PhaseVideoModel? = null
    lateinit var presenter: ProductPhasesListingPresenter

    private var favVideoPosition: Int? = null
    private var masteredVideoId: Int? = null
    private var masterAfterVideoCompletion: Boolean? = null

    private var loadMore = false
    private var currentPage = 0

    private var favoriteModel = FavoriteResponseDataModel()
    private var addProductToCartRequestModel = AddProductToCartRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_phase_listing)

        initData()
        getDataFromIntent()

        initRecyclerView()
        setToolbarTitle()

        initPresenter()
        init()
        setClickListener()
    }

    private fun initData() {
        videoList = arrayListOf()
    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.PHASE_MODEL)) {
            phaseModel = Gson().fromJson(intent.getStringExtra(Constants.App.PHASE_MODEL), PhasesModel::class.java)
            phaseVideoModel = Gson().fromJson(intent.getStringExtra(Constants.App.PHASE_MODEL), PhaseVideoModel::class.java)
        }
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        phaseVideoModel?.apply {
            tvToolbarTitle.text = title + " " + subtitle
            tvHeading.text = title + " " + subtitle

            var subData = getString(
                R.string.format_video_pdf, AppHelper.formatVideoCount(
                    context = this@ProductPhaseListingActivity,
                    video = videoCount
                ), AppHelper.formatPdfCount(
                    context = this@ProductPhaseListingActivity,
                    pdfCount = pdfCount
                )
            )

            tvToolbarVideoPdf.text = subData
            tvSubHeading.text = subData

            updateHeaderImage()

        }
    }

        private fun updateHeaderImage() {
            phaseVideoModel?.apply {
                if (thumbpath.isNullOrEmpty()) {
                    if (thumbnail.isEmpty()) {
                        if (videoListing.isNotEmpty()) {
                            if (videoCount != 0) {
                                if (videoListing[Constants.App.Number.ZERO].thumbpath.isNullOrEmpty()) updateProductImage(
                                    thumbnail = videoListing[Constants.App.Number.ZERO].thumbnail
                                )
                                else updateProductImage(thumbnail = videoListing[Constants.App.Number.ZERO].thumbpath)
                            } else {
                                if (videoListing[Constants.App.Number.ZERO].thumbpath.isNullOrEmpty())
                                    ivPhase.setImageResource(R.drawable.ic_pdf)
                                else updateProductImage(thumbnail = videoListing[Constants.App.Number.ZERO].thumbpath)
                            }
                        } else updateProductImage(thumbnail = "")
                    } else {
                        updateProductImage(thumbnail = thumbnail)
                    }
                } else {
                    updateProductImage(thumbnail = thumbpath)
                }
            }
        }

    /**
 * Here is updating the product image
 *
 * @param thumbnail
 */
private fun updateProductImage(thumbnail: String?) {
        if (thumbnail!!.isNotEmpty())
            Picasso.get()
                .load(thumbnail)
                .into(ivPhase, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                        ivPhase.setImageDrawable(resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                    }
                })
        else ivPhase.setImageDrawable(resources.getDrawable(R.drawable.ic_placeholder_corner_8))
}

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        productPhasesAdapter = ProductPhasesAdapter(
            videoList = videoList!!,
            activity = this@ProductPhaseListingActivity,
            listener = this@ProductPhaseListingActivity
        )

        linearLayoutManager =
            LinearLayoutManager(this@ProductPhaseListingActivity, RecyclerView.VERTICAL, false)

        rvProductPhases.apply {
            layoutManager = this@ProductPhaseListingActivity.linearLayoutManager
            adapter = productPhasesAdapter
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ProductPhasesListingPresenter(this)
    }

    private fun init() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.getVideoList(
                SharedPreferencesHelper.getAuthToken(),
                phaseModel?.id!!.toInt(),
                ++currentPage,
                limit = Constants.App.Number.FIFTEEN
            )

            addOnScrollListener()
        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        if (shouldProceedClick())
            ivBackMenu.setOnClickListener { onBackPressed() }
    }

    /**
     * Check for user logged in or not
     *
     * @param favoriteResponseDataModel
     * @param position
     * @param isCallForFav
     */
    private fun checkForLoginStatus(
        favoriteResponseDataModel: FavoriteResponseDataModel,
        position: Int,
        isCallForFav: Boolean?
    ) {
        when (AppHelper.isUserLoggedIn()) {
            true -> {
                when (isCallForFav) {
                    true -> clickedLikeUnlike(
                        favoriteResponseDataModel = favoriteResponseDataModel,
                        position = position
                    )
                    false -> clickedMastered(
                        videoId = favoriteResponseDataModel.id,
                    )
                    else -> {

                    }
                }
            }

            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.PHASE_VIDEO_LISTING
                )
            }
        }
    }

    /**
     * It is called when user click on the like/unlike view.
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
            action = favoriteResponseDataModel.isMyFav
        )

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter.hitApiToLikeUnlikeVideo(
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
            this@ProductPhaseListingActivity,
            requestMasterVideo = requestMasterVideo,
            context = this@ProductPhaseListingActivity
        ).show()
    }

    /**
     * It is called when user click on the mastered text.
     *
     * @param requestMasterVideo
     */
    private fun clickedMastered(videoId: Int) {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter.hitApiToMasterVideo(
                token = SharedPreferencesHelper.getAuthToken(),
                videoId = videoId
            )
        }
    }

    /**
     * It is called when user click on the cancel text.
     */
    private fun clickedCancel(requestMasterVideo: RequestMasterVideo?) {
        if (null != requestMasterVideo) {
            currentPage = 0
            videoList?.clear()

            init()
        }
    }

    private fun addOnScrollListener() {
        rvProductPhases.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                                presenter!!.getVideoList(
                                    SharedPreferencesHelper.getAuthToken(),
                                    phaseModel?.id!!.toInt(),
                                    ++currentPage,
                                    limit = Constants.App.Number.FIFTEEN
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

    /**
     * Here is updating the UI for empty list
     */
    private fun updateUIForEmptyList() {
        tvNoDataFound.visibility = View.VISIBLE
        tvNoDataFound.text = getString(R.string.empty_list)
    }

    /**
     * Here is updating the purchase type
     */
   /* private fun getPurchaseType(favoriteResponseDataModel: FavoriteResponseDataModel) {
        favoriteModel = favoriteResponseDataModel

        val model  = SharedPreferencesHelper.getUserInfoModel()
        //model!!.proAccess = favoriteModel.pro_access

      //  SharedPreferencesHelper.storeUserInfoModel(model)

        when {

            //pro_access
            favoriteResponseDataModel.msg == "" && favoriteResponseDataModel.productInfo.purchaseType!=3->
                showDialogToBuyMembership()

            favoriteResponseDataModel.msg == "Expired Membership." && favoriteResponseDataModel.membership.amountPaid < 1 ->
                showDialogToBuyMembership()

            favoriteResponseDataModel.msg == "Expired Membership." && favoriteResponseDataModel.membership.amountPaid > 0 ->
                showDialogToBuyMembership()

           *//* SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1 ->
                clickedToPlayPhaseVideos(
                    favoriteResponseDataModel = favoriteResponseDataModel
                )*//*

            favoriteResponseDataModel.alreadyBuy == true -> clickedToPlayPhaseVideos(
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
*/
    private fun checkConditionForPurchaseTypeOne(favoriteResponseDataModel: FavoriteResponseDataModel) {
        when {
            favoriteResponseDataModel.existInCart == true ->
                DialogAddProductToCartOrUpgradeMembership(
                    this@ProductPhaseListingActivity,
                    addToCart = getString(R.string.view_cart),
                    context = this@ProductPhaseListingActivity
                ).show()

            else -> DialogAddProductToCartOrUpgradeMembership(
                this@ProductPhaseListingActivity,
                addToCart = getString(R.string.add_to_cart),
                context = this@ProductPhaseListingActivity
            ).show()
        }
    }

    private fun checkConditionForPurchaseTypeTwo(favoriteResponseDataModel: FavoriteResponseDataModel) {
        if (favoriteResponseDataModel.isSummerProgramSubscribed == 1) {
            clickedToPlayPhaseVideos(favoriteResponseDataModel = favoriteModel)
        } else {
            if (isInternetConnected(shouldShowToast = true)) {
                showProgressDialog("")
                presenter.getUserMembership(AppHelper.getCurrentTimestampInSeconds())
            }
        }
    }

    private fun showDialogToBuyMembership() {
        DialogAddProductToCartOrUpgradeMembership(
            this@ProductPhaseListingActivity,
            addToCart = getString(R.string.upgrade),
            context = this@ProductPhaseListingActivity
        ).show()
    }

    private fun checkConditionForPurchaseTypeThree(favoriteResponseDataModel: FavoriteResponseDataModel) {
        clickedToPlayPhaseVideos(favoriteResponseDataModel = favoriteResponseDataModel)
    }

    /**
     * It is called when user clicked on pdf view
     */
    private fun clickedToOpenPdf(favoriteResponseDataModel: FavoriteResponseDataModel?) {
        favoriteResponseDataModel?.apply {
            showPdfView(pdfUrl = pdf)
        }
    }

    /**
     * It is called to show pdf view
     */
    private fun showPdfView(pdfUrl: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
        startActivity(browserIntent)
    }

    /**
     * It is called when user play phase video
     */
    private fun clickedToPlayPhaseVideos(favoriteResponseDataModel: FavoriteResponseDataModel) {
        val getLink = getVideoUrl(favoriteResponseDataModel)
        Log.d(TAG, "Video Link  => $getLink")

        if (getLink == null || getLink.isEmpty())
            showToast(message = "No video found")
        else {
            /*val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.App.VIDEO_LINK, getLink)
            intent.putExtra(Constants.App.VIDEO_ID, favoriteResponseDataModel.id.toInt())
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

        if (model.preset720p.isEmpty() && model.preset1080p.isEmpty()) {

            when {
                model.preset480p.isNotEmpty() -> return model.preset480p
                model.preset360p.isNotEmpty() -> return model.preset360p
                model.preset240p.isNotEmpty() -> return model.preset240p
                model.iphone4s.isNotEmpty() -> return model.iphone4s
            }

        } else {

            if (model.preset720p.isNotEmpty())
                return model.preset720p

            if (model.preset1080p.isNotEmpty())
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

           /* presenter.apiPostToAddProduct(
                SharedPreferencesHelper.getAuthToken(),
                addProductToCartRequestModel = addProductToCartRequestModel.apply {
                    product_id = favoriteModel.productInfo.id.toString()
                })*/
        }
    }

    /**
     * It is called when user clicked on view cart
     */
    private fun clickedViewCart() {
        startActivityForResult(
            Intent(this@ProductPhaseListingActivity, CartActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PRODUCT_PHASES_LISTING_SCREEN
        )
    }

    /**
     * It is called when user clicked upgrade membership
     */
    private fun clickedUpgradeMembership() {
        startActivityForResult(
            Intent(this@ProductPhaseListingActivity, MembershipsActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PRODUCT_PHASES_LISTING_SCREEN
        )
    }

    /**
     * Here is adding data to list
     *
     * @param listData
     */
    private fun addDataToList(listData: ArrayList<FavoriteResponseDataModel>) {
        videoList?.addAll(listData)
        productPhasesAdapter?.notifyDataSetChanged()
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            ItemClickType.LIKE_UNLIKE -> checkForLoginStatus(
                favoriteResponseDataModel = model as FavoriteResponseDataModel,
                position = position,
                isCallForFav = true
            )

            ItemClickType.MASTER -> checkForLoginStatus(
                favoriteResponseDataModel = model as FavoriteResponseDataModel,
                position = position,
                isCallForFav = false
            )

            ItemClickType.PLAY_VIDEO -> checkForLoginStatus(
                favoriteResponseDataModel = model as FavoriteResponseDataModel,
                position = position,
                isCallForFav = null
            )

            ItemClickType.PDF -> clickedToOpenPdf(favoriteResponseDataModel = model as FavoriteResponseDataModel)

            ItemClickType.PLAY_PHASE_VIDEO ->   clickedToPlayPhaseVideos(
                favoriteResponseDataModel = model as FavoriteResponseDataModel
            )/* getPurchaseType(
                favoriteResponseDataModel = model as FavoriteResponseDataModel
            )*/

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
            /*  DialogEventType.MASTERED -> clickedMastered(
                      requestMasterVideo = model as RequestMasterVideo
              )*/

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.PHASE_VIDEO_LISTING -> init()

                    Constants.App.RequestCode.PRODUCT_PHASES_LISTING_SCREEN -> {
                        currentPage = 0
                        videoList?.clear()

                        init()
                    }

                    Constants.App.RequestCode.VIDEO_PLAYER -> {
                        /* val isMastered: Int? =
                                 data?.getSerializableExtra(Constants.App.IS_MASTERED) as Int?

                         // check is video not already mastered
                         if (isMastered == Constants.VALUE.FALSE) {
                             masterAfterVideoCompletion = true

                             val videoId: Int? =
                                     data?.getSerializableExtra(Constants.App.VIDEO_ID) as Int?

                             callToOpenMasteredDialog(video_id = videoId.toString(), position = 0)
                         } else {*/
                        currentPage = 0
                        videoList?.clear()

                        init()
//                        }
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                when (requestCode) {
                    Constants.App.RequestCode.VIDEO_PLAYER -> {
                        currentPage = 0
                        videoList?.clear()

                        init()
                    }

                    else -> {
                        currentPage = 0
                        videoList?.clear()

                        init()
                    }
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from Api get user membership
     *
     * @param membershipResponseModel is successful response of Api
     */
    override fun onUserMembershipResponseSuccess(membershipResponseModel: MembershipResponseModel.MembershipResponseFirstModel) {
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                membershipResponseModel.data.apply {
                    if (this == null) {
                        showDialogToBuyMembership()
                    } else if (isCanceled == 1) {
                        if (DateTimeHelper.isFutureTimeStampForSeconds(endDate)) {
                            clickedToPlayPhaseVideos(favoriteResponseDataModel = favoriteModel)
                        } else {
                            showDialogToBuyMembership()
                        }
                    } else {
                        clickedToPlayPhaseVideos(favoriteResponseDataModel = favoriteModel)
                    }
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from phases video api
     *
     * @param response success response
     */
    override fun onVideoListingSuccess(response: PhasesListingResponseModel) {
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                if (response.data!!.size == 0) {
                    updateUIForEmptyList()
                } else {
                    rvProductPhases.visibility = View.VISIBLE

                    currentPage = response.currentPage

                    if (response.nextPageUrl != null)
                        loadMore = true

                    val videoListingData: ArrayList<FavoriteResponseDataModel>? =
                        GsonHelper.convertJsonStringToJavaObject(
                            from = response.data!!,
                            to = object :
                                TypeToken<ArrayList<FavoriteResponseDataModel>>() {}.type
                        ) as ArrayList<FavoriteResponseDataModel>?

                    addDataToList(
                        listData = videoListingData!!
                    )
                }

                pbLoadMore.visibility = View.GONE
            }
        }
    }

    /**
     * When successful response or data retrieved from master video api
     *
     * @param response success response
     */
    override fun onMasteredVideoSuccess(response: CommonResponse) {
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductPhaseListingActivity.baseContext != null) {
                    hideProgressDialog()

                    showToast(response.message)

                    currentPage = 0
                    videoList?.clear()

                    init()
                }
            }
        }
    }

    /**
     * When success response from api like/unlike video
     *
     * @param response Success Response
     */
    override fun onLikeUnlikeVideoSuccess(response: CommonResponse) {
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductPhaseListingActivity.baseContext != null) {
                    hideProgressDialog()
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
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductPhaseListingActivity.baseContext != null) {
                    hideProgressDialog()

                    currentPage = 0
                    videoList?.clear()

                    init()

                    showToast(response.message)
                }
            }
        }
    }

    /**
     * When error occurred in success response
     *
     * @param errorResponse Error Response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@ProductPhaseListingActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductPhaseListingActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(errorResponse.errorDescription)
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}

