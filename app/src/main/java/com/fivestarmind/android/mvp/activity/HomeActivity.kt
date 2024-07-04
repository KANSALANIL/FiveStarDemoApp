package com.fivestarmind.android.mvp.activity


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.fivestarmind.android.*
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.constant.AppConst
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.database.SharedPreferencesManager
import com.fivestarmind.android.dialog.PositiveNegativeAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.enum.WebViewType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.MyLocaleManager
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogListener
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.interfaces.SharedPrefConst
import com.fivestarmind.android.mvp.adapter.*
import com.fivestarmind.android.mvp.dialog.DialogDeleteAccount
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.FavoriteVideoRequestModel
import com.fivestarmind.android.mvp.model.request.ProductRequestModel
import com.fivestarmind.android.mvp.model.request.TotalHourInAppRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.HomePresenter
import com.fivestarmind.android.mvp.presenter.LetsGoPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.fivestarmind.android.socket.SocketManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_home_content.*
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_home_nav.*
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : BaseActivity(), HomePresenter.ResponseCallBack, DialogListener,
    ProgramInterface, RecyclerViewItemListener, LetsGoPresenter.ResponseCallBack,
    OnItemSelectedListener, DialogSesstionExpiredListener {

    override val TAG = HomeActivity::class.java.simpleName
    private lateinit var presenter: HomePresenter
    private lateinit var letsGoPresenter: LetsGoPresenter
    private var letsGoDataResponse: LetsGoDataResponse? = null
    private var productCategoryAdapter: ProductCategoryAdapter? = null
    private var featuredProductAdapter: FeaturedProductAdapter? = null
    private var productsAdapter: ProductAdapter? = null
    private var quoteAdapter: QuoteAdapter? = null

    private var todayQuoteList: ArrayList<TodayQuoteItem>? = null
    private var notificationList: ArrayList<NotificationData>? = null
    private var newNotificationAdapter: NewNotificationAdapter? = null

    private var performanceAdapter: PerformanceAdapter? = null

    private var productCategoryList: ArrayList<ProductCategoryDataModel>? = null
    private var featuredProductList: ArrayList<FeaturedProductDetailResModel>? = null
    private var productsList: ArrayList<AllProductsDataModel>? = null

    private var productRequestModel: ProductRequestModel? = null

    private var productCategoryId = ""
    private var isCategorySelected = false

    private var loadMore = false
    private var isResume = false
    private var currentPage = 0
    private var summerProgramId: Int? = null

    private var favVideoPosition: Int? = null

    private var databaseHelper = DatabaseHelper(this)

    private var linearLayoutManager: LinearLayoutManager? = null
    var filterAdapter: FilterAdapter? = null
    var dialogSessionExpired: DialogSessionExpired? = null

    var courses = arrayOf<String?>(
        "C", "Data structures",
        "Interview prep", "Algorithms",
        "DSA with java", "OS"
    )

    var filterItems: ArrayList<FilterItemModel>? = null
    var backPressed: Int = 0
    var homeTabsActivity: HomeTabsActivity? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeTabsActivity = HomeTabsActivity()
      //   getIntentFrom()
        saveAppStartTime()
        initPresenter()
        callOrganisationLogoApi()
        callNotificationListingApi()
        // callPerformanceThisWeekApi()
        setUserProfile()
        initData()
        initUI()
        init()
        initRecyclerView()
        setClickListener()
        initializeMiniAudioPlayer(homeAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        tvPlayer.text = SharedPreferencesHelper.getSubRoleName()
        // val emitters = Emitters(applicationContext)

        /*
            val locale = MyLocaleManager.getLocale(resources)
            val message = locale.language
        //  Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            if (message.equals("en")){
                ivEnglish.isSelected = true
            }else if(message.equals("es")){

                ivSpanish.isSelected = true
              }

       */


        if(!SharedPreferencesHelper.getMessageSenderId().isNullOrEmpty()){
            startActivity(Intent(this@HomeActivity, ChatActivity::class.java))

        }

    }


    fun getIntentFrom() {
        Log.d(TAG, "onNewIntent>>: " + intent.getStringExtra(Constants.App.FROM))

        if (intent.hasExtra(Constants.App.FROM) && null != intent.getStringExtra(Constants.App.FROM)) {
            Log.d(TAG, "onNewIntent1: " + intent!!.extras.toString())

        } else {
            Log.d(TAG, "onNewIntent2: " + intent!!.extras.toString())

            startActivity(Intent(this, AudioPlayActivity::class.java))

        }
    }

    private fun setUserProfile() {
        val userName = SharedPreferencesHelper.getUserName()
        val userImage = SharedPreferencesHelper.getUserImage()
        tvUserName.text = userName


        //  if(!userImage.equals("")) {
        progressBarProfile.visibility = View.VISIBLE

        Picasso.get()
            .load(ApiClient.BASE_URL_PROFILE + userImage)
            //  .networkPolicy(NetworkPolicy.NO_CACHE)
            // .memoryPolicy(MemoryPolicy.NO_CACHE)
            .error(R.drawable.ic_user_placeholder)
            .resize(160, 160)
            .placeholder(R.drawable.ic_user_placeholder)
            .into(ivUserProfile, object : Callback {
                override fun onSuccess() {
                    //   isImageSelected = true
                    progressBarProfile.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progressBarProfile.visibility = View.GONE
                    ivUserProfile.setImageDrawable(resources.getDrawable(R.drawable.ic_user_placeholder))
                }
            })
        // } else{ ivProfile.setImageDrawable(resources.getDrawable(R.drawable.ic_user_placeholder))}


    }

    private fun callOrganisationLogoApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("Please wait...")
            letsGoPresenter.getOrganisationLogo(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                SharedPreferencesHelper.getAppID().toInt()
            )

        }
    }

    private fun callNotificationListingApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            if (!isResume) {
                showProgressDialog("Please wait...")
            }

            presenter.apiToGetNotificationData(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                SharedPreferencesHelper.getAppID().toInt()
            )

        }
    }


    override fun onResume() {
        super.onResume()
        homeTabsActivity = HomeTabsActivity()

        isResume = true
        currentPage = 0
        productsList?.clear()
        //  initializePlayer()
        initializeMiniAudioPlayer(homeAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        updateLoginStatus()
        setUserProfile()
        callNotificationListingApi()
        //  callPerformanceThisWeekApi()

        //init()
        //getCartCount()
        // callForGetAppContentApi()
        //initViewPager(4)

    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        letsGoPresenter = LetsGoPresenter(this)
        presenter = HomePresenter(this)
    }

    private fun initData() {
        productCategoryList = arrayListOf()
        featuredProductList = arrayListOf()
        productsList = arrayListOf()
        notificationList = arrayListOf()
        todayQuoteList = arrayListOf()

    }

    private fun initUI() {
        refreshLayout.setOnRefreshListener(refresh)
        refreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        clContentFirst.visibility = View.GONE
        clContentSecond.visibility = View.GONE

    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        ProductCategoryAdapter(
            this@HomeActivity,
            productCategoryList,
            this
        ).also { productCategoryAdapter = it }

        rvProductsCategories.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = productCategoryAdapter
        }

        featuredProductAdapter = FeaturedProductAdapter(this@HomeActivity, featuredProductList!!)
        viewPagerFeaturedProduct.adapter = featuredProductAdapter

        productsAdapter = ProductAdapter(
            productsList = productsList!!,
            activity = this,
            recyclerViewItemListener = this@HomeActivity,
            isFromHome = true
        )

        linearLayoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)

        /*   rvProducts.apply {
               layoutManager = linearLayoutManager
               adapter = productsAdapter
           }
   */
        newNotificationAdapter = NewNotificationAdapter(
            activity = this,
            recyclerViewItemListener = this@HomeActivity,
            notificationDataList = notificationList
        )

        linearLayoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.HORIZONTAL, false)

        rvNewNotifications.apply {
            layoutManager = linearLayoutManager
            adapter = newNotificationAdapter
        }




        //set quoteAdapter

        linearLayoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.HORIZONTAL, false)

        quoteAdapter = QuoteAdapter(
            activity = this,
            todayQuoteList = todayQuoteList!!,
            listener = this@HomeActivity,
        )

        rvQuote.apply {
            layoutManager = linearLayoutManager
            adapter = quoteAdapter
        }


        /*performanceAdapter = PerformanceAdapter(
            activity = this,
            programInterface = this@HomeActivity,
        )

        linearLayoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)

        rvPerformance.apply {
            layoutManager = linearLayoutManager
            adapter = performanceAdapter
        }
*/

    }

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        refreshLayout.isRefreshing = true
        initRecyclerView()
        //  initializePlayer()
        initializeMiniAudioPlayer(homeAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        init()

        //currentPage = 0
        //   productsList?.clear()
    }

    /**
     * Updating card count
     */
    private fun getCartCount() {
        if (SharedPreferencesHelper.getAuthToken() == "") tvCartCount.visibility = View.GONE
        else callForGetCartCountApi()
    }

    private fun callForGetCartCountApi() {
        /*if (isInternetConnected(shouldShowToast = true))
            presenter.apiToGetCartCount(SharedPreferencesHelper.getAuthToken())*/
    }


    /**
     * Click event on views
     */
    private fun setClickListener() {

        //    ivSliderLogo.setOnClickListener(clickListener)
        ivMenu.setOnClickListener(clickListener)
        //   ivCart.setOnClickListener(clickListener)
        clMyAccount.setOnClickListener(clickListener)
        clPrograms.setOnClickListener(clickListener)
        clMemberships.setOnClickListener(clickListener)
        clJournal.setOnClickListener(clickListener)
        clEvaluation.setOnClickListener(clickListener)
        clFaq.setOnClickListener(clickListener)
        clSuggestions.setOnClickListener(clickListener)
        clPrivacyPolicy.setOnClickListener(clickListener)
        clTermsConditions.setOnClickListener(clickListener)
        //clContactUs.setOnClickListener(clickListener)
        clDeleteAccount.setOnClickListener(clickListener)
        clLogout.setOnClickListener(clickListener)
        //  ivLoginLogout.setOnClickListener(clickListener)
        ivBackArrow.setOnClickListener(clickListener)

        tvViewDetail.setOnClickListener(clickListener)
        clContactCenter.setOnClickListener(clickListener)
        clBookmarks.setOnClickListener(clickListener)
        ivBookMarks.visibility = View.GONE
        //ivBookMarks.setOnClickListener(clickListener)
        tvViewDetail.setOnClickListener(clickListener)
        ivUserProfile.setOnClickListener(clickListener)
        ivEnglish.setOnClickListener(clickListener)
        ivSpanish.setOnClickListener(clickListener)
        ivJapanese.setOnClickListener(clickListener)
        ivFilter.setOnClickListener(clickListener)

        initList()
        filterAdapter = FilterAdapter(this, filterItems!!)
        spinerFilter.adapter = filterAdapter
        spinerFilter.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                // It returns the clicked item.
                val clickedItem: FilterItemModel =
                    parent.getItemAtPosition(position) as FilterItemModel
                val name: String = clickedItem.getFilterName()

                Toast.makeText(this@HomeActivity, "$name selected", Toast.LENGTH_SHORT).show()
                view.findViewById<TextView>(R.id.spinnerName).setTextColor(Color.BLACK)
                tvFilterName.text = name
                // tvFilterName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera, 0, 0, 0);

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

    private fun init() {
        if (isInternetConnected(shouldShowToast = true)) {
            // presenter.apiToGetProductCategories()
            Handler().postDelayed({
                //Socket connetion
                AppController.mSocketManager = null
                  AppController.mSocketManager = SocketManager()
                AppController.mSocketManager!!.connect()

                /*  if (AppController.mSocketManager == null) {
                      AppController.mSocketManager = SocketManager()
                      AppController.mSocketManager!!.connect()
                  }else{
                      AppController.mSocketManager = SocketManager()
                      AppController.mSocketManager!!.connect()

                  }*/

                /*presenter.apiToGetProductCategories(
                    appId = SharedPreferencesHelper.getAppID().toInt()
                )*/

                presenter.apiToGetFeaturedProduct(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt()
                )
                presenter.apiToGetNotificationData(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt()
                )
                //callPerformanceThisWeekApi()

                Log.d(
                    TAG,
                    "apiToGetQuoteListing_saveStartTime: " + convertToCustomFormat(
                        getCurrentDateTime().toString()
                    )
                )

                presenter.apiToGetQuoteListing(
                    "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    SharedPreferencesHelper.getAppID().toInt(),
                    convertToCustomFormat(getCurrentDateTime().toString())
                )

                presenter.apiToGetSpecialContent(
                    "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    SharedPreferencesHelper.getAppID().toInt()
                )


            }, Constants.App.Number.THOUSAND.toLong())
        }
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {

                R.id.ivMenu -> clickedSideMenu()

                R.id.ivBackArrow -> clickedCloseMenu()

                R.id.clPrograms -> clickedPrograms()

                R.id.clMemberships -> clickedMemberships()

                R.id.clJournal -> clickedJournal()

                R.id.clEvaluation -> clickedEvaluation()

                R.id.clFaq -> clickedFaq()

                R.id.clSuggestions -> clickedSuggestions()

                R.id.clTermsConditions -> clickedTermsAndConditions()

                R.id.clContactUs -> clickedContactUs()

                R.id.clPrivacyPolicy -> clickedPrivacyPolicy()

                R.id.clDeleteAccount -> clickedDeleteAccount()

                R.id.clLogout -> clickedLogout()

                R.id.tvViewDetail -> clickedTargetImageAdd()

                R.id.clContactCenter -> clickedContactCenter()

                R.id.clBookmarks -> clickedMyBookmarks()

                R.id.ivBookMarks -> clickedMyBookmarks()

                R.id.ivUserProfile -> clickedMyAccount()

                R.id.ivEnglish -> setEnglishLanguage()

                R.id.ivSpanish -> setSpanishLanguage()

                R.id.ivJapanese -> setJapaneseLanguage()

                R.id.ivFilter -> clickedFilter()

               //  R.id.ivSliderLogo -> gotoOpenAllPdfListActivity()
            }
    }


        /**
         * It is called when user clicked on deleteAccount button
         */
        private fun clickedDeleteAccount() {
            DialogDeleteAccount(
                this,
                context = this@HomeActivity
            ).show()
        }



    private fun setJapaneseLanguage() {
        ivJapanese.isSelected = true

        ivEnglish.isSelected = false
        ivSpanish.isSelected = false

    }

    /**
     * here set English Language
     */
    private fun setEnglishLanguage() {
        ivEnglish.isSelected = true

        ivSpanish.isSelected = false
        ivJapanese.isSelected = false

        setNewLocale("en", false)
    }

    /**
     * here set Spanish Language
     */
    private fun setSpanishLanguage() {
        ivSpanish.isSelected = true
        ivEnglish.isSelected = false
        ivJapanese.isSelected = false
        setNewLocale("es", false)

    }

    private fun setNewLocale(language: String, restartProcess: Boolean): Boolean {
        //  localeManager.setNewLocale(this, language)
        AppController.localeManager?.setNewLocale(this, language)
        val i = Intent(this@HomeActivity, HomeActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        if (restartProcess) {
            System.exit(0)
        } else {
            val locale = MyLocaleManager.getLocale(resources)
            val message = locale.language
            Toast.makeText(this, "Activity restarted:-  " + message, Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun gotoOpenAllPdfListActivity() {
        // startActivity(Intent(this@HomeActivity, AllPdfListActivity::class.java))
    }

    /**
     * It is called when user clicked on contact center view
     */
    private fun clickedContactCenter() {
        startActivity(Intent(this@HomeActivity, ContactCenterActivity::class.java))
    }

    /**
     * It is called when user clicked on contact center view
     */
    private fun clickedFilter() {
        startActivity(
            Intent(
                this@HomeActivity,
                FilterActivity::class.java
            ).putExtra(Constants.App.FROM, "ShowBackButton")
        )
    }

    /**
     * It is called when user clicked on my favorite view
     */
    private fun clickedMyBookmarks() {

        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {
            ///startActivity(Intent(this@HomeActivity, BookMarkActivity::class.java))
            startActivity(
                Intent(this@HomeActivity, BookmarksAllCategory::class.java).putExtra(
                    Constants.App.FROM,
                    "ShowBackButton"
                )
            )

        } else {
            callForLogin()
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }
        if (AppController.mSocketManager != null) {
            AppController.mSocketManager!!.disconnect()
        }
        this.parent.onBackPressed()

        /*        val delayThread = Thread {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                this@HomeActivity.runOnUiThread(Runnable {  })
            }
            delayThread.start()*/

        // super.onBackPressed()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }
        callToAppStartService()
        if (AppController.mSocketManager != null) {
            AppController.mSocketManager!!.disconnect()
        }

    }


    /**
     * It is called when user clicked on side menu view
     */
    private fun clickedSideMenu() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    /**
     * It is called when user clicked on side menu view
     */
    private fun clickedCloseMenu() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }


    /**
     * It is called when user clicked on cart view
     */
    private fun clickedCart() {
        startActivity(Intent(this@HomeActivity, CartActivity::class.java))
    }

    /**
     * It is called when user clicked on my account view
     */
    private fun clickedMyAccount() {
        // startActivity(Intent(this@HomeActivity, MyAccountActivity::class.java))
        startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        //  clickedCloseMenu()

    }

    /**
     * It is called when user clicked on programs view
     */
    private fun clickedPrograms() {
        startActivity(Intent(this@HomeActivity, ProgramsActivity::class.java))
    }

    /**
     * It is called when user clicked on memberships view
     */
    private fun clickedMemberships() {
        startActivity(Intent(this@HomeActivity, MembershipsActivity::class.java))
    }

    /**
     * It is called when user clicked on journal view
     */
    private fun clickedJournal() {
        startActivity(Intent(this@HomeActivity, JournalListingActivity::class.java))
    }

    /**
     * It is called when user clicked on evaluation view
     */
    private fun clickedEvaluation() {
        startActivity(Intent(this@HomeActivity, SelfAssessmentActivity::class.java))
    }

    /**
     * It is called when user clicked on Faq view
     */
    private fun clickedFaq() {
        callToOpenWebViewActivity(webViewType = WebViewType.FAQ)
    }

    /**
     * It is called when user clicked on cart view
     */
    private fun clickedSuggestions() {
        startActivity(Intent(this@HomeActivity, SuggestionsActivity::class.java))
    }

    /**
     * It is called when user clicked on privacy policy view
     */
    private fun clickedPrivacyPolicy() {
        callToOpenWebViewActivity(webViewType = WebViewType.PRIVACY_POLICY)
    }

    /**
     * It is called when user clicked on terms & conditions view
     */
    private fun clickedTermsAndConditions() {
        callToOpenWebViewActivity(webViewType = WebViewType.TERMS_AND_CONDITIONS)
    }

    /**
     * It is called when user clicked on contact us view
     */
    private fun clickedContactUs() {
        startActivity(Intent(this@HomeActivity, ContactUsActivity::class.java))
    }

    /**
     * Here is call to open web url
     */
    private fun callToOpenWebViewActivity(webViewType: WebViewType) {
        val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
        intent.putExtra(Constants.App.WEBVIEW_TYPE, webViewType)
        startActivity(intent)
    }

    /**
     * It is called when user clicked on Logout view
     */
    private fun clickedLogout() {
        when (AppHelper.isUserLoggedIn()) {
            true -> callForLogout()
            else -> callForLogin()
        }
    }

    private fun callForLogout() {
        AppController.simpleExoplayer!!.stop()
//      AppController.simpleExoplayer!!.release()
        AppController.simpleExoplayer!!.playWhenReady = false
        // drawerLayout.closeDrawer(GravityCompat.START)
        showPositiveNegativeAlertDialog()

    }

    private fun callForLogin() {
        startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
    }

    fun showPositiveNegativeAlertDialog() {
        val positiveNegativeAlertDialog = PositiveNegativeAlertDialog(
            baseActivity = this@HomeActivity,
            requestCode = Constants.App.RequestCode.POSITIVE_NEGATIVE_ALERT,
            message = getString(R.string.are_sure_you_want_to_logout),
            negativeButtonText = getString(R.string.no),
            positiveButtonText = getString(R.string.yes),
            listener = this@HomeActivity
        )

        positiveNegativeAlertDialog.show()
    }


    /**
     * It is called when user clicked on yes button for logout
     */
    private fun clickedYesForLogout() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.hitApiForLogout(token = SharedPreferencesHelper.getAuthToken())

            presenter.apiToGetNotificationData(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                SharedPreferencesHelper.getAppID().toInt()
            )
        }
    }

    /**
     * It is called when user clicked on target image add view detail view
     */
    private fun clickedTargetImageAdd() {
        val intent = Intent(this@HomeActivity, MembershipsActivity::class.java)
//        intent.putExtra(Constants.App.PRODUCT_ID, summerProgramId.toString())
//        intent.putExtra(Constants.App.PRODUCT_ID, getString(R.string.win_your_summer_product_id))
//        intent.putExtra(Constants.App.IMAGE, productsList?.get(0)?.image)
        startActivity(intent)
    }

    /**
     * Here is updating the user login status
     */
    private fun updateLoginStatus() {
        when (AppHelper.isUserLoggedIn()) {
            true -> updateLogoutUI()
            else -> updateLoginUI()
        }
    }

    /**
     * Here is updating view if user is not logged in
     */
    private fun updateLoginUI() {
        tvLoginLogout.text = getString(R.string.login)
        ivLoginLogout.isSelected = false
        //ivProfile.setImageResource(R.drawable.ic_user_placeholder)
        //    tvUserName.setText(null)
        setUserProfile()

    }

    /**
     * Here is updating view if user is logged in
     */
    private fun updateLogoutUI() {
        runOnUiThread {
            tvLoginLogout.text = getString(R.string.logout)
            ivLoginLogout.isSelected = true
        }
    }

    private fun hitApiToGetProductWithProductId() {
        productRequestModel = ProductRequestModel().apply {
            categoryId = productCategoryId
            limit = Constants.App.Number.FIFTEEN
            page = ++currentPage
        }

        /*if (isInternetConnected(shouldShowToast = true)) {
            if (currentPage == 1) showProgressDialog("")

            presenter.apiToGetAllProducts(
                SharedPreferencesHelper.getAuthToken(),
                productRequestModel!!
            )

            addOnScrollListener()
        }*/
    }

    private fun addOnScrollListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            svProducts.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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
            }
        }
    }

    /**
     * Update count of card items in view
     */
    private fun updateCartCount(cartCount: String) {
        if (cartCount == getString(R.string.zero))
            tvCartCount.visibility = View.GONE
        else {
            tvCartCount.visibility = View.VISIBLE
            tvCartCount.text = cartCount
        }
    }

    private fun initViewPager(totalPages: Int) {
        generateViewPagerIndicators(totalPages)
        addPageChangeListener(totalPages)


    }

    private fun generateViewPagerIndicators(total: Int) {
        for (i in 0 until total) {
            val imageView = ImageView(this)
            val layoutParams = LinearLayout.LayoutParams(
                resources.getDimension(R.dimen._4sdp).toInt(),
                resources.getDimension(R.dimen._4sdp).toInt()
            )

            layoutParams.setMargins(
                resources.getDimension(R.dimen._4sdp).toInt(),
                0,
                0,
                0
            )
            imageView.layoutParams = layoutParams
            imageView.setImageResource(R.drawable.selector_circle_fill_white_gray)

            llIndicatorView.addView(imageView)

            if (i == 0)
                pageSelected(0, 1)
        }
    }

    /**
     * Page change Listeners
     */
    private fun addPageChangeListener(totalPages: Int) {
        viewPagerFeaturedProduct.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                pageSelected(position, totalPages)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun pageSelected(position: Int, totalPages: Int) {
        tvPagerName.text = featuredProductList!![position].productName

        for (i in 0 until totalPages)
            llIndicatorView.getChildAt(i)?.isSelected = i == position

        /*tvVideoCount.text =AppHelper.formatVideoCount(
                context = this,
                video = featuredProductList!![position].totalVideos)*/
    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLoginStatus(phaseVideoModel: PhaseVideoModel, position: Int) {
        when (AppHelper.isUserLoggedIn()) {
            true -> clickedLikeUnlike(phaseVideoModel = phaseVideoModel, position = position)

            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.HOME
                )
            }
        }
    }

    /**
     * It is called when user click on the like/unlike view.
     */
    private fun clickedLikeUnlike(phaseVideoModel: PhaseVideoModel, position: Int) {
        favVideoPosition = position

        val favoriteVideoRequestModel = FavoriteVideoRequestModel(
            productVideoId = phaseVideoModel.id.toInt(),
            action = phaseVideoModel.isMyFav!!
        )

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            /*  presenter.hitApiToLikeUnlikeVideo(
                  token = SharedPreferencesHelper.getAuthToken(),
                  favoriteVideoRequestModel = favoriteVideoRequestModel
              )*/
        }
    }

    private fun getFirstCategoryId(categoryList: ArrayList<ProductCategoryDataModel>) {
        val categoryModel = categoryList.elementAt(Constants.App.Number.ZERO)
        productCategoryId = categoryModel.id

        isCategorySelected = true
        updateCategoryIdInAdapter()

        currentPage = 0
        productsList?.clear()
        hitApiToGetProductWithProductId()

        showHideFeaturedProductsUI(categoryName = categoryModel.name)
    }

    private fun addDataToProductCategoryList(categoryList: ArrayList<ProductCategoryDataModel>?) {
        productCategoryList!!.clear()

        if (categoryList!!.isNotEmpty())
            productCategoryList!!.addAll(categoryList)

        productCategoryAdapter?.notifyDataSetChanged()
    }

    private fun addDataToFeaturedProductList(featuredList: ArrayList<FeaturedProductDetailResModel>?) {
        featuredProductList!!.clear()

        if (featuredList!!.isNotEmpty()) {
            featuredProductList!!.addAll(featuredList)
        }

        featuredProductAdapter = FeaturedProductAdapter(this@HomeActivity, featuredProductList!!)
        viewPagerFeaturedProduct.adapter = featuredProductAdapter
    }

    private fun addDataToProductList(productList: ArrayList<AllProductsDataModel>) {
        //   productsList!!.clear()
        productsList!!.addAll(productList)
        productsAdapter?.notifyDataSetChanged()
    }

    override fun onProductCategorySelected(position: Int, productId: Int, categoryName: String) {
        productCategoryId = productId.toString()
        updateCategoryIdInAdapter()

        isCategorySelected = true
        currentPage = 0
        productsList?.clear()

        showHideFeaturedProductsUI(categoryName = categoryName)

        startActivityForResult(
            Intent(this@HomeActivity, MenuDetailActivity::class.java)
                .putExtra("type", categoryName)
                .putExtra("productId", productId),
            Constants.App.RequestCode.categorySelectedRequestCode
        )

        /* val intent = Intent(this@HomeActivity, MenuDetailActivity::class.java)
         intent.putExtra("type", categoryName)
         intent.putExtra("productId", productId)
         startActivity(intent)*/

    }

    private fun updateCategoryIdInAdapter() {
        productCategoryAdapter?.updateCategoryId(productCategoryId)
        productCategoryAdapter?.notifyDataSetChanged()
    }

    /**
     * showing featured product UI  and Target product add when category is home
     */
    private fun showHideFeaturedProductsUI(categoryName: String) {
        if (categoryName == getString(R.string.home)) {
            clFeaturedProduct.visibility = View.VISIBLE
            //   clTargetProduct.visibility = View.VISIBLE
            clTargetProduct.visibility = View.GONE
        } else {
            //  clFeaturedProduct.visibility = View.GONE
            clFeaturedProduct.visibility = View.VISIBLE

            clTargetProduct.visibility = View.GONE
        }
    }

    override fun onListEmpty(isEmpty: Boolean) {

    }

    override fun onItemSelected(position: Int, productId: Int) {

    }

    override fun onDialogEventListener(
        dialogEventType: DialogEventType,
        requestCode: Int,
        model: Any?
    ) {
        when (dialogEventType) {
            DialogEventType.POSITIVE -> onDialogPositiveEvent(
                requestCode = requestCode,
                model = model
            )

            else ->
                super.onDialogEventListener(
                    dialogEventType = dialogEventType,
                    requestCode = requestCode,
                    model = model
                )
        }
    }

    override fun onDialogPositiveEvent(requestCode: Int, model: Any?) {
        Log.d(TAG, "onDialogPositiveEvent: requestCode- $requestCode")

        when (requestCode) {
            Constants.App.RequestCode.POSITIVE_NEGATIVE_ALERT ->
                clickedYesForLogout()
            Constants.App.RequestCode.DELETE_ACCOUNT -> clickedYesForDeleteAccount()
            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
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
                checkForLoginStatus(
                    phaseVideoModel = model as PhaseVideoModel,
                    position = position
                )

            ItemClickType.OPEN_NOTIFICATION -> callNotificationDetailApi(model as NotificationData)

            else ->
                Log.e(TAG, "onRecyclerViewItemClick: itemClickType- INVALID")
        }
    }


    /**
     * It is called when user clicked on yes button for delete account
     */
    private fun clickedYesForDeleteAccount() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.hitApiForDeleteAccount("Bearer "+SharedPreferencesHelper.getAuthToken(),SharedPreferencesHelper.getAppID())
        }
    }

    private fun callNotificationDetailApi(notificationData: NotificationData) {
        if (isInternetConnected(shouldShowToast = true)) {

            if (SharedPreferencesHelper.getAuthToken().isNotEmpty()) {
                showProgressDialog("")
                presenter.apiToGetNotificationDetail(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    id = notificationData.id!!
                )
            }
        }

    }

    /**
     * When successful response or data retrieved from master video api
     *
     * @param response success response
     */
    override fun onMasteredVideoSuccess(response: CommonResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()

                    showToast(response.message)
                }
            }
        }
    }

    /**
     * When successful response or data retrieved from like/unlike api
     *
     * @param response success response
     */
    override fun onLikeUnlikeVideoSuccess(response: CommonResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                }
            }
        }
    }

    /**
     * Success Response from api card count
     *
     * @param cartCount count of card items
     */
    override fun onGetCartCount(cartCount: String) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    //updateCartCount(cartCount)
                }
            }
        }
    }

    /**
     * When success response or data retrieved from api product categories
     *
     * @param response success response
     */
    override fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()

                    addDataToProductCategoryList(categoryList = response.data!!.data!!)

                    /*if (!isCategorySelected)
                        getFirstCategoryId(categoryList = response.data)
                    else hitApiToGetProductWithProductId()*/
                }
            }
        }
    }


    /**
     * When success response or data retrieved from api featured product
     *
     * @param response success response
     */
    override fun onFeaturedProductResponseSuccess(response: FeaturedProductResModel) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    Log.d(TAG, "onFeaturedProductResponseSuccess --- $response")
                    hideProgressDialog()

                    llIndicatorView.removeAllViews()
                    refreshLayout.isRefreshing = false

                    addDataToFeaturedProductList(featuredList = response.data!!.data)
                    initViewPager(response.data.data!!.size)
                }
            }
        }
    }

    /**
     * When success response or data retrieved from api all products
     *
     * @param response success response
     */
    override fun onAllProductResponseSuccess(response: AllProductsResponseModel) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    currentPage = response.currentPage

                    if (response.nextPageUrl != null)
                        loadMore = true

                    addDataToProductList(productList = response.data)

                    pbLoadMore.visibility = View.GONE
                }
            }
        }
    }

    /**
     * When success response or data retrieved from api App Content
     *
     * @param response success response
     */
    override fun onAppContentResponseSuccess(response: AppContentResponseModel) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    response.data?.appConfiguration?.apply {
                        for (items in this) {
                            if (items.type == AppConst.KEY.SUMMER_PROGRAM_ID) {
                                summerProgramId = items.value
                                Log.d(
                                    TAG,
                                    "onAppContentResponseSuccess: summerProgramId- $summerProgramId"
                                )
                            }
                        }
                    }


                }
            }
        }
    }

    override fun onNotificationResponseSuccess(response: NotificationResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    isResume = false
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false
                    notificationList!!.clear()
                    notificationList!!.addAll(response.data!!)

                    if (!SharedPreferencesHelper.getNotificationId().isNullOrEmpty()) {

                            for (i in 0 until response.data.size) {
                                if (SharedPreferencesHelper.getNotificationId()
                                        .equals(response.data.get(i).id!!.toString())
                                ) {
                                    callNotificationDetailApi(response.data.get(i))
                                    SharedPreferencesHelper.saveNotificationId("")
                                }
                            }
                        }

                    newNotificationAdapter!!.notifyDataSetChanged()


                }
            }
        }

    }

    override fun onNotificationDetailResponseSuccess(response: NotificationDetailResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {

                    hideProgressDialog()

                    if (response.data!!.file!!.type.equals("video")) {

                        startActivity(
                            Intent(this@HomeActivity, VideoPlayerActivity::class.java).putExtra(
                                Constants.App.VIDEO_LINK, response.data.file!!.tempPath
                            ).putExtra(
                                Constants.App.VIDEO_THUMB_PATH_LINK, response.data.file.thumbpath
                            ).putExtra(
                                Constants.App.POSITION, 0
                            ).putExtra(
                                Constants.App.VIDEO_ID, response.data.file.id
                            ).putExtra(
                                Constants.App.VIDEO_ACTIVITY_NAME, response.data.file.title
                            )
                        )

                    } else if (response.data.file!!.type.equals("audio")) {

                        startActivity(
                            Intent(this@HomeActivity, AudioPlayActivity::class.java).putExtra(
                                Constants.App.AUDIO_LINK, response.data.file.tempPath
                            ).putExtra(
                                Constants.App.POSITION, 0
                            ).putExtra(
                                Constants.App.AUDIO_ID, response.data.file.id
                            ).putExtra(
                                Constants.App.AUDIO_TITLE, response.data.file.title
                            ).putExtra(
                                Constants.App.AUDIO_DURATION, response.data.file.duration
                            ).putExtra(
                                Constants.App.AUDIO_ACTIVITY_NAME, response.data.file.product!!.name
                            ).putExtra(
                                Constants.App.IS_FAVROITE, response.data.file.isFavourite
                            ).putExtra(
                                Constants.App.AUDIO_THUMPATH, response.data.file.thumbpath
                            ).putExtra(
                                Constants.App.SCREEN_TYPE, "Home"
                            )
                        )
                        // SharedPreferencesHelper.saveAudioId( response.data.file.id.toString())
                        AppController.fromHome = "Home"
                        if (AppController.audioList != null) {
                            AppController.audioList!!.clear()
                        }


                    } else if (response.data.file.type.equals("pdf")) {
                        startActivity(
                            Intent(this@HomeActivity, OpenPdfFileActivity::class.java).putExtra(
                                Constants.App.PDF_LINK,
                                response.data.file.pdf
                            ).putExtra(Constants.App.PDF_NAME, response.data.file.title)
                        )

                    }else if(response.data.file.type.equals("image")){
                        //
                        startActivity(
                            Intent(this@HomeActivity, MenuDetailActivity::class.java).putExtra(
                                "type",
                                "Images"
                            )
                                .putExtra("id",  response.data.file.id)
                        )
                        //
                    }

                }
            }
        }
    }

    override fun onVideoViewResponseSuccess(response: VideoViewResponseModel) {

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    Log.d(TAG, "onVideoViewResponseSuccess" + response.data)
                    tvVideoNumbers.text = "" + response.data

                }
            }
        }
    }

    override fun onAudioViewResponseSuccess(response: VideoViewResponseModel) {

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    Log.d(TAG, "onAudioViewResponseSuccess" + response.data)
                    tvAudioCounts.text = "" + response.data

                }
            }
        }
    }

    override fun onTotalPerformanceAppResponseSuccess(response: VideoViewResponseModel) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    Log.d(TAG, "onTotalPerformanceAppResponseSuccess" + response.data)
                    tvTotalHoursCounts.text = "" + response.data

                }
            }
        }
    }

    override fun onTodayQuoteAppResponseSuccess(response: TodayQuoteResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    Log.d(TAG, "onTodayQuoteAppResponseSuccess" + response.data)
                    todayQuoteList!!.clear()
                    todayQuoteList!!.addAll(response.data!!)

                    if (todayQuoteList!!.size > 0) {

                        tvQuoteTitle.visibility = View.VISIBLE
                        tvQuoteTitle.setText(R.string.todays_quote)

                    } else {

                        tvQuoteTitle.visibility = View.GONE
                    }

                    quoteAdapter!!.notifyDataSetChanged()


                }
            }
        }
    }

    override fun onSpecialContentAppResponseSuccess(response: SpecialContentResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                    Log.d(TAG, "SpecialContentResponse" + response.data)

                    if (response.data!!.size > 0) {

                        tvContentFirst.visibility = View.VISIBLE
                        clContentFirst.visibility = View.VISIBLE

                        setSpecialContentFirstInViews(response)
                        setSpecialContentSecondInViews(response)


                    } else {
                        tvContentFirst.visibility = View.GONE
                        clContentFirst.visibility = View.GONE

                        tvContentSceond.visibility = View.GONE
                        clContentSecond.visibility = View.GONE

                    }

                }
            }
        }
    }

    override fun onDeleteAccountResponseSuccess(response: CommonResponse) {
        SharedPreferencesHelper.removeAllDetails()
        databaseHelper.removeAllData()

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)

                    SharedPreferencesManager.with(context = AppController.mInstance!!)!!.apply {
                        edit().apply {
                            remove(SharedPrefConst.KEY.REMEMBER_ME_MODEL)
                            apply()
                        }
                    }

                    clearLocalDB()
                }
            }
        }
    }

    /**
     * set data in SpecialContent Views
     */
    private fun setSpecialContentFirstInViews(response: SpecialContentResponse) {
        if (response.data!!.size > 0) {
            clContentFirst.visibility =View.VISIBLE
            viewContentFirst.visibility =View.VISIBLE

        if (response.data.get(0).content!=null) {

            if (response.data!!.get(0).content!!.heading != null) {
                tvContentFirst.visibility =View.VISIBLE
                tvContentFirst.text = response.data.get(0).content!!.heading
            }else{
               tvContentFirst.visibility =View.GONE
            }

        }else{
            tvContentFirst.visibility =View.GONE

        }
        }else{
            clContentFirst.visibility =View.GONE
            viewContentFirst.visibility =View.GONE

        }



        if (response.data.get(0).title != null && response.data.get(0).title!!.isNotEmpty()) {
            tvContentFirstTitle.visibility =View.VISIBLE

            tvContentFirstTitle.text = response.data.get(0).title
        }else{

            tvContentFirstTitle.visibility =View.GONE
        }

        ivContentFirstPlayerButton.visibility = View.GONE
        //Here call SpecialContent Cover Image method

        setSpecialContentCoverImage(
            response.data.get(0).coverImage,
            response.data.get(0).fileType,
            ivContentFirst,
            pbContentFirst
        )

        if (response.data.get(0).duration != null) {
            ivContentFirstPlayerButton.visibility = View.VISIBLE
            tvContentFirstPlayerDuration.text = "Start Listening - " + AppHelper.getTimeFromat(
                response.data.get(0).duration!!
            ) + " min"
        }

        clContentFirst.setOnClickListener {
            if (response.data.get(0).fileType.equals("video")) {

                if (response.data.get(0).file != null) {
                    startActivity(
                        Intent(this@HomeActivity, VideoPlayerActivity::class.java).putExtra(
                            Constants.App.VIDEO_LINK, response.data.get(0).file
                        ).putExtra(
                            Constants.App.VIDEO_THUMB_PATH_LINK,
                            response.data.get(0).coverImage
                        ).putExtra(
                            Constants.App.VIDEO_ACTIVITY_NAME, response.data.get(0).title
                        ).putExtra(
                            Constants.App.VIDEO_ID, 0
                        ).putExtra(
                            Constants.App.SPECIAL_CONTENT, "specialContent"
                        )
                    )

                }

            } else if (response.data.get(0).fileType.equals("audio")) {
                startActivity(
                    Intent(
                        this@HomeActivity, AudioPlayActivity::class.java
                    ).putExtra(
                        Constants.App.AUDIO_LINK, response.data.get(0).file
                    ).putExtra(
                        Constants.App.AUDIO_TITLE, response.data.get(0).title
                    ).putExtra(
                        Constants.App.AUDIO_DURATION, response.data.get(0).duration
                    ).putExtra(
                        Constants.App.AUDIO_ACTIVITY_NAME, "MEDITATION  UPDATE"
                    ).putExtra(
                        Constants.App.AUDIO_ID, 0
                    ).putExtra(
                        Constants.App.AUDIO_THUMPATH, response.data.get(0).coverImage
                    ).putExtra(
                        Constants.App.SPECIAL_CONTENT, "specialContent"
                    ).putExtra(
                        Constants.App.SCREEN_TYPE, "Home"
                    )
                )


                AppController.fromHome = "Home"
                if (AppController.audioList != null) {
                    AppController.audioList!!.clear()
                }

            }
        }


    }


    /**
     * set data in SpecialContent Views
     */
    private fun setSpecialContentSecondInViews(response: SpecialContentResponse) {
        if (response.data!!.size == 2) {
            clContentSecond.visibility = View.VISIBLE
            viewContentSecond.visibility = View.VISIBLE

            if (response.data.get(1).content != null) {

                if (response.data.get(1).content!!.heading != null) {

                    tvContentSceond.visibility = View.VISIBLE

                    tvContentSceond.text = response.data.get(1).content!!.heading
                }else{
                    tvContentSceond.visibility = View.GONE

                }
            } else {
                tvContentSceond.visibility = View.GONE

            }
        }else{
            clContentSecond.visibility = View.GONE
            viewContentSecond.visibility = View.GONE

        }

        if (response.data.size > 1) {

          //  tvContentSceond.visibility = View.VISIBLE
            clContentSecond.visibility = View.VISIBLE



            if (response.data.get(1).title != null && response.data.get(1).title!!.isNotEmpty()) {
                tvContentSceTitle.visibility =View.VISIBLE
                tvContentSceTitle.text = response.data.get(1).title
            }else{
                tvContentSceTitle.visibility =View.GONE
            }

            ivContentScePlayerButton.visibility = View.GONE

            //Here call SpecialContent Cover Image method
            setSpecialContentCoverImage(
                response.data.get(1).coverImage,
                response.data.get(1).fileType,
                ivContentSecond,
                pbContentSecond
            )

            if (response.data.get(1).duration != null) {
                ivContentScePlayerButton.visibility = View.VISIBLE
                tvContentScePlayerDuration.text = "Start Listening - " + AppHelper.getTimeFromat(
                    response.data.get(1).duration!!
                ) + " min"
            }

            clContentSecond.setOnClickListener {
                if (response.data.get(1).fileType.equals("video")) {
                    if (response.data.get(1).file != null) {
                        startActivity(
                            Intent(this@HomeActivity, VideoPlayerActivity::class.java).putExtra(
                                Constants.App.VIDEO_LINK, response.data.get(1).file
                            ).putExtra(
                                Constants.App.VIDEO_THUMB_PATH_LINK,
                                response.data.get(1).coverImage
                            ).putExtra(
                                Constants.App.POSITION, 0
                            ).putExtra(
                                Constants.App.VIDEO_ACTIVITY_NAME, response.data.get(1).title
                            ).putExtra(
                                Constants.App.VIDEO_ID, 0
                            ).putExtra(
                                Constants.App.SPECIAL_CONTENT, "specialContent"
                            )
                        )
                    }
                } else if (response.data.get(1).fileType.equals("audio")) {
                    startActivity(
                        Intent(this@HomeActivity, AudioPlayActivity::class.java).putExtra(
                            Constants.App.AUDIO_LINK, response.data.get(1).file
                        ).putExtra(
                            Constants.App.AUDIO_TITLE, response.data.get(1).title
                        ).putExtra(
                            Constants.App.AUDIO_DURATION, response.data.get(1).duration
                        ).putExtra(
                            Constants.App.AUDIO_ACTIVITY_NAME, "TODAY’S FOCUS"
                        ).putExtra(
                            Constants.App.AUDIO_ID, 0
                        ).putExtra(
                            Constants.App.AUDIO_THUMPATH, response.data.get(1).coverImage
                        ).putExtra(
                            Constants.App.SPECIAL_CONTENT, "specialContent"
                        ).putExtra(
                            Constants.App.SCREEN_TYPE, "Home"
                        )
                    )

                    AppController.fromHome = "Home"

                    if (AppController.audioList != null) {
                        AppController.audioList!!.clear()
                    }

                }
            }
        } else {

            tvContentSceond.visibility = View.GONE
            clContentSecond.visibility = View.GONE


        }

    }

    /**
     * Updating selected/captured image into view
     *
     */
    private fun setSpecialContentCoverImage(
        imageUrl: String,
        fileType: String,
        imageView: ImageView,
        progressBar: ProgressBar
    ) {
        progressBar.visibility = View.VISIBLE

        var drawableIcon = 0
        if (fileType.contains("video")) {
            drawableIcon = R.drawable.ic_content_vedio
        } else if (fileType.contains("audio")) {
            //   drawableIcon = R.drawable.ic_audio
            drawableIcon = R.drawable.ic_content_audio
        }

        Picasso.get()
            .load(ApiClient.BASE_URL_MEDIA + imageUrl).placeholder(drawableIcon)
            .centerCrop().fit()
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    progressBar.visibility = View.GONE

                }

                override fun onError(e: java.lang.Exception?) {
                    progressBar.visibility = View.GONE
                    imageView.setImageDrawable(baseContext.resources.getDrawable(drawableIcon))

                }

            })
    }

    /**
     * When successful response or data retrieved from Logout Api
     *
     * @param response is successful response from Api
     */
    override fun onLogoutResponseSuccess(response: CommonResponse) {
        SharedPreferencesHelper.removeAllDetails()
        databaseHelper.removeAllData()

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)
                    //updateLoginStatus()

                    // startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
                    //  finish()

                    clearLocalDB()

                }
            }
        }
    }


    fun clearLocalDB() {

        if (!isFinishing && dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
        onSessionExpiredClearLocalDB(databaseHelper)
        // updateLoginStatus()

        startActivity(
            Intent(
                this@HomeActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }

    /**
     * When error occurred in success response
     *
     * @param errorResponse error response
     */
    override fun onResponseFailure(errorResponse: ErrorResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {

                    refreshLayout.isRefreshing = false
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onResFailure(message: String) {

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {

                    refreshLayout.isRefreshing = false
                    hideProgressDialog()

                    if (message.equals("Unauthorized")) {
                        /*showSestionExpiredtDialog(
                            "You are logged in with another device to continue with this device you need to login again",
                            this,
                            this
                        )*/
                        if (dialogSessionExpired == null) {

                            dialogSessionExpired = DialogSessionExpired(
                                listener = this,
                                message = getString(R.string.session_expired),
                                context = this,

                                )
                        }

                        if (dialogSessionExpired!!.isShowing && dialogSessionExpired != null) {
                            dialogSessionExpired!!.dismiss()
                        }

                        try {


                            if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }

                    } else {

                        Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
                    }


                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.HOME -> init()
                    Constants.App.RequestCode.categorySelectedRequestCode -> {
                        productCategoryId = ""
                        updateCategoryIdInAdapter()

                    }
                }
            }
        }
    }

    override fun onResponseSuccess(response: LetsGoResponse) {
        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {
                    hideProgressDialog()

                    response.data?.apply {
                        letsGoDataResponse = this
                        updateLogoInUI()
                    }
                }
            }
        }
    }

    override fun onAppTimeResponseSuccess(commonResponse: CommonResponse) {

    }

    override fun onFailureResponse(message: String) {

        if (this@HomeActivity.baseContext != null) {
            runOnUiThread {
                if (this@HomeActivity.baseContext != null) {

                    hideProgressDialog()
                    refreshLayout.isRefreshing = false

                }
            }
        }
    }

    private fun updateLogoInUI() {
        pbHome.visibility = View.VISIBLE
        if (letsGoDataResponse!!.logoImage != null) {
            SharedPreferencesHelper.storeLogoImage(letsGoDataResponse!!.logoImage!!)
        }
        Picasso.get()
            .load(ApiClient.BASE_URL_PROFILE + letsGoDataResponse!!.logoImage)
            .placeholder(R.drawable.app_logo_placeholder)
            //   .into(ivOrganisationLogo, object : Callback {
            .into(ivHomeLogo, object : Callback {
                override fun onSuccess() {
                    pbHome.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    pbHome.visibility = View.GONE
                }
            })

    }

    /**
     * here call save app start time in local DB
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveAppStartTime() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(
                TAG, "saveStartTime: " + convertToCustomFormat(getCurrentDateTime().toString())
            )
            //   SharedPreferencesHelper.storeAppStartTime(convertToCustomFormat(getCurrentDateTime().toString()))

        }

    }

    /**
     * here call App end time in local DB
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEndTime() {

        Log.d(TAG, "saveEndTime: " + convertToCustomFormat(getCurrentDateTime().toString()))
        SharedPreferencesHelper.storeAppEndtime(convertToCustomFormat(getCurrentDateTime().toString()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()

        saveEndTime()
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    //Wed Aug 30 17:13:31 GMT+05:30 2023
    private fun convertToCustomFormat(dateStr: String?): String {


        val cal = Calendar.getInstance()
        val tz = cal.timeZone
        Log.d("Time zone", "=" + tz.displayName)

   //     val utc = TimeZone.getTimeZone("UTC")
        val utc = TimeZone.getDefault()
        Log.d("Time zone", "getDefault= " + utc.displayName)

        val sourceFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        val destFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
            SimpleDateFormat("YYYY-MM-dd")
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        sourceFormat.timeZone = utc
        val convertedDate = sourceFormat.parse(dateStr)
        Log.d("Time zone", "convertedDate =" + convertedDate)
        Log.d("Time zone", "convertedDate =" + destFormat.format(convertedDate))

        return destFormat.format(convertedDate)
    }


    fun callPerformanceThisWeekApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            if (!refreshLayout.isRefreshing && !isResume) {
                showProgressDialog("Please wait...")
            }

            val totalHourInAppRequestModel = TotalHourInAppRequestModel()
            totalHourInAppRequestModel.app_id = "com.fivestarmind.android"
            //
            /* presenter.apiToGetTotalhourInApp(
                 "Bearer " + SharedPreferencesHelper.getAuthToken(),
                 totalHourInAppRequestModel,SharedPreferencesHelper.getAppID().toInt()
             )
 */
            presenter.apiToGetVideoView(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt()
            )
            presenter.apiToGetAudioView(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt()
            )
        }

    }

    fun callSpiner() {

        // Create the instance of ArrayAdapter
        // having the list of courses
        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )

        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spinerFilter.adapter = ad
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(
            applicationContext,
            courses[position],
            Toast.LENGTH_LONG
        )
            .show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    private fun initList() {
        filterItems = ArrayList()
        filterItems!!.add(FilterItemModel("Discipline"))
        filterItems!!.add(FilterItemModel("Prims Algorithm"))
        filterItems!!.add(FilterItemModel("Quick Sort"))
        filterItems!!.add(FilterItemModel("Merge Sort"))
        filterItems!!.add(FilterItemModel("Heap Sort"))
        filterItems!!.add(FilterItemModel("Prims Algorithm"))
        filterItems!!.add(FilterItemModel("Kruskal Algorithm"))
        filterItems!!.add(FilterItemModel("Rabin Karp"))
        filterItems!!.add(FilterItemModel("Binary Search"))
    }

    override fun onDialogSesstionExpiredListener(
        dialogEventType: DialogEventType
    ) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }

    }


}
