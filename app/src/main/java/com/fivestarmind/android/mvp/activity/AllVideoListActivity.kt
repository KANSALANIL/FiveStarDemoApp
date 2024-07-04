package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.AllVideoListAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.ProductRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.Product
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.mvp.model.response.SeeAllResModel
import com.fivestarmind.android.mvp.model.response.VideoViewModel
import com.fivestarmind.android.mvp.presenter.SeeAllCategoryListDataPresenter
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import kotlinx.android.synthetic.main.activity_all_video_list.allAudioPlayerView
import kotlinx.android.synthetic.main.activity_all_video_list.ivBack
import kotlinx.android.synthetic.main.activity_all_video_list.ivBookMarks
import kotlinx.android.synthetic.main.activity_all_video_list.pbLoadMore
import kotlinx.android.synthetic.main.activity_all_video_list.refreshLayoutAllVideos
import kotlinx.android.synthetic.main.activity_all_video_list.rvAllVideos
import kotlinx.android.synthetic.main.activity_all_video_list.tvAllVideos
import kotlinx.android.synthetic.main.activity_all_video_list.tvItemCount
import kotlinx.android.synthetic.main.activity_all_video_list.tvNoDataFound
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class AllVideoListActivity : BaseActivity(), RecyclerViewItemListener,
    DialogSesstionExpiredListener,
    SeeAllCategoryListDataPresenter.ResponseCallBack {
    private var linearLayoutManager: LinearLayoutManager? = null
    private var allVideoListAdapter: AllVideoListAdapter? = null
    private var allDataItemList: ArrayList<SeeAllDataItem>? = null
    private lateinit var seeAllCategoryListDataPresenter: SeeAllCategoryListDataPresenter
    private var productId: Int? = null
    private var productName: String? = null
    var videoPlayRequestCode: Int = 3
    var audioPlayRequestCode: Int = 4
    var videoPlayPercentage: Int = 0
    var videoDuration: Int = 0
    private var addFavouriteRequest = AddFavouriteRequestModel()

    var whenCallAudioViewsApi: Boolean = false

    private var isLoading = false
    private var currentPage = 1
    private var lastloadpage = 0
    private var total = 0
    private var isLastPage: Boolean = false

    private var tagId: Int = 0
    private var categoryId: Int = 0
    private var categoryName: String = ""
    private var tagName: String = ""
    private var playingSongAudioId: String = ""
    private var saveAudioDuration: Int = 0
    private var categoryItemCount: String = ""

    var mAudioFilesItem: SeeAllDataItem? = null
    var product: Product? = null
    var mAudioList: ArrayList<SeeAllDataItem>? = null
    var audioIndex = 0
    var showProgress: Boolean? = false


    // private var pageSize = Constants.App.Number.TEN
    private var productRequestModel: ProductRequestModel? = null
    var videoDurationRequestModel: VideoDurationRequestModel? = null
    private var databaseHelper = DatabaseHelper(this)

    private var callPagination: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_video_list)
        initUI()

        initPresenter()
        getDataFromIntent()
        //setToolbarTitle()
        clickListener()
        initRecyclerView()
        //initializePlayer()

        if(AppController.simpleExoplayer!!.isPlaying){
            AppController.simpleExoplayer!!.pause()
        }

       // initializeMiniAudioPlayer(allAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)

        //audioPlayerListner()


    }

    override fun onResume() {
        super.onResume()

        currentPage = 1
        callPagination = false

        if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {

            callSeeAllCategoryListApi(productId!!)

        } else {
            callParticularCategoryApi(tagId, categoryId)

        }

        initializeMiniAudioPlayer(allAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)

    }


    private fun initUI() {
        refreshLayoutAllVideos.setOnRefreshListener(refresh)
        refreshLayoutAllVideos.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

    }

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        //   initializePlayer()
        initializeMiniAudioPlayer(allAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)

        if (!allDataItemList?.size?.equals(total)!! == true) {
            refreshLayoutAllVideos.isRefreshing = true

            allDataItemList!!.clear()
            currentPage = 1
            if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {

                callSeeAllCategoryListApi(productId!!)
            } else {
                callParticularCategoryApi(tagId, categoryId)
            }
        } else {
            refreshLayoutAllVideos.isRefreshing = false
        }


    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        allDataItemList = ArrayList()
        mAudioList = ArrayList()
        seeAllCategoryListDataPresenter = SeeAllCategoryListDataPresenter(this)

    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {

        if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {
            if (intent.hasExtra(Constants.App.PRODUCT_ID)) {
                productId = intent.getIntExtra(Constants.App.PRODUCT_ID, 0)
                productName = intent.getStringExtra(Constants.App.PRODUCT_NAME)


                // here call See All Category List Api
                // callSeeAllCategoryListApi(productId!!)
            }
        } else {

            if (intent.hasExtra(Constants.App.CATEGORY_ID)) {

                tagId = intent.getIntExtra(Constants.App.TAG_ID, 0)
                categoryId = intent.getIntExtra(Constants.App.CATEGORY_ID, 0)
                categoryName = intent.getStringExtra(Constants.App.CATEGORY_NAME)!!
                tagName = intent.getStringExtra(Constants.App.TGA_NAME)!!
                categoryItemCount = intent.getStringExtra(Constants.App.CATEGORY_ITEMS_COUNT)!!

                // here call See All Category List Api

                //  callParticularCategoryApi(tagId, categoryId)

            }
        }


    }

    private fun callSeeAllCategoryListApi(productId: Int) {

        if (pbLoadMore.isVisible) {
            hideProgressDialog()

        } else {
            runOnUiThread {


                if (AllVideoListActivity@ this != null && !AllVideoListActivity@ this.isFinishing) {
                    try {

                        if (showProgress!!) {

                            showProgress = false

                        } else {
                            showProgressDialog("")

                        }


                    } catch (e: WindowManager.BadTokenException) {
                        Log.e("WindowManagerBad ", e.toString())
                    }

                }
            }
        }

        seeAllCategoryListDataPresenter.hitSeeAllCategoryDataList(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            productId = productId,
            pageNumber = currentPage,
            pageSize = Constants.App.Number.TEN
        )

        addScrollListener(productId)
    }


    private fun callParticularCategoryApi(tagId: Int, categoryId: Int) {
        if (pbLoadMore.isVisible) {
            hideProgressDialog()

        } else {
            runOnUiThread {


                if (AllVideoListActivity@ this != null && !AllVideoListActivity@ this.isFinishing) {
                    try {

                        if (showProgress!!) {

                            showProgress = false

                        } else {
                            showProgressDialog("")

                        }


                    } catch (e: WindowManager.BadTokenException) {
                        Log.e("WindowManagerBad ", e.toString())
                    }

                }
            }
        }

        seeAllCategoryListDataPresenter.hitSeeParticularCategoryDataList(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            tagId = tagId,
            categoryId = categoryId,
            pageNumber = currentPage,
            pageSize = Constants.App.Number.TEN

        )

        addScrollListener(0)
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {

            //  tvTitle.text = getString(R.string.mental_minutes)
            if(allDataItemList!!.size>0) {
                tvTitle.text = allDataItemList!!.get(0).product!!.name
            }else{
                tvTitle.text = productName

            }
        } else {
            tvTitle.text = categoryName

        }

    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        // rvAllVideos.setNestedScrollingEnabled(false);

        allVideoListAdapter = AllVideoListAdapter(this, allDataItemList, this)
        /* linearLayoutManager = LinearLayoutManager(this)
         rvAllVideos.layoutManager = linearLayoutManager*/

        rvAllVideos.apply {
            //   layoutManager = linearLayoutManager
            adapter = allVideoListAdapter
        }
    }

    /**
     * Click event for views
     */
    private fun clickListener() {
        ivBack.setOnClickListener(clickListener)
        ivBookMarks.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> onBackPressed()
                R.id.ivBookMarks -> gotoBookMarkActivity()
            }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun gotoBookMarkActivity() {

        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {
            //  startActivity(Intent(this@AllVideoListActivity, BookMarkActivity::class.java))
            startActivity(Intent(this@AllVideoListActivity, BookmarksAllCategory::class.java))
            finish()
        } else {

            callForLogin()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            TAG,
            "onActivityResult" + intent.getStringExtra(Constants.App.FROM).to(
                videoDuration
            )
        )
        /*currentPage = 1
        if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {

            callSeeAllCategoryListApi(productId!!)
        } else {
            callParticularCategoryApi(tagId, categoryId)
        }*/

        /*   if (requestCode == videoPlayRequestCode) {
               videoDuration = data!!.getIntExtra("videoDuration", 0)
               val position = data.getIntExtra("videoPosition", 0)

               Log.d(TAG, "onActivityResult: videoPosition " + position)
               Log.d(TAG, "onActivityResult: videoDuration " + videoDuration)


               Log.d(
                   TAG,
                   "onActivityResult: ConvertSecondToHHMMString " + ConvertSecondToHHMMString(
                       videoDuration
                   )
               )


               val seeAllDataItem = SeeAllDataItem()

               // here add adapter model data into list
               seeAllDataItem.image = allDataItemList!!.get(position).image
               seeAllDataItem.thumbnail = allDataItemList!!.get(position).thumbnail
               seeAllDataItem.featured = allDataItemList!!.get(position).featured
               seeAllDataItem.description = allDataItemList!!.get(position).description
               seeAllDataItem.title = allDataItemList!!.get(position).title
               seeAllDataItem.type = allDataItemList!!.get(position).type
               seeAllDataItem.duration = allDataItemList!!.get(position).duration
               seeAllDataItem.path = allDataItemList!!.get(position).path
               seeAllDataItem.pdf = allDataItemList!!.get(position).pdf
               seeAllDataItem.thumbpath = allDataItemList!!.get(position).thumbpath
               seeAllDataItem.productId = allDataItemList!!.get(position).productId
               seeAllDataItem.id = allDataItemList!!.get(position).id
               seeAllDataItem.tempPath = allDataItemList!!.get(position).tempPath
               seeAllDataItem.status = allDataItemList!!.get(position).status
               seeAllDataItem.isFavourite = allDataItemList!!.get(position).isFavourite

               // model.product!!.name

               if (allDataItemList!!.get(position).product != null) {
                   val product = Product()
                   product.name = allDataItemList!!.get(position).product!!.name
                   seeAllDataItem.product = product
               }
               if (allDataItemList!!.get(position).videoView != null) {

                   val videoViewModel = VideoViewModel()
                   videoViewModel.duration = ConvertSecondToHHMMString(videoDuration)


                   allDataItemList!!.get(position).videoView!!.duration = videoViewModel.duration
                   seeAllDataItem.videoView = allDataItemList!!.get(position).videoView!!

               }

               allDataItemList!!.set(position, seeAllDataItem)
               allVideoListAdapter!!.notifyDataSetChanged()

           }
           else if (requestCode == audioPlayRequestCode) {
               videoDuration = data!!.getIntExtra("audioDuration", 0)
               val position = data.getIntExtra("audioPosition", 0)

               Log.d(TAG, "onActivityResult: videoPosition " + position)
               Log.d(TAG, "onActivityResult: videoDuration " + videoDuration)


               Log.d(
                   TAG,
                   "onActivityResult: ConvertSecondToHHMMString " + ConvertSecondToHHMMString(
                       videoDuration
                   )
               )


               val seeAllDataItem = SeeAllDataItem()

               // here add adapter model data into list
               seeAllDataItem.image = allDataItemList!!.get(position).image
               seeAllDataItem.thumbnail = allDataItemList!!.get(position).thumbnail
               seeAllDataItem.featured = allDataItemList!!.get(position).featured
               seeAllDataItem.description = allDataItemList!!.get(position).description
               seeAllDataItem.title = allDataItemList!!.get(position).title
               seeAllDataItem.type = allDataItemList!!.get(position).type
               if (AppController.simpleExoplayer!!.playbackState==4) {
                   allDataItemList!!.get(position).duration = AppController.simpleExoplayer!!.duration.toString()
                   seeAllDataItem.duration = allDataItemList!!.get(position).duration

               }
               else {
                   seeAllDataItem.duration = allDataItemList!!.get(position).duration
               }
               seeAllDataItem.path = allDataItemList!!.get(position).path
               seeAllDataItem.pdf = allDataItemList!!.get(position).pdf
               seeAllDataItem.thumbpath = allDataItemList!!.get(position).thumbpath
               seeAllDataItem.productId = allDataItemList!!.get(position).productId
               seeAllDataItem.id = allDataItemList!!.get(position).id
               seeAllDataItem.tempPath = allDataItemList!!.get(position).tempPath
               seeAllDataItem.status = allDataItemList!!.get(position).status
               seeAllDataItem.isFavourite = allDataItemList!!.get(position).isFavourite

               if (allDataItemList!!.get(position).product != null) {
                   val product = Product()
                   product.name = allDataItemList!!.get(position).product!!.name
                   seeAllDataItem.product = product
               }


               if (allDataItemList!!.get(position).videoView != null) {

                   val videoViewModel = VideoViewModel()
                   videoViewModel.duration = ConvertSecondToHHMMString(videoDuration)


                   allDataItemList!!.get(position).videoView!!.duration = videoViewModel.duration
                   seeAllDataItem.videoView = allDataItemList!!.get(position).videoView!!

               }

               allDataItemList!!.set(position, seeAllDataItem)
               allVideoListAdapter!!.notifyDataSetChanged()

           }*/
    }


    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {

        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {

            val data = model as SeeAllDataItem

            when (itemClickType) {
                ItemClickType.LIKE_UNLIKE -> onClickBookMark(model, position)

                ItemClickType.PLAY_VIDEO -> startActivityForResult(
                    Intent(this@AllVideoListActivity, VideoPlayerActivity::class.java).putExtra(
                        Constants.App.VIDEO_LINK, data.tempPath
                    ).putExtra(
                        Constants.App.VIDEO_THUMB_PATH_LINK, data.thumbpath
                    ).putExtra(
                        Constants.App.POSITION, position
                    ).putExtra(
                        Constants.App.VIDEO_ID, data.id
                    ).putExtra(
                        Constants.App.VIDEO_ACTIVITY_NAME, model.title
                    ).putExtra(
                        Constants.App.IS_FAVROITE, data.isFavourite
                    ), videoPlayRequestCode
                )

                ItemClickType.OPEN_PDF -> gotoOpenPdfActivity(data)
                /*Intent(context, OpenPdfFileActivity::class.java)
                    intent.putExtra(Constants.App.PDF_LINK, featuredProductListModel[position].pdf)
                            context.startActivity(intent)*/

                ItemClickType.PLAY_AUDIO -> gotoAudioPlayingActivity(data, position)


                ItemClickType.OPEN_IMAGE -> gotoOpenImageDetail(data)

                else -> {}
            }

        } else {

            callForLogin()

        }


    }

    private fun gotoAudioPlayingActivity(data: SeeAllDataItem, position: Int) {


        if (AppController.simpleExoplayer!!.playbackState == 4) {
            AppController.audioStateEnd = "true"
            AppController.simpleExoplayer!!.seekTo(0)
            AppController.simpleExoplayer!!.playWhenReady = true
        } else {
            AppController.audioStateEnd = "false"

        }


        startActivityForResult(
            Intent(this@AllVideoListActivity, AudioPlayActivity::class.java).putExtra(
                Constants.App.AUDIO_LINK, data.tempPath
            ).putExtra(
                Constants.App.POSITION, position
            ).putExtra(
                Constants.App.AUDIO_ID, data.id
            ).putExtra(
                Constants.App.AUDIO_TITLE, data.title
            ).putExtra(
                Constants.App.AUDIO_DURATION, data.duration
            ).putExtra(
                Constants.App.AUDIO_ACTIVITY_NAME, data.product!!.name
            ).putExtra(
                Constants.App.IS_FAVROITE, data.isFavourite
            ).putExtra(
                Constants.App.AUDIO_THUMPATH, data.thumbpath
            ).putExtra(
                Constants.App.SCREEN_TYPE, "PlayList"
            ), audioPlayRequestCode
        )

        AppController.audioList!!.clear()
        mAudioList!!.clear()
        playingSongAudioId = data.id.toString()
        //  SharedPreferencesHelper.saveAudioId(data.id.toString())

        for (i in 0 until allDataItemList!!.size) {
            mAudioFilesItem = SeeAllDataItem()
            product = Product()
            if (allDataItemList!!.get(i).thumbpath != null) {
                mAudioFilesItem!!.thumbpath = allDataItemList!!.get(i).thumbpath
            }
            mAudioFilesItem!!.id = allDataItemList!!.get(i).id
            mAudioFilesItem!!.tempPath = allDataItemList!!.get(i).tempPath
            mAudioFilesItem!!.isFavourite = allDataItemList!!.get(i).isFavourite
            mAudioFilesItem!!.title = allDataItemList!!.get(i).title


            product!!.name = allDataItemList!!.get(i).product!!.name
            mAudioFilesItem!!.product = product

            mAudioList!!.add(mAudioFilesItem!!)

        }

        AppController.audioList!!.addAll(mAudioList!!)

    }

    private fun gotoOpenImageDetail(model: SeeAllDataItem) {
        startActivity(
            Intent(this@AllVideoListActivity, MenuDetailActivity::class.java).putExtra(
                "type",
                "Images"
            )
                .putExtra("id", model.id)
        )

        //finish()

    }

    /**
     * here on click add/remove video Bookmark
     */
    private fun onClickBookMark(model: SeeAllDataItem, position: Int) {
        val dataItems = SeeAllDataItem()
        val addFav: Int

        if (model.isFavourite) {
            addFav = 0
            model.isFavourite = false

        } else {
            addFav = 1
            model.isFavourite = true

        }

        addFavouriteRequest.apply {
            product_video_id = model.id
            action = addFav
        }

        // here add adapter model data into list
        dataItems.image = model.image
        dataItems.thumbnail = model.thumbnail
        dataItems.featured = model.featured
        dataItems.description = model.description
        dataItems.title = model.title
        dataItems.type = model.type
        dataItems.duration = model.duration
        dataItems.path = model.path
        dataItems.pdf = model.pdf
        dataItems.thumbpath = model.thumbpath
        dataItems.productId = model.productId
        dataItems.id = model.id
        dataItems.tempPath = model.tempPath
        dataItems.status = model.status
        dataItems.isFavourite = model.isFavourite

        if (model.product != null) {
            val product = Product()

            product.name = model.product!!.name
            dataItems.product = product
        }
        if (model.videoView != null) {

            val videoViewModel = VideoViewModel()
            videoViewModel.duration = model.videoView!!.duration
            model.videoView!!.duration = videoViewModel.duration
            dataItems.videoView = model.videoView!!

        }

        allDataItemList!!.set(position, dataItems)
        allVideoListAdapter!!.notifyItemChanged(position, dataItems)


        //here call add/remove favourite api
        callAddOrRemoveFavouriteApi()

    }


    /**
     * here call add or remove favourite api
     */
    private fun callAddOrRemoveFavouriteApi() {

        // notifyDataSetChanged()
        seeAllCategoryListDataPresenter.hitApiToAddFavourite(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            addFavouriteRequest = addFavouriteRequest
        )

    }

    /**
     * goto open PDF file
     */
    private fun gotoOpenPdfActivity(data: SeeAllDataItem) {

        startActivity(
            Intent(this@AllVideoListActivity, OpenPdfFileActivity::class.java).putExtra(
                Constants.App.PDF_LINK,
                data.pdf
            ).putExtra(Constants.App.PDF_NAME, data.title)
        )

    }

    private fun addDataList(data: ArrayList<SeeAllDataItem>) {
        if (!callPagination) {
            allDataItemList!!.clear()
        } else {
            callPagination = false

        }

        Log.d(TAG, "lastloadpage-" + lastloadpage)

        allDataItemList!!.addAll(data)

        //AppController.audioList!!.clear()
        //AppController.audioList!!.addAll(data)


        Log.d(TAG, "allDataItemList(1):" + allDataItemList!!.size)


        if (allDataItemList!!.size > 0) {
            tvNoDataFound.visibility = View.GONE
            //   tvAllVideos.visibility = View.VISIBLE

            initializeMiniAudioPlayer(
                allAudioPlayerView,
                tvAudioTitleName,
                ivExoIcon,
                progressBarExo
            )

        } else {

            tvNoDataFound.visibility = View.VISIBLE
            // tvAllVideos.visibility = View.GONE
        }

        setToolbarTitle()
        setListHeadingName()


        allVideoListAdapter!!.notifyDataSetChanged()

        Log.d(TAG, "allDataItemList:  " + allDataItemList!!.size)

    }

    private fun setListHeadingName() {

        if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {
            tvAllVideos.visibility = View.GONE
            tvItemCount.visibility = View.GONE
            setMargins(rvAllVideos, 0, 24, 0, 34)

            //tvAllVideos.text = allDataItemList!!.get(0).product!!.name
        } else {
            tvAllVideos.visibility = View.VISIBLE
            tvItemCount.visibility = View.VISIBLE

            tvAllVideos.text = tagName + "/All " + categoryName
            tvItemCount.text = categoryItemCount


        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is MarginLayoutParams) {
            val p = view.layoutParams as MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }


    private fun addScrollListener(productId: Int) {
        rvAllVideos.addOnScrollListener(object :
            PaginationScrollListener(rvAllVideos.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {

                //if (currentPage <= lastloadpage) {
                pbLoadMore.visibility = View.VISIBLE
                isLoading = true
                currentPage += 1
                Handler(Looper.myLooper()!!).postDelayed({


                    if (intent.getStringExtra(Constants.App.FROM).equals("MenuDetailActivity")) {
                        callPagination = true
                        callSeeAllCategoryListApi(productId)
                    } else {

                        callParticularCategoryApi(tagId, categoryId)

                    }

                }, 1000)


                // }


            }

            override fun isLastPage() = isLastPage

            override fun isLoading() = isLoading
        })


    }

    override fun onResponseSuccess(response: SeeAllResModel) {
        if (this@AllVideoListActivity.baseContext != null) {
            refreshLayoutAllVideos.isRefreshing = false

            runOnUiThread {
                if (this@AllVideoListActivity.baseContext != null) {

                    hideProgressDialog()

                    pbLoadMore.visibility = View.GONE
                    isLoading = false
                    lastloadpage = response.data!!.lastPage!!
                    total = response.data.total!!

                    isLastPage = currentPage == response.data.lastPage!!

                    // pbLoadMore.visibility = View.GONE

                    //currentPage = ++currentPage

                    //if (allDataItemList!!.size<=response.data.lastPage)

                    addDataList(response.data.data!!)


                }
            }
        }

    }


    override fun onAddFavResponseSuccess(response: CommonResponse) {
        if (this@AllVideoListActivity.baseContext != null) {
            runOnUiThread {
                refreshLayoutAllVideos.isRefreshing = false

                if (this@AllVideoListActivity.baseContext != null) {
                    showToast(response.message)
                }
            }
        }
    }

    override fun onViewsSubmittedSuccess(response: CommonResponse) {
        if (this@AllVideoListActivity.baseContext != null) {
            runOnUiThread {
                refreshLayoutAllVideos.isRefreshing = false
                hideProgressDialog()

                if (this@AllVideoListActivity.baseContext != null) {

                    try {


                        if (AppController.simpleExoplayer!!.currentWindowIndex.toInt() == -1) {
                            Log.d(
                                TAG,
                                "currentWindowIndex:-  " + AppController.simpleExoplayer!!.currentWindowIndex.toInt()
                            )

                            return@runOnUiThread
                        } else {

                            playingSongAudioId =
                                AppController.simpleExoplayer!!.currentMediaItem!!.mediaId

                            if (allDataItemList!!.size > 0 && allDataItemList!!.get(AppController.simpleExoplayer!!.currentWindowIndex).id.toString()
                                    .equals(AppController.simpleExoplayer!!.currentMediaItem!!.mediaId)
                            ) {


                                var duration = "0"

                                if (allDataItemList!!.get(AppController.simpleExoplayer!!.currentWindowIndex).duration != null) {
                                    duration =
                                        allDataItemList!!.get(AppController.simpleExoplayer!!.currentWindowIndex).duration!!


                                    Log.d(
                                        TAG,
                                        "AudioTimeduration1= " + duration
                                    )


                                }

                                val time =
                                    duration            //which is from server;
                                val splitTime: List<String> = time.split(":")
                                val hourOfDay = splitTime[0]
                                val minute = splitTime[1]
                                val sec = splitTime[2]

                                val mycalendar: Calendar = Calendar.getInstance()
                                mycalendar.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                                mycalendar.set(Calendar.MINUTE, minute.toInt())
                                mycalendar.set(Calendar.SECOND, sec.toInt())

                                val times: Long = mycalendar.timeInMillis

                                Log.d(TAG, "TimeInMillis: " + times)
                                Log.d(
                                    TAG,
                                    "convertMillisecondsToSeconds: " + AppHelper.convertMillisecondsToSeconds(
                                        milliseconds = times
                                    ).toInt()
                                )

                                saveAudioDuration = AppHelper.convertMillisecondsToSeconds(
                                    milliseconds = times
                                ).toInt()

                                Log.d(
                                    TAG,
                                    "AudioTimeduration2= " + saveAudioDuration
                                )

                            }
                            currentPage = 1
                            if (intent.getStringExtra(Constants.App.FROM)
                                    .equals("MenuDetailActivity")
                            ) {
                                showProgress = true
                                callSeeAllCategoryListApi(productId!!)
                            } else {
                                showProgress = true

                                callParticularCategoryApi(tagId, categoryId)
                            }

                        }
                    } catch (e: IndexOutOfBoundsException) {

                        Log.d(TAG, "IndexOutOfBoundsException: " + e.message)

                    }

                }
            }
        }
    }

    override fun onFailureResponse(message: String) {

        if (this@AllVideoListActivity.baseContext != null) {

            runOnUiThread {
                if (this@AllVideoListActivity.baseContext != null) {
                    hideProgressDialog()
                    refreshLayoutAllVideos.isRefreshing = false
                    pbLoadMore.visibility = View.GONE
                    Log.d(TAG, ">>" + message)

                    if (message.equals("Unauthorized")) {

                        val dialogSessionExpired = DialogSessionExpired(
                            listener = this,
                            message = getString(R.string.session_expired),
                            context = this,

                            )

                        try {
                            dialogSessionExpired.show()

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }


                    } else {

                        tvNoDataFound.visibility = View.VISIBLE
                        tvNoDataFound.setText(R.string.no_data_found)
                    }
                }
            }
        }

    }

    /**
     * here Convert Second To HH:MM:SS string
     */
    private fun ConvertSecondToHHMMString(secondtTime: Int): String? {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat("HH:mm:ss")
        df.timeZone = tz
        return df.format(Date(secondtTime * 1000L))
    }

    private fun callForLogin() {
        startActivity(Intent(this@AllVideoListActivity, SignInActivity::class.java))
    }

    /**
     * Here  call Exoplayer Player Listener
     */
    fun audioPlayerListner() {

        AppController.simpleExoplayer!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                val isActivityInForeground =
                    lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

                if (isActivityInForeground) {
                    Log.d(TAG, "isActivityInForeground")

                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        Log.d(TAG, "onPlayerStateChanged= STATE_READY")
                        //  playVideo()

                    } else if (playWhenReady && playbackState == Player.STATE_IDLE) {

                        Log.d(TAG, "onPlayerStateChanged= audio is STATE_IDLE")


                    } else if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "onPlayerStateChanged= STATE_ENDED")
                        //  audioIndex=AppController.simpleExoplayer?.currentWindowIndex!! - 1
                        callToHitApiForAudioViews()


                    } else {
                        Log.d(TAG, "onPlayerStateChanged= audio is paused ")

                        callToHitApiForAudioViews()
                    }
                }
            }


            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                val isActivityInForeground =
                    lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

                if (isActivityInForeground) {
                    Log.d(TAG, "isActivityInForeground")
                    Log.d(TAG, "onTracksChanged12= " + tracks)
                    callToHitApiForAudioViews()

                }


            }


            /* override fun onIsPlayingChanged(isPlaying: Boolean) {
                 super.onIsPlayingChanged(isPlaying)
                 Log.d(TAG, "onPlayerStateChanged= " + isPlaying)

             }
             override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                 super.onMediaItemTransition(mediaItem, reason)
                 Log.d(TAG, "onMediaItemTransition ")

                 callToHitApiForAudioViews()

             }*/

            /*override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().message);
                //Restart the playback
               // initializePlayer()
            }*/
        })


    }


    /**
     * Here connecting to server and uses view video API
     */
    private fun callToHitApiForAudioViews() {


        if (!playingSongAudioId.isNullOrEmpty() && !playingSongAudioId.equals(AppController.simpleExoplayer!!.currentMediaItem!!.mediaId)) {

            Log.d(
                TAG,
                "audioDuration1>> " + saveAudioDuration
            )

            Log.d(
                TAG,
                "playingSongAudioId1>> " + playingSongAudioId
            )

            videoDurationRequestModel = VideoDurationRequestModel().apply {
                id = playingSongAudioId.toInt()
                duration = saveAudioDuration
            }


            if (isInternetConnected(shouldShowToast = true)) {
                seeAllCategoryListDataPresenter.hitApiForVideoView(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    videoId = playingSongAudioId.toInt(),
                    videoDurationRequestModel = videoDurationRequestModel!!
                )


            }

        } else {

            saveAudioDuration = 0

            videoDurationRequestModel = VideoDurationRequestModel().apply {
                id = AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt()
                duration = getViewAudioDuration().toInt()
            }

            Log.d(
                TAG,
                "audioDuration2>> " + getViewAudioDuration().toInt()
            )

            Log.d(
                TAG,
                "playingSongAudioId2>> " + AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt()
            )



            Log.d(
                TAG,
                "AudioId>> " + AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt()
            )
            Log.d(TAG, "ViewAudioDuration>> " + getViewAudioDuration())


            if (isInternetConnected(shouldShowToast = true)) {
                seeAllCategoryListDataPresenter.hitApiForVideoView(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    videoId = AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt(),
                    videoDurationRequestModel = videoDurationRequestModel!!
                )

            }
        }

    }

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }

    }

    fun clearLocalDB() {
        onSessionExpiredClearLocalDB(databaseHelper)
        startActivity(
            Intent(
                this@AllVideoListActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }
}




