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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fivestarmind.android.R
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogBookMarkListener
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.BookMarkAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.request.AddFavouriteRequestModel
import com.fivestarmind.android.mvp.model.request.VideoDurationRequestModel
import com.fivestarmind.android.mvp.model.response.BookMarkDataItem
import com.fivestarmind.android.mvp.model.response.BookMarkResponseModel
import com.fivestarmind.android.mvp.model.response.CommonResponse
import com.fivestarmind.android.mvp.model.response.Product
import com.fivestarmind.android.mvp.model.response.ProductBookMarkName
import com.fivestarmind.android.mvp.model.response.SeeAllDataItem
import com.fivestarmind.android.mvp.model.response.VideoViewModel
import com.fivestarmind.android.mvp.presenter.BookMarkPresenter
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Tracks
import kotlinx.android.synthetic.main.activity_bookmark.bookmarkMiniPlayer
import kotlinx.android.synthetic.main.activity_bookmark.pbLoadMore
import kotlinx.android.synthetic.main.activity_bookmark.rvBookmarks
import kotlinx.android.synthetic.main.activity_menu_detail.refreshLayout
import kotlinx.android.synthetic.main.activity_my_favorite.tvNoDataFound
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class BookMarkActivity : BaseActivity(), RecyclerViewItemListener, DialogBookMarkListener,
    DialogSesstionExpiredListener,
    BookMarkPresenter.ResponseCallBack {

    override val TAG = BookMarkActivity::class.java.simpleName
    private var linearLayoutManager: LinearLayoutManager? = null

    private var bookMarkAdapter: BookMarkAdapter? = null
    private var bookMarkArrayList = arrayListOf<BookMarkDataItem>()

    private var bookMarkPresenter: BookMarkPresenter? = null
    var videoPlayRequestCode: Int = 2
    var audioPlayRequestCode: Int = 3
    var videoPlayPercentage: Int = 0
    private var addFavouriteRequest = AddFavouriteRequestModel()

    private var isLoading = false
    private var currentPage = 1
    private var lastloadpage = 0
    private var total = 0
    private var isLastPage: Boolean = false
    private var categoryId: Int? = null

    var mAudioFilesItem: SeeAllDataItem? = null
    var product: Product? = null
    var mAudioList: ArrayList<SeeAllDataItem>? = null
    private var playingSongAudioId: String = ""
    private var saveAudioDuration: Int = 0
    var showProgress: Boolean? = false
    var videoDurationRequestModel: VideoDurationRequestModel? = null
    private var databaseHelper = DatabaseHelper(this)
    var scrollPagination: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        initPresenter()
        initUI()
        setToolbarTitle()
        init()
        initRecyclerView()
        setClickListener()
        audioPlayerListner()
        initializeMiniAudioPlayer(bookmarkMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)
        //   callGetBookMarkApi()


    }

    override fun onResume() {
        super.onResume()
        initializeMiniAudioPlayer(bookmarkMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)

        currentPage = 1
        scrollPagination = false

        callGetBookMarkApi()

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
        initializeMiniAudioPlayer(bookmarkMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)

        if (!bookMarkArrayList.size.equals(total) == true) {
            refreshLayout.isRefreshing = true

            bookMarkArrayList.clear()

            callGetBookMarkApi()
        } else {
            refreshLayout.isRefreshing = false
        }
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

        bookMarkAdapter = BookMarkAdapter(this, bookMarkArrayList, this)

        rvBookmarks.apply {
            //  layoutManager = linearLayoutManager
            adapter = bookMarkAdapter
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        categoryId = intent.getIntExtra(Constants.App.CATEGORY_ID, Constants.App.Number.ZERO)

        bookMarkPresenter = BookMarkPresenter(this)
    }

    private fun init() {
        bookMarkArrayList = ArrayList()
        mAudioList = ArrayList()

        callGetBookMarkApi()

    }

    private fun callGetBookMarkApi() {

        if (isInternetConnected(shouldShowToast = true)) {
            if (pbLoadMore.isVisible) {
                hideProgressDialog()

            } else {
                if (!MenuDetailActivity@ this.isFinishing) {


                    if (showProgress!!) {

                        showProgress = false

                    } else {
                        showProgressDialog("")

                    }


                }
            }

            //  linearLayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
            //  rvBookmarks.layoutManager = linearLayoutManager

            bookMarkPresenter!!.hitApiForBookMark(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                categoryId = categoryId!!,
                pageNumber = currentPage,
                limit = Constants.App.Number.TEN
            )

            //here call Recyclerview ScrollListener

            addScrollListener()

        }

    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        if (shouldProceedClick())
            ivBackMenu.setOnClickListener { onBackPressed() }
    }


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("backBookMark","backBookMark")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        val data = model as BookMarkDataItem
        when (itemClickType) {
            ItemClickType.LIKE_UNLIKE -> clickedLikeUnlike(
                model = data,
                position = position

            )

            ItemClickType.PLAY_VIDEO -> startActivityForResult(
                Intent(this@BookMarkActivity, VideoPlayerActivity::class.java).putExtra(
                    Constants.App.VIDEO_LINK, data.tempPath
                ).putExtra(
                    Constants.App.VIDEO_THUMB_PATH_LINK, data.thumbpath
                ).putExtra(
                    Constants.App.POSITION, position
                ).putExtra(
                    Constants.App.VIDEO_ID, data.id
                ).putExtra(
                    Constants.App.VIDEO_ACTIVITY_NAME, data.title
                ).putExtra(
                    Constants.App.IS_FAVROITE, data.isFavourite
                ), videoPlayRequestCode
            )

            ItemClickType.OPEN_PDF -> gotoOpenPdfActivity(data)

            ItemClickType.PLAY_AUDIO -> gotoAudioPlayingActivity(data, position)

            ItemClickType.OPEN_IMAGE -> openImageDetail(data)


            else -> {
                showToast("wrong click type")
            }
        }


    }

    private fun gotoAudioPlayingActivity(data: BookMarkDataItem, position: Int) {

        if (AppController.simpleExoplayer!!.playbackState == 4) {
            AppController.audioStateEnd = "true"
            AppController.simpleExoplayer!!.seekTo(0)
            AppController.simpleExoplayer!!.playWhenReady = true
        } else {
            AppController.audioStateEnd = "false"

        }

        startActivityForResult(
            Intent(this@BookMarkActivity, AudioPlayActivity::class.java).putExtra(
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

        //SharedPreferencesHelper.saveAudioId(data.id.toString())


        for (i in 0 until bookMarkArrayList.size) {

            mAudioFilesItem = SeeAllDataItem()
            product = Product()

            if (bookMarkArrayList.get(i).thumbpath != null) {
                mAudioFilesItem!!.thumbpath = bookMarkArrayList.get(i).thumbpath
            }
            mAudioFilesItem!!.id = bookMarkArrayList.get(i).id
            mAudioFilesItem!!.tempPath = bookMarkArrayList.get(i).tempPath
            mAudioFilesItem!!.isFavourite = bookMarkArrayList.get(i).isFavourite
            mAudioFilesItem!!.title = bookMarkArrayList.get(i).title

            product!!.name = bookMarkArrayList.get(i).product?.name
            mAudioFilesItem!!.product = product

            mAudioList!!.add(mAudioFilesItem!!)

        }

        AppController.audioList!!.addAll(mAudioList!!)
    }

    /**
     *  goto open open Image Detail
     */
    private fun openImageDetail(data: BookMarkDataItem) {

      /*  startActivity(
            Intent(this@BookMarkActivity, MenuDetailActivity::class.java).putExtra(
                "type",
                "Mind Venom"
            )
                .putExtra("id", data.id)
        )*/

        startActivity(
            Intent(this@BookMarkActivity, MenuDetailActivity::class.java).putExtra(
                "type",
                "Images"
            )
                .putExtra("id", data.id)
        )

        //     finish()
    }

    /**
     * goto open PDF file
     */
    private fun gotoOpenPdfActivity(data: BookMarkDataItem) {

        startActivity(
            Intent(this@BookMarkActivity, OpenPdfFileActivity::class.java).putExtra(
                Constants.App.PDF_LINK,
                data.pdf
            ).putExtra(
                Constants.App.PDF_NAME,
                data.title
            )
        )

    }


    private fun onDialogEventClicked(requestCode: Int, position: Int, model: BookMarkDataItem) {
        Log.d(TAG, "onDialogPositiveEvent: requestCode- $requestCode")

        when (requestCode) {
            Constants.App.RequestCode.REMOVE_BOOK_MARK -> callRemoveBookmarkItem(position, model)
        }
    }

    private fun callRemoveBookmarkItem(position: Int, model: BookMarkDataItem) {
// remove book mark
    }


    /**
     * here add or remove bookMark product
     */
    private fun clickedLikeUnlike(model: BookMarkDataItem, position: Int) {

        /* DialogBookMark(
             this@BookMarkActivity,
             type = REMOVE_BOOK_MARK,
             model= model,
             position = position,
             context = this@BookMarkActivity).show()*/
        showProgressDialog("")
        val dataItems = BookMarkDataItem()
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
        // dataItems.image =model.image
        // dataItems.thumbnail = model.thumbnail
        dataItems.featured = model.featured
        dataItems.description = model.description
        dataItems.title = model.title
        dataItems.type = model.type
        // fileItems.duration = model.duration
        //dataItems.path = model.path
        dataItems.pdf = model.pdf
        dataItems.thumbpath = model.thumbpath
        // dataItems.productId = model.productId
        dataItems.id = model.id
        dataItems.tempPath = model.tempPath
        dataItems.status = model.status
        dataItems.isFavourite = model.isFavourite

        bookMarkArrayList.set(position, dataItems)
        bookMarkAdapter!!.notifyItemChanged(position, dataItems)


        //here call add/remove favourite api
        callAddOrRemoveFavouriteApi(position)
    }

    /**
     * here call add or remove favourite api
     */
    private fun callAddOrRemoveFavouriteApi(position: Int) {

        // notifyDataSetChanged()
        bookMarkPresenter!!.hitApiToAddFavourite(
            token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
            appId = SharedPreferencesHelper.getAppID().toInt(),
            addFavouriteRequest = addFavouriteRequest
        )

        removeDataFromList(position)

    }


    override fun onResponseSuccess(response: BookMarkResponseModel) {

        if (this@BookMarkActivity.baseContext != null) {
            runOnUiThread {

                hideProgressDialog()
                refreshLayout.isRefreshing = false


                pbLoadMore.visibility = View.GONE
                isLoading = false
                lastloadpage = response.data!!.lastPage!!
                total = response.data.total!!
                Log.d(TAG, "bookMarkList:total:- " + total)

                isLastPage = currentPage == response.data.lastPage!!


                if (response.data.data!!.size > 0) {
                    tvNoDataFound.visibility = View.GONE
                    Log.d(TAG, "currentPage:- " + currentPage)
                    Log.d(TAG, "scrollPagination:- " + scrollPagination)

                  //  if (!scrollPagination && currentPage==1) {
                    if (!scrollPagination) {
                        bookMarkArrayList.clear()
                    } else {
                        scrollPagination = false
                    }

                        bookMarkArrayList.addAll(response.data.data)

                        Log.d(TAG, "bookMarkList:Size:- " + bookMarkArrayList.size)
                        bookMarkAdapter!!.notifyDataSetChanged()




                    initializeMiniAudioPlayer(
                        bookmarkMiniPlayer,
                        tvAudioTitleName,
                        ivExoIcon,
                        progressBarExo
                    )


                } else {
                    tvNoDataFound.visibility = View.VISIBLE
                    bookMarkArrayList.clear()
                    bookMarkAdapter!!.notifyDataSetChanged()
                }


            }
        }
    }

    override fun onAddFavResponseSuccess(response: CommonResponse) {
        hideProgressDialog()
        showToast(message = response.message)
    }

    override fun onViewsSubmittedSuccess(response: CommonResponse) {
        if (this@BookMarkActivity.baseContext != null) {
            runOnUiThread {
                refreshLayout.isRefreshing = false

                if (this@BookMarkActivity.baseContext != null) {

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
                            if (bookMarkArrayList.size > 0 && bookMarkArrayList.get(AppController.simpleExoplayer!!.currentWindowIndex).id.toString()
                                    .equals(AppController.simpleExoplayer!!.currentMediaItem!!.mediaId)
                            ) {


                                var duration = "0"
                                if (bookMarkArrayList.get(AppController.simpleExoplayer!!.currentWindowIndex).duration != null) {
                                    duration =
                                        bookMarkArrayList.get(AppController.simpleExoplayer!!.currentWindowIndex).duration!!


                                }

                                val time =
                                    duration
                                //which is from server;
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
                            currentPage = 1
                            showProgress = true
                            callGetBookMarkApi()
                           //updateBookMarkList(getViewAudioDuration().toInt(), AppController.simpleExoplayer!!.currentWindowIndex)

                        }

                    } catch (e: IndexOutOfBoundsException) {

                        Log.d(TAG, "IndexOutOfBoundsException: " + e.message)

                    }
                }


            }
        }
    }

    override fun onFailureResponse(message: String) {
        if (this@BookMarkActivity.baseContext != null) {
            refreshLayout.isRefreshing = false

            runOnUiThread {
                hideProgressDialog()
                pbLoadMore.visibility = View.GONE
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

                    showToast(message)

                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == videoPlayRequestCode) {
            videoPlayPercentage = data!!.getIntExtra("video_play_percentage", 0)
            //callGetBookMarkApi()
            Log.d(TAG, "onActivityResult: videoPlayPercet_ " + videoPlayPercentage)


            val videoDuration = data.getIntExtra("videoDuration", 0)
            val position = data.getIntExtra("videoPosition", 0)

            Log.d(TAG, "onActivityResult: videoPosition " + position)
            Log.d(TAG, "onActivityResult: videoDuration " + videoDuration)


            Log.d(
                TAG,
                "onActivityResult: ConvertSecondToHHMMString " + ConvertSecondToHHMMString(
                    videoDuration
                )
            )


            val dataItem = BookMarkDataItem()

            // here add adapter model data into list
            dataItem.image = bookMarkArrayList.get(position).image
            // seeAllDataItem.thumbpath = bookMarkArrayList!!.get(position).t
            dataItem.featured = bookMarkArrayList.get(position).featured
            dataItem.description = bookMarkArrayList.get(position).description
            dataItem.title = bookMarkArrayList.get(position).title
            dataItem.type = bookMarkArrayList.get(position).type
            dataItem.duration = bookMarkArrayList.get(position).duration
            //  seeAllDataItem.path = bookMarkArrayList!!.get(position).path
            dataItem.pdf = bookMarkArrayList.get(position).pdf
            dataItem.thumbpath = bookMarkArrayList.get(position).thumbpath
            // seeAllDataItem.productId = bookMarkArrayList!!.get(position).productId
            dataItem.id = bookMarkArrayList.get(position).id
            dataItem.tempPath = bookMarkArrayList.get(position).tempPath
            dataItem.status = bookMarkArrayList.get(position).status
            dataItem.isFavourite = bookMarkArrayList.get(position).isFavourite

            // model.product!!.name

            if (bookMarkArrayList.get(position).product != null) {
                val product = ProductBookMarkName()
                product.name = bookMarkArrayList.get(position).product!!.name
                dataItem.product = product
            }
            if (bookMarkArrayList.get(position).videoView != null) {

                val videoViewModel = VideoViewModel()
                videoViewModel.duration = ConvertSecondToHHMMString(videoDuration)


                bookMarkArrayList.get(position).videoView!!.duration = videoViewModel.duration
                dataItem.videoView = bookMarkArrayList.get(position).videoView!!

            }

            bookMarkArrayList.set(position, dataItem)
            bookMarkAdapter!!.notifyDataSetChanged()


        } else if (requestCode == audioPlayRequestCode) {
            //callGetBookMarkApi()
            Log.d(TAG, "onActivityResult: audioPlayRequestCode " + audioPlayRequestCode)

            val videoDuration = data!!.getIntExtra("audioDuration", 0)
            val position = data.getIntExtra("audioPosition", 0)
          //  updateBookMarkList(videoDuration,position)
            val dataItem = BookMarkDataItem()

            // here add adapter model data into list
            dataItem.image = bookMarkArrayList.get(position).image
            // seeAllDataItem.thumbpath = bookMarkArrayList!!.get(position).t
            dataItem.featured = bookMarkArrayList.get(position).featured
            dataItem.description = bookMarkArrayList.get(position).description
            dataItem.title = bookMarkArrayList.get(position).title
            dataItem.type = bookMarkArrayList.get(position).type
            dataItem.duration = bookMarkArrayList.get(position).duration
            //  seeAllDataItem.path = bookMarkArrayList!!.get(position).path
            dataItem.pdf = bookMarkArrayList.get(position).pdf
            dataItem.thumbpath = bookMarkArrayList.get(position).thumbpath
            // seeAllDataItem.productId = bookMarkArrayList!!.get(position).productId
            dataItem.id = bookMarkArrayList.get(position).id
            dataItem.tempPath = bookMarkArrayList.get(position).tempPath
            dataItem.status = bookMarkArrayList.get(position).status
            dataItem.isFavourite = bookMarkArrayList.get(position).isFavourite

            // model.product!!.name

            if (bookMarkArrayList.get(position).product != null) {
                val product = ProductBookMarkName()
                product.name = bookMarkArrayList.get(position).product!!.name
                dataItem.product = product
            }
            if (bookMarkArrayList.get(position).videoView != null) {

                val videoViewModel = VideoViewModel()
                videoViewModel.duration = ConvertSecondToHHMMString(videoDuration)


                bookMarkArrayList.get(position).videoView!!.duration = videoViewModel.duration
                dataItem.videoView = bookMarkArrayList.get(position).videoView!!

            }

            bookMarkArrayList.set(position, dataItem)
            bookMarkAdapter!!.notifyDataSetChanged()


        }

    }


    fun updateBookMarkList(videoDuration :Int, position:Int ){

     //   val videoDuration = data!!.getIntExtra("audioDuration", 0)
    //    val position = data.getIntExtra("audioPosition", 0)

        val dataItem = BookMarkDataItem()

        // here add adapter model data into list
        dataItem.image = bookMarkArrayList.get(position).image
        // seeAllDataItem.thumbpath = bookMarkArrayList!!.get(position).t
        dataItem.featured = bookMarkArrayList.get(position).featured
        dataItem.description = bookMarkArrayList.get(position).description
        dataItem.title = bookMarkArrayList.get(position).title
        dataItem.type = bookMarkArrayList.get(position).type
        dataItem.duration = bookMarkArrayList.get(position).duration
        //  seeAllDataItem.path = bookMarkArrayList!!.get(position).path
        dataItem.pdf = bookMarkArrayList.get(position).pdf
        dataItem.thumbpath = bookMarkArrayList.get(position).thumbpath
        // seeAllDataItem.productId = bookMarkArrayList!!.get(position).productId
        dataItem.id = bookMarkArrayList.get(position).id
        dataItem.tempPath = bookMarkArrayList.get(position).tempPath
        dataItem.status = bookMarkArrayList.get(position).status
        dataItem.isFavourite = bookMarkArrayList.get(position).isFavourite

        // model.product!!.name

        if (bookMarkArrayList.get(position).product != null) {
            val product = ProductBookMarkName()
            product.name = bookMarkArrayList.get(position).product!!.name
            dataItem.product = product
        }
        if (bookMarkArrayList.get(position).videoView != null) {

            val videoViewModel = VideoViewModel()
            videoViewModel.duration = ConvertSecondToHHMMString(videoDuration)


            bookMarkArrayList.get(position).videoView!!.duration = videoViewModel.duration
            dataItem.videoView = bookMarkArrayList.get(position).videoView!!

        }

        bookMarkArrayList.set(position, dataItem)
        bookMarkAdapter!!.notifyDataSetChanged()

    }

    private fun removeDataFromList(position: Int) {
        bookMarkArrayList.removeAt(position)
        bookMarkAdapter!!.notifyItemRemoved(position)

        if (bookMarkArrayList.size == 0) tvNoDataFound.visibility = View.VISIBLE
    }

    override fun onDialogEventListener(
        dialogEventType: DialogEventType,
        requestCode: Int,
        position: Int,
        model: Any?
    ) {
        when (dialogEventType) {
            DialogEventType.POSITIVE -> onDialogEventClicked(
                requestCode = requestCode,
                model = model as BookMarkDataItem,
                position = position
            )

            else ->


                super.onDialogEventListener(dialogEventType, requestCode, model)
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


    /**
     * here call recycler view scroll listener for load more data
     */
    private fun addScrollListener() {
        rvBookmarks.addOnScrollListener(object :
            PaginationScrollListener(rvBookmarks.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {

                // if (currentPage <= lastloadpage) {
                pbLoadMore.visibility = View.VISIBLE
                scrollPagination = true
                isLoading = true
                currentPage += 1
                Handler(Looper.myLooper()!!).postDelayed({
                    callGetBookMarkApi()

                }, 1000)
                //  }


            }

            override fun isLastPage() = isLastPage

            override fun isLoading() = isLoading
        })


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
            /*  override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            Log.d(TAG, "onPlayerStateChanged= " + isPlaying)

                        }*/

            /*    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
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
                bookMarkPresenter!!.hitApiForVideoView(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appId = SharedPreferencesHelper.getAppID().toInt(),
                    videoId = playingSongAudioId.toInt(),
                    videoDurationRequestModel = videoDurationRequestModel!!
                )


            }


        } else {

            saveAudioDuration = 0

            if (AppController.simpleExoplayer!!.currentMediaItem!!.mediaId == null)
                return



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
                bookMarkPresenter!!.hitApiForVideoView(
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
                this@BookMarkActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }

}
