package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.DateTimeHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.NewVideosAdapter
import com.fivestarmind.android.mvp.adapter.ProductAdapter
import com.fivestarmind.android.mvp.adapter.ProductCategoryAdapter
import com.fivestarmind.android.mvp.dialog.DialogAddProductToCartOrUpgradeMembership
import com.fivestarmind.android.mvp.model.request.AddProductToCartRequestModel
import com.fivestarmind.android.mvp.model.request.ProductRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.ProgramsPresenter
import kotlinx.android.synthetic.main.activity_programs.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ProgramsActivity : BaseActivity(), ProgramsPresenter.ResponseCallBack, ProgramInterface,
    RecyclerViewItemListener {

    override val TAG = ProgramsActivity::class.java.simpleName
    private lateinit var presenter: ProgramsPresenter

    private var productCategoryAdapter: ProductCategoryAdapter? = null
    private var newVideosAdapter: NewVideosAdapter? = null
    private var productsAdapter: ProductAdapter? = null

    private var productCategoryList: ArrayList<ProductCategoryDataModel>? = null
    private var newVideosList: ArrayList<NewVideosModel>? = null
    private var productsList: ArrayList<AllProductsDataModel>? = null

    private var productRequestModel: ProductRequestModel? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    private var category_id = ""
    private var isCategorySelected = false

    private var loadMore = false
    private var currentPage = 0

    private var newVideoModel = NewVideosModel()
    private var addProductToCartRequestModel = AddProductToCartRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_programs)

        setTitleBar()

        initPresenter()
        initData()

        initUI()
        init()

        initRecyclerView()
        setClickListener()
    }

    override fun onResume() {
        super.onResume()

        currentPage = 0
        productsList?.clear()
    }

    /**
     * Here is updating the title of the screen
     */
    private fun setTitleBar() {
        tvTitle.text = getString(R.string.programs)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = ProgramsPresenter(this)
    }

    private fun initData() {
        productCategoryList = arrayListOf()
        newVideosList = arrayListOf()
        productsList = arrayListOf()
    }

    private fun initUI() {
        refreshLayout.setOnRefreshListener(refresh)
        refreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        refreshLayout.isRefreshing = true

        currentPage = 0
        productsList?.clear()

        init()
    }

    private fun initRecyclerView() {
        productCategoryAdapter =
            ProductCategoryAdapter(this@ProgramsActivity, productCategoryList!!, this)

        rvProductsCategories.apply {
            layoutManager = LinearLayoutManager(
                this@ProgramsActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = productCategoryAdapter
        }

        newVideosAdapter =
            NewVideosAdapter(newVideosList!!, this@ProgramsActivity, this@ProgramsActivity)

        rvNewVideos.apply {
            layoutManager = LinearLayoutManager(
                this@ProgramsActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = newVideosAdapter
        }

        productsAdapter = ProductAdapter(
            productsList = productsList!!,
            activity = this,
            recyclerViewItemListener = this@ProgramsActivity,
            isFromHome = false
        )

        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rvProducts.apply {
            layoutManager = linearLayoutManager
            adapter = productsAdapter
        }
    }

    private fun init() {
        if (isInternetConnected(shouldShowToast = true)) {
           // presenter.apiToGetProductCategories(SharedPreferencesHelper.getAuthToken())
            //presenter.apiToGetProductCategories(SharedPreferencesHelper.getAppID().toInt())

//            Handler().postDelayed({
//                presenter.apiToGetNewVideos(SharedPreferencesHelper.getAuthToken())
//            }, Constants.App.Number.THOUSAND.toLong())
        }
    }

    /**
     * Click event on view ivMenu
     */
    private fun setClickListener() {
        if (shouldProceedClick())
            ivBackMenu.setOnClickListener { onBackPressed() }
    }

    /**
     * Here connecting to server and uses Products API
     */
    private fun hitApiToGetProductWithProductId() {
        productRequestModel = ProductRequestModel().apply {
            categoryId = category_id
            limit = Constants.App.Number.FIFTEEN
            page = ++currentPage
        }

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter.apiToGetAllProducts(
                SharedPreferencesHelper.getAuthToken(),
                productRequestModel!!
            )

            addOnScrollListener()
        }
    }

    private fun addOnScrollListener() {
       /* svProducts.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > 0) {
                val total = linearLayoutManager!!.itemCount
                val lastVisibleItemCount =
                    linearLayoutManager!!.findLastVisibleItemPosition()
                if (loadMore) {
                    if (total > 0)
                        if (total - 1 == lastVisibleItemCount) {
                            loadMore = false
                            hitApiToGetProductWithProductId()
                            pbLoadMore.visibility = View.VISIBLE
                        }
                } else {
                    pbLoadMore.visibility = View.GONE
                }
            }
        }*/
    }

    private fun getFirstCategoryId(categoryList: ArrayList<ProductCategoryDataModel>) {
        val categoryModel = categoryList.elementAt(Constants.App.Number.ZERO)
        category_id = categoryModel.id

        isCategorySelected = true
        updateCategoryIdInAdapter()

        currentPage = 0
        productsList?.clear()
        hitApiToGetProductWithProductId()
    }

    private fun addDataToProductCategoryList(categoryList: ArrayList<ProductCategoryDataModel>) {
        productCategoryList!!.clear()

        if (categoryList.isNotEmpty())
            productCategoryList!!.addAll(categoryList)

        productCategoryAdapter?.notifyDataSetChanged()
    }

    private fun addDataToNewVideosList(newVideosList: ArrayList<NewVideosModel>) {
        this.newVideosList!!.clear()

        if (newVideosList.isNotEmpty())
            this.newVideosList!!.addAll(newVideosList)

        newVideosAdapter?.notifyDataSetChanged()
    }

    private fun addDataToProductList(productList: ArrayList<AllProductsDataModel>) {
//        productsList!!.clear()

        if (productList.isNotEmpty())
            productsList!!.addAll(productList)

        productsAdapter?.notifyDataSetChanged()
    }

    override fun onProductCategorySelected(position: Int, productId: Int, categoryName: String) {
        currentPage = 0
        productsList?.clear()

        category_id = productId.toString()
        updateCategoryIdInAdapter()

        isCategorySelected = true
        hitApiToGetProductWithProductId()
    }

    override fun onItemSelected(position: Int, productId: Int) {

    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLoginStatus(
        newVideosModel: NewVideosModel,
        position: Int,
    ) {
        when (AppHelper.isUserLoggedIn()) {
            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.PROGRAM_SCREEN
                )
            }

            else -> {}
        }
    }

    override fun onListEmpty(isEmpty: Boolean) {

    }

    private fun updateCategoryIdInAdapter() {
        productCategoryAdapter?.updateCategoryId(category_id)
        productCategoryAdapter?.notifyDataSetChanged()
    }

    /**
     * Here is updating the purchase type
     */
    private fun getPurchaseType(newVideosModel: NewVideosModel) {
        newVideoModel = newVideosModel
        when {
           /* SharedPreferencesHelper.getUserInfoModel()?.proAccess == 1 -> clickedToPlayVideos(
                newVideosModel = newVideosModel
            )*/

            newVideosModel.alreadyBuy == true -> clickedToPlayVideos(
                newVideosModel = newVideosModel
            )

            newVideoModel.productInfo.purchaseType == Constants.App.Number.ONE -> {
                checkConditionForPurchaseTypeOne(newVideosModel = newVideosModel)
            }

            newVideoModel.productInfo.purchaseType == Constants.App.Number.TWO -> {
                checkConditionForPurchaseTypeTwo(newVideosModel = newVideosModel)
            }

            newVideoModel.productInfo.purchaseType == Constants.App.Number.THREE -> {
                checkConditionForPurchaseTypeThree(newVideosModel = newVideosModel)
            }
        }
    }

    private fun checkConditionForPurchaseTypeOne(newVideosModel: NewVideosModel) {
        when {
            newVideosModel.existInCart == true ->
                DialogAddProductToCartOrUpgradeMembership(
                    this@ProgramsActivity,
                    addToCart = getString(R.string.view_cart),
                    context = this@ProgramsActivity
                ).show()

            else -> DialogAddProductToCartOrUpgradeMembership(
                this@ProgramsActivity,
                addToCart = getString(R.string.add_to_cart),
                context = this@ProgramsActivity
            ).show()
        }
    }

    private fun checkConditionForPurchaseTypeTwo(newVideosModel: NewVideosModel) {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter?.getUserMembership(AppHelper.getCurrentTimestampInSeconds())
        }
    }

    private fun showDialogToBuyMembership() {
        DialogAddProductToCartOrUpgradeMembership(
            this@ProgramsActivity,
            addToCart = getString(R.string.upgrade),
            context = this@ProgramsActivity
        ).show()
    }

    private fun checkConditionForPurchaseTypeThree(newVideosModel: NewVideosModel) {
        clickedToPlayVideos(newVideosModel = newVideosModel)
    }

    /**
     * It is called when user clicked on phase videos
     */
    private fun clickedToPlayVideos(newVideosModel: NewVideosModel) {
        val getLink = getVideoUrl(newVideosModel)
        Log.d(TAG, "Video Link  => $getLink")

        if (getLink == null)
            showToast(message = "No video found")
        else {
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.App.VIDEO_LINK, getLink)
            intent.putExtra(Constants.App.VIDEO_ID, newVideosModel.id)
            intent.putExtra(Constants.App.IS_MASTERED, newVideosModel.isMasteredByMe)
            startActivityForResult(
                intent
                    .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                Constants.App.RequestCode.VIDEO_PLAYER
            )
        }
    }

    private fun getVideoUrl(model: NewVideosModel): String? {
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
                    product_id = newVideoModel.productInfo.id.toString()
                })
        }
    }

    /**
     * It is called when user clicked on view cart
     */
    private fun clickedViewCart() {
        startActivityForResult(
            Intent(this@ProgramsActivity, CartActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PROGRAMS_SCREEN
        )
    }

    /**
     * It is called when user clicked upgrade membership
     */
    private fun clickedUpgradeMembership() {
        startActivityForResult(
            Intent(this@ProgramsActivity, MembershipsActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.PROGRAMS_SCREEN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {

            Activity.RESULT_CANCELED -> {
                when (requestCode) {
                    Constants.App.RequestCode.VIDEO_PLAYER -> {

                    }

                    else -> {
                        init()
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
            ItemClickType.PLAY_VIDEO -> checkForLoginStatus(
                newVideosModel = model as NewVideosModel,
                position = position
            )

            ItemClickType.PLAY_PHASE_VIDEO ->
                getPurchaseType(newVideosModel = model as NewVideosModel)

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
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                membershipResponseModel.data.apply {
                    if (this == null) {
                        showDialogToBuyMembership()
                    } else if (isCanceled == 1) {
                        if (DateTimeHelper.isFutureTimeStampForSeconds(endDate)) {
                            clickedToPlayVideos(newVideosModel = newVideoModel)
                        } else {
                            showDialogToBuyMembership()
                        }
                    } else {
                        clickedToPlayVideos(newVideosModel = newVideoModel)
                    }
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from categories api
     *
     * @param response success response
     */
    override fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel) {
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProgramsActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                   // addDataToProductCategoryList(categoryList = response.data)

                    if (!isCategorySelected)
                    //     getFirstCategoryId(categoryList = response.data)
                    else {
                        currentPage = 0
                        productsList?.clear()

                        hitApiToGetProductWithProductId()
                    }
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from products api
     *
     * @param response success response
     */
    override fun onAllProductResponseSuccess(response: AllProductsResponseModel) {
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProgramsActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    addDataToProductList(productList = response.data)
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from new videos api
     *
     * @param response success response
     */
    override fun onNewVideosResponseSuccess(response: ArrayList<NewVideosModel>) {
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProgramsActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    addDataToNewVideosList(newVideosList = response)
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
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProgramsActivity.baseContext != null) {
                    hideProgressDialog()

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
        if (this@ProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@ProgramsActivity.baseContext != null) {
                    hideProgressDialog()

                    refreshLayout.isRefreshing = false
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() = finish()
}