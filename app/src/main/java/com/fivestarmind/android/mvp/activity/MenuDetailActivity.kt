package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.interfaces.SectionedRecyclerViewListener
import com.fivestarmind.android.mvp.adapter.ImageListAdapter
import com.fivestarmind.android.mvp.adapter.sectionAdapter.ListSectionAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.FilesItem
import com.fivestarmind.android.mvp.model.response.MenuDetailDataResponse
import com.fivestarmind.android.mvp.model.response.MenuDetailListResponse
import com.fivestarmind.android.mvp.model.response.Product
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import com.fivestarmind.android.mvp.model.response.ProductImageDetailResponseModel
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.mvp.presenter.MenuDetailListPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_menu_detail.clShowImage
import kotlinx.android.synthetic.main.activity_menu_detail.detailAudioPlayerView
import kotlinx.android.synthetic.main.activity_menu_detail.pbLoadMore
import kotlinx.android.synthetic.main.activity_menu_detail.refreshLayout
import kotlinx.android.synthetic.main.activity_menu_detail.rvItemList
import kotlinx.android.synthetic.main.activity_menu_detail.tvMenudetailNoDataFound
import kotlinx.android.synthetic.main.item_images.ivBookMark
import kotlinx.android.synthetic.main.item_images.ivItemImage
import kotlinx.android.synthetic.main.item_images.progressBar
import kotlinx.android.synthetic.main.item_images.tvTitleName
import kotlinx.android.synthetic.main.item_images.view.ivBookMark
import kotlinx.android.synthetic.main.item_images.view.ivItemImage
import kotlinx.android.synthetic.main.item_images.view.progressBar
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.ivChat
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import java.util.Calendar


class MenuDetailActivity : BaseActivity(), ProgramInterface, SectionedRecyclerViewListener,
    DialogSesstionExpiredListener,
    RecyclerViewItemListener, MenuDetailListPresenter.ResponseCallBack {
    private lateinit var menuDetailListPresenter: MenuDetailListPresenter
    private var imageListAdapter: ImageListAdapter? = null
    private var menuFilesDataList: ArrayList<FilesItem>? = null
    private var menuDetailDataList: ArrayList<MenuDetailDataResponse>? = null
    private var productCategoryList: ArrayList<ProductCategoryDataModel>? = null

    private var sectionedAdapter: SectionedRecyclerViewAdapter? = null
    var position: Int? = null
    var tab: String? = null
    var productId: Int? = null
    var OpenImageId: Int? = null
    var headerName: String? = null
    var videoPlayRequestCode: Int = 2
    var videoDuration: Int = 0
    private var addFavouriteRequest = AddFavouriteRequestModel()

    private var isLoading = false
    private var currentPage = 1
    private var lastloadpage = 0
    private var total = 0
    private var isLastPage: Boolean = false
    var seeAllDataItem: SeeAllDataItem? = null
    var product: Product? = null
    var mAudioList: ArrayList<SeeAllDataItem>? = null
    private var playingSongAudioId: String = ""
    private var saveAudioDuration: Int = 0
    var videoDurationRequestModel: VideoDurationRequestModel? = null
    var showProgress: Boolean? = false
    private var databaseHelper = DatabaseHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_detail)
        clShowImage.visibility = View.GONE

        initUI()
        initData()
        clickListener()
        // callMenuDetailApi()
        // initializePlayer()
        audioPlayerListner()
        initializeMiniAudioPlayer(
            detailAudioPlayerView,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )

    }

    override fun onResume() {
        super.onResume()
        initializeMiniAudioPlayer(
            detailAudioPlayerView,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )
        callMenuDetailApi()
    }

    private fun initUI() {

        //imageListAdapter == null
        refreshLayout.setOnRefreshListener(refresh)
        refreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )


    }

    private val refresh = SwipeRefreshLayout.OnRefreshListener {

        if (imageListAdapter != null) {
            refreshLayout.isEnabled = false
            refreshLayout.isRefreshing = false
            refreshLayout.visibility = View.GONE

        } else {
            refreshLayout.isEnabled = true
            refreshLayout.visibility = View.VISIBLE


        }


        initializeMiniAudioPlayer(
            detailAudioPlayerView,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )
        if (!menuDetailDataList!!.size.equals(total) == true) {
            refreshLayout.isRefreshing = true

            menuDetailDataList!!.clear()
            currentPage = 1
            //
            callMenuDetailApi()
        } else {
            refreshLayout.isRefreshing = false
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == videoPlayRequestCode) {

            //   callMenuDetailApi()

        }


    }

    private fun initData() {
        menuDetailListPresenter = MenuDetailListPresenter(this)
        productCategoryList = arrayListOf()
        menuFilesDataList = arrayListOf()
        menuDetailDataList = arrayListOf()
        mAudioList = arrayListOf()

        tab = intent.getStringExtra("type")
        productId = intent.getIntExtra("productId", 0)
        OpenImageId = intent.getIntExtra("id", 0)

        tvTitle.text = tab
        ivChat.visibility = View.VISIBLE
        ivChat.setImageDrawable(this.getDrawable(R.drawable.ic_unselected_book_mark))

    }

    private fun setAdapterImages() {

        // rvImageList.visibility = View.VISIBLE
        clShowImage.visibility = View.VISIBLE

        // rvItemList.visibility = View.GONE
        refreshLayout.visibility = View.GONE
        showImage()


        /* imageListAdapter =
             ImageListAdapter(this@MenuDetailActivity, menuFilesDataList!!, this)


         rvImageList.apply {
             layoutManager = LinearLayoutManager(
                 this@MenuDetailActivity, LinearLayoutManager.VERTICAL, false
             )
             adapter = imageListAdapter
         }*/
    }


    fun showImage() {
        ivBookMark.isSelected = menuFilesDataList!!.get(0).isFavourite


        tvTitleName.text = menuFilesDataList!!.get(0).title

        if (menuFilesDataList!!.get(0).image != null) {
            updateFavProductImage(itemImage = menuFilesDataList!!.get(0).image!!)
        } else {
            updateFavProductImage(itemImage = "")
        }
    }

    private fun updateFavProductImage(itemImage: String) {

            Picasso.get()
                .load(ApiClient.BASE_URL_MEDIA + itemImage)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(ivItemImage, object : Callback {
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        progressBar.visibility = View.GONE
                        //ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        //  ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))

                    }
                })

            //    ivItemImage.scaleType = ImageView.ScaleType.FIT_XY

            //else ivFavProgram.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))

    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView(menuFilesDataList: ArrayList<FilesItem>) {
        //    rvImageList.visibility = View.GONE
        clShowImage.visibility = View.GONE


        // rvItemList.visibility = View.VISIBLE
        refreshLayout.visibility = View.VISIBLE

        sectionedAdapter = SectionedRecyclerViewAdapter()
        Log.d(TAG, "menuDetailDataList_loop_1 " + menuFilesDataList.size)

        for (i in menuDetailDataList!!.indices) {
            Log.d(TAG, "menuFilesDataList_loop_1 " + menuFilesDataList.size)

            // headerName = menuDetailDataList!!.get(i).name!!
            headerName = menuDetailDataList!!.get(i).name!!

            sectionedAdapter!!.addSection(
                ListSectionAdapter(
                    this,
                    //this.getString(R.string.meditation), "See All ("+MenuFilesDataList!!.size+")",
                    headerName!!, getString(
                        R.string.format_see_all_count,
                        "See All ", menuDetailDataList!!.get(i).filesCount!!
                    ),
                    // menuFilesDataList,
                    menuDetailDataList!!.get(i).files,
                    menuDetailDataList!!.get(i).id!!,
                    menuDetailDataList!!.get(i).name!!,
                    this
                )
            )
        }


        rvItemList.apply {
            adapter = sectionedAdapter
        }

    }


    /**
     * Click event on views ivMenu and btnSubmit
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        ivChat.setOnClickListener(clickListener)
        ivBookMark.setOnClickListener(clickListener)
    }


    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()
                R.id.ivChat -> clickedMyBookmarks()
                R.id.ivBookMark -> onClickedBookMark(0,menuFilesDataList!!.get(0))


            }
    }

    /**
     * It is called when user clicked on my favorite view
     */
    private fun clickedMyBookmarks() {
        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {
            startActivity(
                Intent(
                    this@MenuDetailActivity,
                    BookmarksAllCategory::class.java
                ).putExtra(Constants.App.FROM, "ShowBackButton")
            )
        } else {
            callForLogin()
        }

    }


    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        if (productId != null && productId != 0) {
            val intent = Intent()
            //intent.putExtra("categoryId", "")
            setResult(RESULT_OK, intent)
            finish()
        } else {
            /* startActivity(
                 Intent(
                     this@MenuDetailActivity,
                     HomeActivity::class.java
                 ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                     .putExtra(Constants.App.FROM, "BackFromImage")
             )*/
            finish()
        }
    }

    override fun onBackPressed() {
        clickedBack()
        // super.onBackPressed()

    }

    override fun onItemSelected(position: Int, productId: Int) {
    }


    override fun onListEmpty(isEmpty: Boolean) {
    }

    override fun onProductCategorySelected(position: Int, productId: Int, categoryName: String) {

    }

    override fun onItemRootViewClicked(
        section: ListSectionAdapter,
        model: Any,
        itemAdapterPosition: Int,
        type: String
    ) {
        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {
            val model = model as FilesItem
            when (type) {
                Constants.App.TYPE_PLAY_VIDEO -> gotoPlayMedia(model, itemAdapterPosition)
                Constants.App.TYPE_BOOKMARK -> onClickedBookMark(itemAdapterPosition, model)
                Constants.App.TYPE_PDF -> gotoOpenPdfActivity(model)
                Constants.App.TYPE_AUDIO -> gotoAudioPlayActivity(model, itemAdapterPosition)
                Constants.App.TYPE_IMAGE -> gotoOpenImageDetail(model)
            }

        } else {
            callForLogin()
        }


    }

    private fun gotoOpenImageDetail(model: FilesItem) {


        startActivity(
            Intent(this@MenuDetailActivity, MenuDetailActivity::class.java).putExtra(
                //Intent(this@MenuDetailActivity, ImageDetailActivity::class.java).putExtra(
                "type",
                "Images"
            )
                .putExtra("id", model.id)
        )

        //finish()

    }

    private fun gotoOpenPdfActivity(model: FilesItem) {
        startActivity(
            Intent(this@MenuDetailActivity, OpenPdfFileActivity::class.java)
                .putExtra(Constants.App.PDF_LINK, model.pdf)
                .putExtra(Constants.App.PDF_NAME, model.title)
        )
    }


    private fun gotoAudioPlayActivity(model: FilesItem, itemAdapterPosition: Int) {


        if (AppController.simpleExoplayer!!.playbackState == 4) {
            AppController.audioStateEnd = "true"
            AppController.simpleExoplayer!!.seekTo(0)
            AppController.simpleExoplayer!!.playWhenReady = true
        } else {
            AppController.audioStateEnd = "false"

        }

        startActivityForResult(
            Intent(this@MenuDetailActivity, AudioPlayActivity::class.java).putExtra(
                Constants.App.AUDIO_LINK, model.tempPath
            ).putExtra(
                Constants.App.AUDIO_ID, model.id
            ).putExtra(
                Constants.App.POSITION, itemAdapterPosition
            ).putExtra(
                Constants.App.AUDIO_DURATION, model.duration
            ).putExtra(
                Constants.App.AUDIO_TITLE, model.title
            ).putExtra(
                Constants.App.AUDIO_ACTIVITY_NAME, model.product!!.name
            ).putExtra(
                Constants.App.IS_FAVROITE, model.isFavourite
            ).putExtra(
                Constants.App.AUDIO_THUMPATH, model.thumbpath
            ).putExtra(
                Constants.App.SCREEN_TYPE, "PlayList"
            ), videoPlayRequestCode
        )

        AppController.audioList!!.clear()
        mAudioList!!.clear()

        for (i in 0 until menuDetailDataList!!.size) {
            for (j in 0 until menuDetailDataList!!.get(i).files.size) {
                seeAllDataItem = SeeAllDataItem()
                product = Product()
                if (menuDetailDataList!!.get(i).files.get(j).thumbpath != null) {
                    seeAllDataItem!!.thumbpath = menuDetailDataList!!.get(i).files.get(j).thumbpath
                }
                seeAllDataItem!!.id = menuDetailDataList!!.get(i).files.get(j).id
                seeAllDataItem!!.tempPath = menuDetailDataList!!.get(i).files.get(j).tempPath
                seeAllDataItem!!.title = menuDetailDataList!!.get(i).files.get(j).title
                seeAllDataItem!!.duration = menuDetailDataList!!.get(i).files.get(j).duration
                seeAllDataItem!!.isFavourite = menuDetailDataList!!.get(i).files.get(j).isFavourite
                product!!.name = menuDetailDataList!!.get(i).files.get(j).product!!.name
                seeAllDataItem!!.product = product
                mAudioList!!.add(seeAllDataItem!!)


            }
            AppController.audioList!!.addAll(mAudioList!!)

            Log.d(TAG, "AppController_audioList" + AppController.audioList!!.size)
        }

    }

    /**
     * here call bookmark method
     */
    private fun onClickedBookMark(position: Int, model: FilesItem) {
        val fileItems = FilesItem()
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
        fileItems.image = model.image
        fileItems.thumbnail = model.thumbnail
        fileItems.featured = model.featured
        fileItems.description = model.description
        fileItems.title = model.title
        fileItems.type = model.type
        fileItems.duration = model.duration
        fileItems.path = model.path
        fileItems.pdf = model.pdf
        fileItems.thumbpath = model.thumbpath
        fileItems.productId = model.productId
        fileItems.id = model.id
        fileItems.tempPath = model.tempPath
        fileItems.status = model.status
        fileItems.isFavourite = model.isFavourite

        if (tab!!.contains("Mind Venom") || tab!!.contains("Images")) {

            /*menuFilesDataList!!.set(position, fileItems)
            imageListAdapter!!.notifyItemChanged(position, fileItems)*/
            callAddOrRemoveFavouriteApi()

            //   callMenuDetailApi()

        } else {

            callAddOrRemoveFavouriteApi()
            // callMenuDetailApi()

        }
    }

    /**
     * here call add or remove favourite api
     */
    private fun callAddOrRemoveFavouriteApi() {

        // notifyDataSetChanged()
        menuDetailListPresenter.hitApiToAddFavourite(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            addFavouriteRequest = addFavouriteRequest
        )

    }

    /**
     * here call play media file
     */
    private fun gotoPlayMedia(model: FilesItem, position: Int) {

        startActivityForResult(
            Intent(this@MenuDetailActivity, VideoPlayerActivity::class.java).putExtra(
                Constants.App.VIDEO_LINK, model.tempPath
            ).putExtra(
                Constants.App.POSITION, position
            ).putExtra(
                Constants.App.VIDEO_THUMB_PATH_LINK, model.thumbpath
            ).putExtra(
                Constants.App.VIDEO_ID, model.id
            ).putExtra(
                //  Constants.App.VIDEO_ACTIVITY_NAME, model.product!!.name
                Constants.App.VIDEO_ACTIVITY_NAME, model.title
            ).putExtra(
                Constants.App.IS_FAVROITE, model.isFavourite
            ), videoPlayRequestCode
        )

    }

    override fun onFooterRootViewClicked(section: ListSectionAdapter, itemAdapterPosition: Int) {


    }

    override fun onHeaderViewClicked(
        section: ListSectionAdapter,
        productId: Int,
        productName: String,
        itemAdapterPosition: Int
    ) {
        //  val model = model as FilesItem

        gotoAllMediaList(productId, productName)

    }

    /**
     * here call all media list
     */
    private fun gotoAllMediaList(productId: Int, productName: String) {

        startActivity(
            Intent(this@MenuDetailActivity, AllVideoListActivity::class.java).putExtra(
                Constants.App.PRODUCT_ID,
                productId
            ).putExtra(Constants.App.PRODUCT_NAME, productName)
                .putExtra(Constants.App.FROM, "MenuDetailActivity")
        )

    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        if (!SharedPreferencesHelper.getAuthToken().isEmpty()) {
            val model = model as FilesItem

            when (itemClickType) {
                ItemClickType.LIKE_UNLIKE -> onClickedBookMark(position = position, model = model)
                //   ItemClickType.OPEN_PDF-> startActivity(Intent(this@MenuDetailActivity,OpenPdfFileActivity::class.java ))
                else -> {}
            }
        } else {
            callForLogin()
        }


    }

    override fun onResponseSuccess(response: MenuDetailListResponse) {
        if (this@MenuDetailActivity.baseContext != null) {
            refreshLayout.isRefreshing = false

            runOnUiThread {
                if (this@MenuDetailActivity.baseContext != null) {

                    hideProgressDialog()

                    pbLoadMore.visibility = View.GONE
                    isLoading = false
                    lastloadpage = response.data!!.lastPage!!
                    total = response.data.total!!

                    isLastPage = currentPage == response.data.lastPage!!

                    if (response.data.data!!.size > 0) {
                        //here set text visibility No Data Found
                        tvMenudetailNoDataFound.visibility = View.GONE

                        addDataToMenuDetailList(response.data.data!!)


                        initializeMiniAudioPlayer(
                            detailAudioPlayerView,
                            tvAudioTitleName,
                            ivExoIcon,
                            progressBarExo
                        )

                    } else {
                        //here set text visibility No Data Found
                        tvMenudetailNoDataFound.visibility = View.VISIBLE
                    }


                }
            }
        }
    }

    override fun onResponseSuccess(response: ProductImageDetailResponseModel) {
        if (this@MenuDetailActivity.baseContext != null) {
            runOnUiThread {
                refreshLayout.isRefreshing = false
                tvMenudetailNoDataFound.visibility = View.GONE

                hideProgressDialog()


                if (this@MenuDetailActivity.baseContext != null) {
                    menuFilesDataList!!.clear()

                    Log.d(TAG, "ProductImageDetailResponse:-  " + response.data!!)
                    menuFilesDataList!!.add(response.data)

                    //     if (tab!!.contains("Mind Venom")) {
                    if (tab!!.contains("Images")) {
                        tvTitle.text = tab
                        setAdapterImages()

                    }
                }
            }
        }
    }


    /**
     * here call recycler view scroll listener for load more data
     */
    private fun addScrollListener() {
        rvItemList.addOnScrollListener(object :
            PaginationScrollListener(rvItemList.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {

                pbLoadMore.visibility = View.VISIBLE

                isLoading = true
                currentPage += 1
                Handler(Looper.myLooper()!!).postDelayed({

                    callMenuDetailApi()

                }, 1000)


            }

            override fun isLastPage() = isLastPage

            override fun isLoading() = isLoading
        })


    }

    override fun onAddFavResponseSuccess(response: CommonResponse) {
        if (this@MenuDetailActivity.baseContext != null) {
            runOnUiThread {
                refreshLayout.isRefreshing = false

                if (this@MenuDetailActivity.baseContext != null) {
                    showToast(response.message)
                    callMenuDetailApi()
                }
            }
        }
    }

    override fun onViewsSubmittedSuccess(response: CommonResponse) {
        if (this@MenuDetailActivity.baseContext != null) {
            runOnUiThread {
                refreshLayout.isRefreshing = false

                if (this@MenuDetailActivity.baseContext != null) {

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

                            if (menuFilesDataList!!.size > 0 && menuFilesDataList!!.get(
                                    AppController.simpleExoplayer!!.currentWindowIndex
                                ).id.toString()
                                    .equals(AppController.simpleExoplayer!!.currentMediaItem!!.mediaId)
                            ) {


                                var duration = "0"

                                if (menuFilesDataList!!.get(AppController.simpleExoplayer!!.currentWindowIndex).duration != null) {
                                    duration =
                                        menuFilesDataList!!.get(AppController.simpleExoplayer!!.currentWindowIndex).duration!!
                                }

                                Log.d(
                                    TAG,
                                    "AudioTimeduration2 : $duration"
                                )
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


                            }

                            showProgress = true
                            callMenuDetailApi()
                        }

                    } catch (e: IndexOutOfBoundsException) {

                        Log.d(TAG, "IndexOutOfBoundsException: " + e.message)

                    }


                }

            }
        }
    }


    /**
     * here update data in view
     */

    private fun addDataToMenuDetailList(dataList: ArrayList<MenuDetailDataResponse>?) {
        menuDetailDataList!!.clear()
        menuFilesDataList!!.clear()
        if (dataList!!.isNotEmpty())
            menuDetailDataList!!.addAll(dataList)
        Log.d(TAG, "menuDetailDataList_size " + menuDetailDataList!!.size)


        for (i in menuDetailDataList!!.indices) {
            Log.d(TAG, "menuDetailDataList_files_size " + menuDetailDataList!!.get(i).files.size)

            if (menuDetailDataList!!.get(i).files.size > 0) {
                //here set text visibility No Data Found
                tvMenudetailNoDataFound.visibility = View.GONE

                menuFilesDataList!!.addAll(menuDetailDataList!!.get(i).files)

            } else {
                //here set text visibility No Data Found

                tvMenudetailNoDataFound.visibility = View.VISIBLE

            }

        }


        /*if (tab!!.contains("Mind Venom") || tab!!.contains("Images")) {
                tvTitle.text = tab
                setAdapterImages()

            } else {
                tvTitle.text = tab
                initRecyclerView(menuFilesDataList!!)
            }
    */

        tvTitle.text = tab
        initRecyclerView(menuFilesDataList!!)

    }

    override fun onFailureResponse(messages: String) {
        if (this@MenuDetailActivity.baseContext != null) {
            refreshLayout.isRefreshing = false

            runOnUiThread {
                if (this@MenuDetailActivity.baseContext != null) {
                    hideProgressDialog()


                    if (messages.equals("Unauthorized")) {

                        //    showToast(getString(R.string.session_expired))
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
                        menuFilesDataList!!.clear()
                        tvMenudetailNoDataFound.visibility = View.VISIBLE
                    }

                }
            }
        }

    }

    /**
     * Call for api get Organisation Logo
     *
     */
    private fun callMenuDetailApi() {

        if (OpenImageId != null && OpenImageId != 0) {

            Log.d(TAG, "callMenuDetailApi: " + OpenImageId)
            callProductImageDetailApi()

        } else {
            if (isInternetConnected(shouldShowToast = true))
            /* if (showProgress!!) {

                 showProgress = false

             } else {
                 showProgressDialog("")

             }*/


                if (pbLoadMore.isVisible) {
                    hideProgressDialog()

                } else {
                    runOnUiThread {


                        if (MenuDetailActivity@ this != null && !MenuDetailActivity@ this.isFinishing) {
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

            LinearLayoutManager(this@MenuDetailActivity, RecyclerView.VERTICAL, false)

            menuDetailListPresenter.getMenuDetailListApi(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                id = productId!!,
                pageNumber = currentPage,
                limit = Constants.App.Number.TEN,
            )

            addScrollListener()

        }
    }

    /**
     * Call for api get Organisation Logo
     *
     */
    private fun callProductImageDetailApi() {
        if (isInternetConnected(shouldShowToast = true))
            showProgressDialog("")
        menuDetailListPresenter.getProductImageDetailApi(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            imageId = OpenImageId!!
        )
    }

    private fun callForLogin() {
        startActivity(Intent(this@MenuDetailActivity, SignInActivity::class.java))
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
                        Log.d(TAG, "onPlayerStateChanged = STATE_READY")
                        //  playVideo()

                    } else if (playWhenReady && playbackState == Player.STATE_IDLE) {

                        Log.d(TAG, "onPlayerStateChanged= audio is STATE_IDLE")


                    } else if (playbackState == Player.STATE_ENDED) {
                        Log.d(TAG, "onPlayerStateChanged= STATE_ENDED")
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
                    Log.d(TAG, "onPlayerStateChanged= onTracksChanged")
                    callToHitApiForAudioViews()
                }


            }

            /*  override fun onIsPlayingChanged(isPlaying: Boolean) {
              super.onIsPlayingChanged(isPlaying)
              Log.d(TAG, "onPlayerStateChanged= " + "onIsPlayingChanged")

          }*/

            /*override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                Log.d(TAG, "onPlayerStateChanged= "+"onMediaItemTransition")
                //callToHitApiForAudioViews()

            }*/

        })
    }

    /**
     * Here connecting to server and uses view audio API
     */
    private fun callToHitApiForAudioViews() {


        if (!playingSongAudioId.isEmpty() && !playingSongAudioId.equals(AppController.simpleExoplayer!!.currentMediaItem!!.mediaId)) {

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
                menuDetailListPresenter.hitApiForVideoView(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    videoId = playingSongAudioId.toInt(),
                    videoDurationRequestModel = videoDurationRequestModel!!
                )


            }


        } else {
            if (AppController.simpleExoplayer!!.currentMediaItem != null) {
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
                    "playingSongAudioId2>> " + AppController.simpleExoplayer!!.currentMediaItem!!.mediaId
                )



                Log.d(
                    TAG,
                    "AudioId>> " + AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt()
                )
                Log.d(TAG, "ViewAudioDuration>> " + getViewAudioDuration())


                if (isInternetConnected(shouldShowToast = true)) {
                    menuDetailListPresenter.hitApiForVideoView(
                        token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                        appId = SharedPreferencesHelper.getAppID().toInt(),
                        videoId = AppController.simpleExoplayer!!.currentMediaItem!!.mediaId.toInt(),
                        videoDurationRequestModel = videoDurationRequestModel!!
                    )


                }
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
                this@MenuDetailActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }
}

