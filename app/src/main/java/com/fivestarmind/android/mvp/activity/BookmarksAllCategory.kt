package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.fivestarmind.android.R
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.BookMarkSectionAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.BookmarkAllCategoryItem
import com.fivestarmind.android.mvp.model.response.BookmarkAllCategoryResModel
import com.fivestarmind.android.mvp.model.response.BookmarkFilesItem
import com.fivestarmind.android.mvp.presenter.BookMarkAllCategoryPresenter
import kotlinx.android.synthetic.main.activity_bookmarks_all_category.allBookmarkMiniPlayer
import kotlinx.android.synthetic.main.activity_bookmarks_all_category.rvBookmarkAllCategory
import kotlinx.android.synthetic.main.activity_bookmarks_all_category.tvBookmarkNoDataFound
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle

class BookmarksAllCategory : BaseActivity(), RecyclerViewItemListener,
    DialogSesstionExpiredListener,
    BookMarkAllCategoryPresenter.ResponseCallBack {
    private var presenter: BookMarkAllCategoryPresenter? = null

    var bookMarkSectionAdapter: BookMarkSectionAdapter? = null
    var bookmarkAllCategoryItem: ArrayList<BookmarkAllCategoryItem>? = null
    var bookmarkAllCategoryResModel: BookmarkAllCategoryResModel? = null
    var showAllCategoryRequestCode: Int = 1
    var videoPlayRequestCode: Int = 2
    var audioPlayRequestCode: Int = 3
    private var databaseHelper = DatabaseHelper(this)
    var showBackIcon: String = ""
    var backPressed: Int = 0
    var dialogSessionExpired: DialogSessionExpired? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks_all_category)
        initPresenter()
        setToolbarTitle()
        getIntentFrom()
        setClickListener()
        initRecyclerView()
        callGetAllBookMarkCategoryApi()
        initializeMiniAudioPlayer(
            allBookmarkMiniPlayer,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )


    }


    override fun onResume() {
        super.onResume()
        callGetAllBookMarkCategoryApi()
        initializeMiniAudioPlayer(
            allBookmarkMiniPlayer,
            tvAudioTitleName,
            ivExoIcon,
            progressBarExo
        )


    }


    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        bookmarkAllCategoryItem = ArrayList()

        presenter = BookMarkAllCategoryPresenter(this)
    }


    private fun callGetAllBookMarkCategoryApi() {

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter!!.hitApiToBookmarkAllCategory(
                "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt()
            )


        }

    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        ivBackMenu.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickOnBackPress()
            }
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {

        bookMarkSectionAdapter = BookMarkSectionAdapter(this, bookmarkAllCategoryItem!!, this)
        rvBookmarkAllCategory.apply {
            //  layoutManager = linearLayoutManager
            adapter = bookMarkSectionAdapter
        }
    }


    /**
     * here call click On BackPress
     */
    private fun clickOnBackPress() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == showAllCategoryRequestCode) {

            val backFromBookMark = data!!.getStringExtra("backBookMark")

            if (backFromBookMark.equals("backBookMark")) {

                // callGetAllBookMarkCategoryApi()

            }
        }

    }

    override fun onBackPressed() {
        if (showBackIcon.equals("ShowBackButton")) {
            clickOnBackPress()


        } else {
            this.parent.onBackPressed()

        }
    }


    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        // ivBackMenu.setImageResource(R.drawable.ic_arrow_right_white)
        //     ivBackMenu.rotation = 180f
        tvTitle.text = getString(R.string.book_marks)
    }

    fun getIntentFrom() {
        ivBackMenu.visibility = View.GONE

        if (intent.hasExtra(Constants.App.FROM) && null != intent.getStringExtra(Constants.App.FROM)) {
            showBackIcon = intent.getStringExtra(Constants.App.FROM)!!
            if (showBackIcon.equals("ShowBackButton")) {
                ivBackMenu.visibility = View.VISIBLE


            } else {
                ivBackMenu.visibility = View.GONE
                showBackIcon = ""
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
            /* ItemClickType.LIKE_UNLIKE -> clickedLikeUnlike(
                 model = data,
                 position = position

             )*/
            ItemClickType.PLAY_VIDEO -> {
                val data = model as BookmarkFilesItem
                startActivityForResult(
                    Intent(this@BookmarksAllCategory, VideoPlayerActivity::class.java).putExtra(
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
            }

            ItemClickType.OPEN_PDF -> {
                val data = model as BookmarkFilesItem
                gotoOpenPdfActivity(data)
            }

            ItemClickType.PLAY_AUDIO -> {
                val data = model as BookmarkFilesItem
                gotoAudioPlayingActivity(data, position)
            }

            ItemClickType.OPEN_IMAGE -> {
                val data = model as BookmarkFilesItem
                openImageDetail(data)
            }

            ItemClickType.DETAIL -> {
                val data = model as BookmarkAllCategoryItem

                gotoBookMarkActivity(data)
            }


            else -> {
                showToast("wrong click type")
            }
        }

    }

    private fun gotoBookMarkActivity(data: BookmarkAllCategoryItem) {
        startActivityForResult(
            Intent(this@BookmarksAllCategory, BookMarkActivity::class.java).putExtra(
                Constants.App.CATEGORY_ID, data.id!!
            ), showAllCategoryRequestCode
        )
    }

    private fun gotoAudioPlayingActivity(data: BookmarkFilesItem, position: Int) {
        startActivityForResult(
            Intent(this@BookmarksAllCategory, AudioPlayActivity::class.java).putExtra(
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
                Constants.App.AUDIO_ACTIVITY_NAME, data.product?.name
            ).putExtra(
                Constants.App.IS_FAVROITE, data.isFavourite
            ).putExtra(
                Constants.App.AUDIO_THUMPATH, data.thumbpath
            ).putExtra(
                Constants.App.SCREEN_TYPE, "PlayList"
            ), audioPlayRequestCode
        )

    }

    /**
     *  goto open open Image Detail
     */
    private fun openImageDetail(data: BookmarkFilesItem) {

        /*  startActivity(
              Intent(this@BookmarksAllCategory, MenuDetailActivity::class.java).putExtra(
                  "type",
                  "Mind Venom"
              )
                  .putExtra("id", data.id)
          )*/

        startActivity(
            Intent(this@BookmarksAllCategory, MenuDetailActivity::class.java).putExtra(
                "type",
                "Images"
            )
                .putExtra("id", data.id)
        )

        // finish()
    }

    /**
     * goto open PDF file
     */
    private fun gotoOpenPdfActivity(data: BookmarkFilesItem) {

        startActivity(
            Intent(this@BookmarksAllCategory, OpenPdfFileActivity::class.java).putExtra(
                Constants.App.PDF_LINK,
                data.pdf
            ).putExtra(
                Constants.App.PDF_NAME,
                data.title
            )
        )

    }

    override fun onResponseSuccess(response: BookmarkAllCategoryResModel) {
        if (this@BookmarksAllCategory.baseContext != null) {
            runOnUiThread {

                Log.d(TAG, "onResponseSuccess_1__: " + bookmarkAllCategoryItem!!.size)
                hideProgressDialog()
                bookmarkAllCategoryResModel = response
                bookmarkAllCategoryItem!!.clear()

                if (bookmarkAllCategoryResModel!!.data!!.size > 0) {
                    Log.d(TAG, "onResponseSuccess_2__:" + bookmarkAllCategoryItem)

                    bookmarkAllCategoryResModel!!.data!!.forEach { it ->
                        if (it.files!!.size > 0) {

                            bookmarkAllCategoryItem!!.add(it)
                        }
                    }

                    if (bookmarkAllCategoryItem!!.size > 0) {
                        tvBookmarkNoDataFound.visibility = View.GONE

                    } else {
                        tvBookmarkNoDataFound.visibility = View.VISIBLE

                    }


                }

                bookMarkSectionAdapter!!.notifyDataSetChanged()


            }

        }
    }

    override fun onFailureResponse(message: String) {
        if (this@BookmarksAllCategory.baseContext != null) {

            runOnUiThread {
                hideProgressDialog()
                // showToast(message)
                bookMarkSectionAdapter!!.notifyDataSetChanged()

                if (message.equals("Unauthorized")) {
                    //  showToast("You are logged in with another device to continue with this device you need to login again")
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
                        // dialogSessionExpired!!.show()


                        if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                            dialogSessionExpired!!.show()
                        }

                    } catch (e: WindowManager.BadTokenException) {
                        //use a log message
                        Log.d(TAG, "BadTokenException" + e.message)
                    }
                    //  showSestionExpiredtDialog("You are logged in with another device to continue with this device you need to login again",this,this)

                } else {
                    showToast(message)

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
        if (!isFinishing && dialogSessionExpired!=null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }

        onSessionExpiredClearLocalDB(databaseHelper)

        startActivity(
            Intent(
                this@BookmarksAllCategory,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }
}
