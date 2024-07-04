package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivestarmind.android.R
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.FilterCategoryAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.TagAllCategoryItem
import com.fivestarmind.android.mvp.model.response.TagAllCategoryResModel
import com.fivestarmind.android.mvp.model.response.TagResponseItem
import com.fivestarmind.android.mvp.model.response.TagResponseModel
import com.fivestarmind.android.mvp.presenter.FilterPresenter
import kotlinx.android.synthetic.main.activity_filter.filterMiniPlayer
import kotlinx.android.synthetic.main.activity_filter.ivWheel
import kotlinx.android.synthetic.main.activity_filter.rvFilterCategory
import kotlinx.android.synthetic.main.activity_filter.tvDiscription
import kotlinx.android.synthetic.main.activity_filter.tvSubTitle
import kotlinx.android.synthetic.main.activity_filter.tvTagFiveth
import kotlinx.android.synthetic.main.activity_filter.tvTagFourth
import kotlinx.android.synthetic.main.activity_filter.tvTagOne
import kotlinx.android.synthetic.main.activity_filter.tvTagThree
import kotlinx.android.synthetic.main.activity_filter.tvTagTwo
import kotlinx.android.synthetic.main.activity_filter.wheelView
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle
import java.util.Arrays


class FilterActivity : BaseActivity(), FilterPresenter.ResponseCallBack, RecyclerViewItemListener,
    DialogSesstionExpiredListener {
    private lateinit var filterPresenter: FilterPresenter
    private var tagList: ArrayList<TagResponseItem>? = null
    private var tagAllCategoryList: ArrayList<TagAllCategoryItem>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var filterCategoryAdapter: FilterCategoryAdapter? = null
    var filesTagId: Int = 0
    private var databaseHelper = DatabaseHelper(this)
    var showBackIcon: String = ""
    var backPressed: Int = 0
    var dialogSessionExpired: DialogSessionExpired? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        filterPresenter = FilterPresenter(this)
        tagList = ArrayList()
        tagAllCategoryList = ArrayList()

        initializeMiniAudioPlayer(filterMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)


        setToolbarTitle()
        getIntentFrom()
        setClickListener()
        callTagListingApi()
        initRecyclerView()
        setWheelTagOnClick()
        // create a string array  of tiles
        // create a string array  of tiles
        val titles = arrayOf("Believe", "Composur", "Kaizen", "Discipline", "Awareness")

        // get the reference of the wheelView

        // get the reference of the wheelView

        wheelView.setOnClickListener {

            //  Toast.makeText(this,">>>"+wheelView.titles.get(0),Toast.LENGTH_SHORT).show()
            Toast.makeText(
                this@FilterActivity,
                "wheelView" + wheelView.selectListener.toString(),
                Toast.LENGTH_SHORT
            ).show()

        }

        wheelView.selectListener?.let {

            Toast.makeText(this@FilterActivity, "wheelView" + it.toString(), Toast.LENGTH_SHORT)
                .show()


        }


        // convert tiles array to list and pass it to setTitles

        // convert tiles array to list and pass it to setTitles
        wheelView.titles = Arrays.asList(*titles)

        // Toast.makeText(this@FilterActivity, "wheelView"+   wheelView.get,Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialogSessionExpired != null) {
            dialogSessionExpired!!.dismiss()
        }
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

    override fun onResume() {
        super.onResume()
        initializeMiniAudioPlayer(filterMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)

    }

    /**
     * Here set text lable around wheel
     */
    private fun setWheelTagName() {
        if (tagList!!.size > 0) {
            //here call Read more text
            /*tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
            tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

            tvDiscription.addShowMoreText("Read More");
            tvDiscription.addShowLessText("Read Less");
            tvDiscription.text = htmlToStringFilter(tagList!!.get(0).description).trim()
            tvDiscription.setShowingLine(2)*/

            ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel1))
            tvTagOne.setTextColor(resources.getColor(R.color.red))
            tvTagOne.text = tagList!!.get(0).title

            tvSubTitle.text = tagList!!.get(0).title
            tvDiscription.text = htmlToStringFilter(tagList!!.get(0).description).trim()

            filterPresenter.hitApitoGetTagAllCategory(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appID = SharedPreferencesHelper.getAppID().toInt(),
                tagId = tagList!!.get(0).id!!

            )
        }

        if (tagList!!.size > 1) {
            tvTagTwo.text = tagList!!.get(1).title
        }
        if (tagList!!.size > 2) {
            tvTagThree.text = tagList!!.get(2).title

        }
        if (tagList!!.size > 3) {

            tvTagFourth.text = tagList!!.get(3).title

        }

        if (tagList!!.size > 4) {

            tvTagFiveth.text = tagList!!.get(4).title

        }


    }

    /**
     * here set text lable around wheel
     */
    private fun setWheelTagOnClick() {
        tvTagOne.setOnClickListener {
            if (tagList!!.size > 0) {
                filesTagId = tagList!!.get(0).id!!

                ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel1))
                tvSubTitle.text = tagList!!.get(0).title
                tvDiscription.text = htmlToStringFilter(tagList!!.get(0).description).trim()


                /*tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
                tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

                tvDiscription.addShowMoreText("Read More");
                tvDiscription.addShowLessText("Read Less");
                tvDiscription.text = htmlToStringFilter(tagList!!.get(0).description).trim()
                tvDiscription.setShowingLine(2)*/


                tvTagOne.setTextColor(resources.getColor(R.color.red))
                tvTagTwo.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagThree.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFourth.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFiveth.setTextColor(resources.getColor(R.color.gray_text_))

                filterPresenter.hitApitoGetTagAllCategory(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appID = SharedPreferencesHelper.getAppID().toInt(),
                    tagId = tagList!!.get(0).id!!

                )
            }
        }

        tvTagTwo.setOnClickListener {
            if (tagList!!.size > 1) {
                filesTagId = tagList!!.get(1).id!!

                ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel2))
                tvSubTitle.text = tagList!!.get(1).title
                tvDiscription.text = htmlToStringFilter(tagList!!.get(1).description).trim()

                /*tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
                tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

                tvDiscription.addShowMoreText("Read More")
                tvDiscription.addShowLessText("Read Less")
                tvDiscription.text = htmlToStringFilter(tagList!!.get(1).description).trim()
                tvDiscription.setShowingLine(2)*/

                tvTagOne.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagTwo.setTextColor(resources.getColor(R.color.red))
                tvTagThree.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFourth.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFiveth.setTextColor(resources.getColor(R.color.gray_text_))

                filterPresenter.hitApitoGetTagAllCategory(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appID = SharedPreferencesHelper.getAppID().toInt(),
                    tagId = tagList!!.get(1).id!!

                )
            }

        }

        tvTagThree.setOnClickListener {
            if (tagList!!.size > 2) {
                filesTagId = tagList!!.get(2).id!!

                ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel3))
                tvSubTitle.text = tagList!!.get(2).title
                tvDiscription.text = htmlToStringFilter(tagList!!.get(2).description).trim()

                /*tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
                tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

                tvDiscription.addShowMoreText("Read More");
                tvDiscription.addShowLessText("Read Less");
                tvDiscription.text = htmlToStringFilter(tagList!!.get(2).description).trim()
                tvDiscription.setShowingLine(2)*/

                tvTagOne.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagTwo.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagThree.setTextColor(resources.getColor(R.color.red))
                tvTagFourth.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFiveth.setTextColor(resources.getColor(R.color.gray_text_))

                filterPresenter.hitApitoGetTagAllCategory(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appID = SharedPreferencesHelper.getAppID().toInt(),
                    tagId = tagList!!.get(2).id!!

                )
            }
        }

        tvTagFourth.setOnClickListener {
            if (tagList!!.size > 3) {
                filesTagId = tagList!!.get(3).id!!

                ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel4))
                tvSubTitle.text = tagList!!.get(3).title
                tvDiscription.text = htmlToStringFilter(tagList!!.get(3).description).trim()

                /* tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
                 tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

                 tvDiscription.addShowMoreText("Read More");
                 tvDiscription.addShowLessText("Read Less");
                 tvDiscription.text = htmlToStringFilter(tagList!!.get(3).description).trim()
                 tvDiscription.setShowingLine(2)*/

                tvTagOne.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagTwo.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagThree.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFourth.setTextColor(resources.getColor(R.color.red))
                tvTagFiveth.setTextColor(resources.getColor(R.color.gray_text_))


                filterPresenter.hitApitoGetTagAllCategory(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appID = SharedPreferencesHelper.getAppID().toInt(),
                    tagId = tagList!!.get(3).id!!

                )
            }

        }

        tvTagFiveth.setOnClickListener {
            if (tagList!!.size > 4) {
                filesTagId = tagList!!.get(4).id!!
                ivWheel.setImageDrawable(resources.getDrawable(R.drawable.img_wheel5))
                tvSubTitle.text = tagList!!.get(4).title
                tvDiscription.text = htmlToStringFilter(tagList!!.get(4).description).trim()

                /*tvDiscription.setShowMoreColor(resources.getColor(R.color.dark_brownish_red)); // or other color
                tvDiscription.setShowLessTextColor(resources.getColor(R.color.dark_brownish_red)); // or other color

                tvDiscription.addShowMoreText("Read More");
                tvDiscription.addShowLessText("Read Less");
                tvDiscription.text = htmlToStringFilter(tagList!!.get(4).description).trim()
                tvDiscription.setShowingLine(2)*/


                tvTagOne.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagTwo.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagThree.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFourth.setTextColor(resources.getColor(R.color.gray_text_))
                tvTagFiveth.setTextColor(resources.getColor(R.color.red))

                filterPresenter.hitApitoGetTagAllCategory(
                    token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                    appID = SharedPreferencesHelper.getAppID().toInt(),
                    tagId = tagList!!.get(4).id!!

                )
            }


        }
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)

        filterCategoryAdapter = FilterCategoryAdapter(this, tagAllCategoryList!!, this)

        rvFilterCategory.apply {
            layoutManager = linearLayoutManager
            adapter = filterCategoryAdapter
        }
    }


    private fun callTagListingApi() {

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("Please wait...")
            filterPresenter.hitApitoGetTagListing(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appID = SharedPreferencesHelper.getAppID().toInt()
            )

        }

    }


    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.dbacks_pillars)
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
     * here call click On BackPress
     */
    private fun clickOnBackPress() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onBackPressed() {
        if (showBackIcon.equals("ShowBackButton")) {
            clickOnBackPress()

        } else {

            this.parent.onBackPressed()

        }
    }


    override fun onResponseSuccess(response: TagResponseModel) {
        if (this@FilterActivity.baseContext != null) {
            runOnUiThread {
                if (this@FilterActivity.baseContext != null) {
                    hideProgressDialog()

                    if (response.data!!.size > 0) {

                        tagList!!.clear()
                        tagList!!.addAll(response.data)
                        filesTagId = tagList!!.get(0).id!!



                        setWheelTagName()
                        Log.d(TAG, "tagList: " + tagList!!.size)
                    }
                }
            }
        }
    }

    override fun onTagAllCategoryResponseSuccess(response: TagAllCategoryResModel) {
        if (this@FilterActivity.baseContext != null) {
            runOnUiThread {
                if (this@FilterActivity.baseContext != null) {
                    hideProgressDialog()

                    if (response.data!!.size > 0) {
                        tagAllCategoryList!!.clear()
                        tagAllCategoryList!!.addAll(response.data)
                        filterCategoryAdapter!!.notifyDataSetChanged()

                        Log.d(TAG, "tagAllCategoryList: " + tagList!!.size)

                    }


                }
            }
        }
    }


    override fun onFailureResponse(message: String) {
        if (this@FilterActivity.baseContext != null) {
            runOnUiThread {
                if (this@FilterActivity.baseContext != null) {
                    hideProgressDialog()
                    if (message.equals("Unauthorized")) {

                        if (dialogSessionExpired == null) {

                            dialogSessionExpired = DialogSessionExpired(
                                listener = this,
                                message = getString(R.string.session_expired),
                                context = this,

                                )
                        }

                        try {
                            // dialogSessionExpired!!.show()

                            if (dialogSessionExpired!!.isShowing && dialogSessionExpired != null) {
                                dialogSessionExpired!!.dismiss()
                            }

                            if (!isFinishing && !isDestroyed && !dialogSessionExpired!!.isShowing) {
                                dialogSessionExpired!!.show()
                            }

                        } catch (e: WindowManager.BadTokenException) {
                            //use a log message
                            Log.d(TAG, "BadTokenException" + e.message)
                        }

                        //  showSestionExpiredtDialog("You are logged in with another device to continue with this device you need to login again",this,this)

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
        val data = model as TagAllCategoryItem
        when (itemClickType) {
            ItemClickType.DETAIL -> clickedCategoryItem(
                model = data,
                position = position

            )

            else -> {
                showToast("wrong click type")
            }

        }

    }

    private fun clickedCategoryItem(model: TagAllCategoryItem, position: Int) {

        /*    if (model.files!!.size > 0) {

               filesTagId = model.files.get(0).prodVideoTags!!.get(0)!!.tagId!!

             }*/
        startActivity(
            Intent(
                this@FilterActivity,
                AllVideoListActivity::class.java

            ).putExtra(Constants.App.TAG_ID, filesTagId)
                .putExtra(Constants.App.CATEGORY_ID, model.id)
                .putExtra(Constants.App.FROM, "FilterActivity")
                .putExtra(Constants.App.CATEGORY_NAME, model.name)
                .putExtra(Constants.App.TGA_NAME, tvSubTitle.text.toString())
                .putExtra(Constants.App.CATEGORY_ITEMS_COUNT, model.filesCount.toString())
        )

    }

    /**
     * here remove HTML tags from a String
     */
    fun htmlToStringFilter(textToFilter: String?): String {
        var text: String? = null

        if (textToFilter != null) {
            text = Html.fromHtml(textToFilter).toString()
        } else {
            text = ""
        }
        return text
    }

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }


    }

    fun clearLocalDB() {
        if (!isFinishing && dialogSessionExpired != null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
        onSessionExpiredClearLocalDB(databaseHelper)
        startActivity(
            Intent(
                this@FilterActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }

}