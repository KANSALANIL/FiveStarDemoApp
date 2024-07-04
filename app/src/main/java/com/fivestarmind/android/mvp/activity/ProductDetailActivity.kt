package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
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
import com.fivestarmind.android.mvp.adapter.ProductDetailPhasesAdapter
import com.fivestarmind.android.mvp.dialog.DialogAddProductToCartOrUpgradeMembership
import com.fivestarmind.android.mvp.dialog.DialogMastered
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.request.RequestMasterVideo
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.ProductDetailPresenter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ProductDetailActivity : BaseActivity(), ProductDetailPresenter.ResponseCallBack,
    RecyclerViewItemListener {

    override var TAG = ProductDetailActivity::class.java.simpleName
    lateinit var presenter: ProductDetailPresenter
    private var addProductToCartRequestModel = AddProductToCartRequestModel()
    private var productId = ""

    private var productDetailPhasesAdapter: ProductDetailPhasesAdapter? = null
    private var productDetailLayoutManager: LinearLayoutManager? = null

    private var phasesVideoList: ArrayList<PhaseVideoModel> =
        arrayListOf()

    private var favVideoPosition: Int? = null
    private var masteredVideoId: Int? = null
    private var masterAfterVideoCompletion: Boolean? = null

    private var programDetailResponseModel = ProgramDetailResponseModel()

    // "On Demand for Sale" => 1
    // "On Subscription" => 2
    // "Free" => 3
    private var purchaseType: Int? = null
    private var phaseVideoModel: PhaseVideoModel? = null

    private var productImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        getDataFromIntent()
        initData()
        initRecyclerViewWithAdapter()

        setToolbarTitle()
        initPresenter()

        hitApiToGetProductDetail()
        setClickListener()
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.PRODUCT_ID))
            productId = intent.getStringExtra(Constants.App.PRODUCT_ID)!!

        if (intent.hasExtra(Constants.App.IMAGE) && null != intent.getStringExtra(Constants.App.IMAGE)) {
            productImage = intent.getStringExtra(Constants.App.IMAGE)
            setImage(productImage)
        }
    }

    private fun initData() {
        phasesVideoList = arrayListOf()
    }

    /**
     * Here recyclerViews are setup with it's adapter
     */
    private fun initRecyclerViewWithAdapter() {
        productDetailPhasesAdapter = ProductDetailPhasesAdapter(
            activity = this,
            phasesVideoList = phasesVideoList,
            listener = this@ProductDetailActivity
        )

        productDetailLayoutManager =
            LinearLayoutManager(this@ProductDetailActivity, RecyclerView.VERTICAL, false)

        rvPhases.apply {
            layoutManager = this@ProductDetailActivity.productDetailLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = productDetailPhasesAdapter
        }
    }

    /**
     * Here is updating the title of the screen
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.detail)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ProductDetailPresenter(this)
    }

    /**
     * Click event on views ivMenu and tvAddToCart
     */
    private fun setClickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        tvAddToCart.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

                R.id.tvAddToCart -> clickedAddToOrViewCartView()
            }
    }

    /**
     * It is called when user click on AddToCart/ViewCart button
     */
    private fun clickedAddToOrViewCartView() {
        when (tvAddToCart.text) {
//            getString(R.string.view_cart) -> clickedViewCart()
            getString(R.string.purchased) -> {
            }
            getString(R.string.add_to_cart) -> checkForLogin()
        }
    }

    /**
     * It is called when user clicked on view cart
     */
    private fun clickedViewCart() {
        startActivityForResult(
            Intent(this@ProductDetailActivity, CartActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PRODUCT_DETAIL_CART
        )
    }

    /**
     * Call for api product details
     */
    private fun hitApiToGetProductDetail() {
        Log.d(TAG, "hitApiToGetProductDetail")

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.apiGetProductDetail(SharedPreferencesHelper.getAuthToken(), productId)
        }
    }

    /**
     * Call for api add program into card
     */
    private fun apiHitAddProductToCart() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter.apiPostToAddProduct(
                SharedPreferencesHelper.getAuthToken(),
                addProductToCartRequestModel = addProductToCartRequestModel.apply {
                    product_id = productId
                })
        }
    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLogin() {
        when (AppHelper.isUserLoggedIn()) {
            true -> apiHitAddProductToCart()

            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.PRODUCT_DETAIL
                )
            }
        }
    }

    /**
     * set product image from intent data
     *
     * @param image
     */
    private fun setImage(image: String?) {
        pbLoadMore.visibility = View.VISIBLE

        if (image!!.isNotEmpty())
            Picasso.get()
                .load(image)
                .placeholder(ivProduct.drawable)
                .into(ivProduct, object : Callback {
                    override fun onSuccess() {
                        pbLoadMore.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        pbLoadMore.visibility = View.GONE
                    }
                }) else ivProduct.setImageDrawable(resources.getDrawable(R.drawable.ic_placeholder_corner_8))
    }

    /**
     * Set program details in views from api response
     *
     * @param response
     */
    private fun setProductDetail(response: ProgramDetailResponseModel) {
        scrollView.visibility = View.VISIBLE

        tvVideo.text = getString(
            R.string.format_video_pdf, AppHelper.formatVideoCount(
                context = this,
                video = response.data.videoCount
            ), AppHelper.formatPdfCount(
                context = this,
                pdfCount = response.data.pdfCount
            )
        )

        var description =
            "<font color='#AEAEAE'>" + "<font size='3'>" + response.data.description.replace(
                ":black",
                ":#AEAEAE"
            ) + "</font>"
        tvDescription.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        tvDescription.loadDataWithBaseURL(
            null,
            description,
            "text/html",
            "utf-8",
            null
        )

        tvDescription.setBackgroundColor(Color.TRANSPARENT)

        val webSettingsDescription = tvDescription.settings
        webSettingsDescription.defaultFontSize = 13

        tvProductName.text = response.data.productName

        if (response.data.price == "0" || response.data.price == null) tvPrice.visibility =
            View.GONE
        else {
            tvPrice.visibility = View.VISIBLE
            tvPrice.text = "$${response.data.price}"
        }

        if (productImage == null) {
            if (response.data.image.size != 0)
                setImage(image = response.data.image[0]?.productImage)
        }

        if (AppHelper.isUserLoggedIn()) {
            /*val model = SharedPreferencesHelper.getUserInfoModel()?.apply {
                proAccess = response.pro_access
            }*/
         //   SharedPreferencesHelper.storeUserInfoModel(model!!)
        }

//        pbSkill.progress = response.data.skill
//        pbTactical.progress = response.data.tectical
//        pbIntensity.progress = response.data.intensity
    }

    /**
     * Update status of program is already purchased, already added into card or not handles here
     *
     * @param response
     */
    private fun updateCartStatus(response: ProgramDetailResponseModel) {
        if (response.data.purchaseType == Constants.App.Number.ONE) {
            tvAddToCart.visibility = View.VISIBLE
            if (response.alreadyBuy) {
                tvAddToCart.text = getString(R.string.purchased)
                tvAddToCart.setBackgroundResource(R.drawable.drawable_gray_rectangle_hollow)
                tvAddToCart.setTextColor(resources.getColor(R.color.grayLight))

            } else {
                if (response.existInCart)
                    tvAddToCart.text = getString(R.string.view_cart)
                else tvAddToCart.text = getString(R.string.add_to_cart)
            }
        }
    }

    /**
     * Here is adding data to adapter list
     *
     * @param phases
     */
    private fun addDataToPhasesList(phases: ArrayList<PhaseVideoModel>) {
        phasesVideoList.clear()

        if (phases.isNotEmpty()) {
            val index =
                phases.firstOrNull { it.listType == getString(R.string.without_phase) }
                    ?.let { phases.indexOf(it) }
                    ?: -1
            if (index > -1) {
                phasesVideoList.addAll(phases[index].videoListing)
                phasesVideoList.addAll(phases)

                val index =
                    phasesVideoList.firstOrNull { it.title == getString(R.string.phase_zero) }
                        ?.let { phasesVideoList.indexOf(it) }
                        ?: -1
                if (index > -1) {
                    phasesVideoList.remove(phasesVideoList[index])
                }

            } else
                phasesVideoList.addAll(phases)

            productDetailPhasesAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * It is called when user click on like/unlike view
     *
     * @param phaseVideoModel
     */
    private fun clickedLikeUnlike(phaseVideoModel: PhaseVideoModel, position: Int) {
        favVideoPosition = position

        val favoriteVideoRequestModel = FavoriteVideoRequestModel(
            productVideoId = phaseVideoModel.id.toInt(),
            action = phaseVideoModel.isMyFav!!
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
     * Check for user logged in or not
     *
     * @param phaseVideoModel
     * @param position
     * @param isCallForFav
     */
    private fun checkForLoginStatus(
        phaseVideoModel: PhaseVideoModel,
        position: Int,
        isCallForFav: Boolean?
    ) {
        when (AppHelper.isUserLoggedIn()) {
            true -> {
                when (isCallForFav) {
                    true -> clickedLikeUnlike(
                        phaseVideoModel = phaseVideoModel,
                        position = position
                    )
                    false -> callToOpenMasteredDialog(
                        video_id = phaseVideoModel.id,
                        position = position
                    )
                    else -> {

                    }
                }
            }

            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.PRODUCT_DETAIL
                )
            }
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
            this@ProductDetailActivity,
            requestMasterVideo = requestMasterVideo,
            context = this@ProductDetailActivity
        ).show()
    }

    /**
     * It is called when user click on the mastered text.
     *
     * @param requestMasterVideo
     */
    private fun clickedMastered(model:PhaseVideoModel) {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter.hitApiToMasterVideo(
                token = SharedPreferencesHelper.getAuthToken(),
                videoId = model.id.toInt()
            )
        }
    }

    /**
     * It is called when user click on the cancel text.
     */
    private fun clickedCancel(requestMasterVideo: RequestMasterVideo?) {
        if (null != requestMasterVideo)
            hitApiToGetProductDetail()
    }

    /**
     * It is called when user clicked add to cart
     */
    private fun clickedAddToCart() {
        apiHitAddProductToCart()
    }

    /**
     * It is called when user clicked upgrade membership
     */
    private fun clickedUpgradeMembership() {
        startActivityForResult(
            Intent(this@ProductDetailActivity, MembershipsActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PRODUCT_DETAIL
        )
    }

    /**
     * It is called when user play without phase video
     */
    private fun clickedWithoutPhaseVideoView() {
        val getLink = getVideoUrl(phaseVideoModel!!)
        Log.d(TAG, "Video Link  => $getLink")

        if (getLink == null || getLink.isEmpty())
            showToast(message = "No video found")
        else {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.App.VIDEO_LINK, getLink)
            intent.putExtra(Constants.App.VIDEO_ID, phaseVideoModel?.id!!.toInt())
            intent.putExtra(Constants.App.IS_MASTERED, phaseVideoModel?.isMasteredByMe)

            startActivityForResult(
                intent
                    .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                Constants.App.RequestCode.VIDEO_PLAYER
            )
        }
    }

    /**
     * It returns url for video
     */
    private fun getVideoUrl(model: PhaseVideoModel): String? {
        Log.d(TAG, "getVideoUrl response  => ${Gson().toJson(model)}")

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
     * Here is updating the purchase type
     */
    private fun getPurchaseType(phaseVideoModel: PhaseVideoModel?) {
        this.phaseVideoModel = phaseVideoModel

        when {

            programDetailResponseModel.message == "" && programDetailResponseModel.data.purchaseType!=3 ->
                showDialogToBuyMembership()

            programDetailResponseModel.message == "Expired Membership." && programDetailResponseModel.membership.amountPaid < 1 ->
                showDialogToBuyMembership()

            programDetailResponseModel.message == "Expired Membership." && programDetailResponseModel.membership.amountPaid > 0 ->
                showDialogToBuyMembership()

            /*SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1 ->
                clickedWithoutPhaseVideoView()
*/
            programDetailResponseModel.alreadyBuy ->
                clickedWithoutPhaseVideoView()

            programDetailResponseModel.data.purchaseType == Constants.App.Number.ONE -> {
                checkConditionForPurchaseTypeOne()
            }

            programDetailResponseModel.data.purchaseType == Constants.App.Number.TWO -> {
                checkConditionForPurchaseTypeTwo()
            }

            programDetailResponseModel.data.purchaseType == Constants.App.Number.THREE -> {
                checkConditionForPurchaseTypeThree()
            }
        }
    }

    private fun checkConditionForPurchaseTypeOne() {
        when {
            programDetailResponseModel.existInCart ->
                DialogAddProductToCartOrUpgradeMembership(
                    this@ProductDetailActivity,
                    addToCart = getString(R.string.view_cart),
                    context = this@ProductDetailActivity
                ).show()

            else -> DialogAddProductToCartOrUpgradeMembership(
                this@ProductDetailActivity,
                addToCart = getString(R.string.add_to_cart),
                context = this@ProductDetailActivity
            ).show()
        }
    }

    private fun checkConditionForPurchaseTypeTwo() {
        if (programDetailResponseModel.isSummerProgramSubscribed == 1) {
            clickedWithoutPhaseVideoView()
        } else {
            if (isInternetConnected(shouldShowToast = true)) {
                showProgressDialog("")
                presenter.getUserMembership(AppHelper.getCurrentTimestampInSeconds())
            }
        }
    }

    private fun showDialogToBuyMembership() {
        DialogAddProductToCartOrUpgradeMembership(
            this@ProductDetailActivity,
            addToCart = getString(R.string.upgrade),
            context = this@ProductDetailActivity
        ).show()
    }

    private fun checkConditionForPurchaseTypeThree() {
        clickedWithoutPhaseVideoView()
    }

    /**
     * It is called when user clicked on phase videos
     */
    private fun clickedToPlayPhaseVideos(phaseVideoModel: PhaseVideoModel?) {
        val intent = Intent(this, ProductPhaseListingActivity::class.java)
        intent.putExtra(Constants.App.PHASE_MODEL, Gson().toJson(phaseVideoModel))
        startActivity(intent)
    }

    /**
     * It is called when user clicked on pdf view
     */
    private fun clickedToOpenPdf(phaseVideoModel: PhaseVideoModel?) {
        phaseVideoModel?.apply {
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

    private fun afterLoginSuccess() {
        apiHitAddProductToCart()

        Handler().postDelayed({
            hitApiToGetProductDetail()
        }, Constants.App.Number.THOUSAND.toLong())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.PRODUCT_DETAIL -> afterLoginSuccess()

                    Constants.App.RequestCode.PRODUCT_DETAIL_CART -> hitApiToGetProductDetail()

                    Constants.App.RequestCode.VIDEO_PLAYER -> {
                      /*  val isMastered: Int? =
                            data?.getSerializableExtra(Constants.App.IS_MASTERED) as Int?

                        // check is video not already mastered
                        if (isMastered == Constants.VALUE.FALSE) {
                            masterAfterVideoCompletion = true

                            val videoId: Int? =
                                data?.getSerializableExtra(Constants.App.VIDEO_ID) as Int?

                            callToOpenMasteredDialog(video_id = videoId.toString(), position = 0)
                        } else*/

                        hitApiToGetProductDetail()
                    }
               }
            }

            Activity.RESULT_CANCELED -> {
                when (requestCode) {
                    Constants.App.RequestCode.VIDEO_PLAYER -> {
                        hitApiToGetProductDetail()
                    }

                    else -> hitApiToGetProductDetail()
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
            ItemClickType.LIKE_UNLIKE -> checkForLoginStatus(
                phaseVideoModel = model as PhaseVideoModel,
                position = position,
                isCallForFav = true
            )

            ItemClickType.MASTER ->
                clickedMastered(model as PhaseVideoModel
            )

            ItemClickType.PLAY_VIDEO -> checkForLoginStatus(
                phaseVideoModel = model as PhaseVideoModel,
                position = position,
                isCallForFav = null
            )

               ItemClickType.PLAY_WITHOUT_PHASE_VIDEO -> getPurchaseType(
                phaseVideoModel = model as PhaseVideoModel
            )

            ItemClickType.PLAY_PHASE_VIDEO -> clickedToPlayPhaseVideos(
                phaseVideoModel = model as PhaseVideoModel
            )

            ItemClickType.PDF -> clickedToOpenPdf(phaseVideoModel = model as PhaseVideoModel)

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
           /* DialogEventType.MASTERED -> clickedMastered(
                requestMasterVideo = model as RequestMasterVideo
            )*/

            DialogEventType.ADD_TO_CART -> clickedAddToCart()

            DialogEventType.VIEW_CART -> clickedViewCart()

            DialogEventType.UPGRADE_MEMBERSHIP -> clickedUpgradeMembership()

            DialogEventType.CANCEL -> clickedCancel(
                requestMasterVideo = model as RequestMasterVideo?
            )

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
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                membershipResponseModel.data.apply {
                    if (this == null) {
                        showDialogToBuyMembership()
                    } else if (isCanceled == 1) {
                        if (DateTimeHelper.isFutureTimeStampForSeconds(endDate)) {
                            clickedWithoutPhaseVideoView()
                        } else {
                            showDialogToBuyMembership()
                        }
                    } else {
                        clickedWithoutPhaseVideoView()
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
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductDetailActivity.baseContext != null) {
                    hideProgressDialog()

                    showToast(response.message)

                    hitApiToGetProductDetail()
                }
            }
        }
    }

    /**
     * When success response from api program details
     *
     * @param response Success Response
     */
    override fun onResponseSuccess(response: ProgramDetailResponseModel) {
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductDetailActivity.baseContext != null) {
                    hideProgressDialog()

                    programDetailResponseModel = response
                    setProductDetail(response)

                    addDataToPhasesList(phases = response.data.phases)
                    updateCartStatus(response)
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
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductDetailActivity.baseContext != null) {
                    hideProgressDialog()
                    tvAddToCart.text = getString(R.string.view_cart)

                    programDetailResponseModel.existInCart = true

                    showToast(response.message)
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
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductDetailActivity.baseContext != null) {
                    hideProgressDialog()
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
        if (this@ProductDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProductDetailActivity.baseContext != null) {
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
