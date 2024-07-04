package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.GsonHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.adapter.JournalAdapter
import com.fivestarmind.android.mvp.model.response.CommonListingResponse
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.JournalListingResponseModel
import com.fivestarmind.android.mvp.presenter.JournalListingPresenter
import kotlinx.android.synthetic.main.activity_journal_listing.*
import kotlinx.android.synthetic.main.layout_toolbar_journal.*

class JournalListingActivity : BaseActivity(), JournalListingPresenter.ResponseCallBack {

    override val TAG = JournalListingActivity::class.java.simpleName
    private lateinit var presenter: JournalListingPresenter

    private var journalAdapter: JournalAdapter? = null
    private var journalLayoutManager: LinearLayoutManager? = null
    private var journalList: ArrayList<JournalListingResponseModel>? = null

    private var loadMore = false
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_listing)

        setToolbarTitle()
        initData()
        initRecyclerViewWithAdapter()

        initPresenter()
        clickListener()
    }

    override fun onResume() {
        super.onResume()
        currentPage = 0
    }

    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.journal)
    }

    /**
     * Here initializing the data
     */
    private fun initData() {
        journalList = arrayListOf()
    }

    /**
     * Here recyclerViews are setup with it's adapter
     */
    private fun initRecyclerViewWithAdapter() {
        // Here setup is done for SelectUsers
        journalAdapter = JournalAdapter(
            journalList = journalList!!,
            activity = this@JournalListingActivity
        )
        journalLayoutManager =
            LinearLayoutManager(this@JournalListingActivity, RecyclerView.VERTICAL, false)

        rvJournals.apply {
            layoutManager = this@JournalListingActivity.journalLayoutManager
            adapter = journalAdapter
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = JournalListingPresenter(this)
        checkForLogin()
    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLogin() {
        when (AppHelper.isUserLoggedIn()) {
            true -> hitApiToGetJournalList()
            false -> callToOpenLoginActivity()
        }
    }

    /**
     * Call for api get journals
     */
    private fun hitApiToGetJournalList() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.getJournalList(
                SharedPreferencesHelper.getAuthToken(),
                ++currentPage,
                Constants.App.Number.FIFTEEN
            )

            addOnScrollListener()
        }
    }

    /**
     * Click event on view ivMenu
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()
            }
    }

    private fun addOnScrollListener() {
        rvJournals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val total = journalLayoutManager!!.itemCount
                    val lastVisibleItemCount = journalLayoutManager!!.findLastVisibleItemPosition()
                    if (loadMore) {
                        if (total > 0)
                            if (total - 1 == lastVisibleItemCount) {
                                loadMore = false
//                                hitApiToGetJournalList()
                                presenter.getJournalList(
                                    SharedPreferencesHelper.getAuthToken(),
                                    ++currentPage,
                                    Constants.App.Number.FIFTEEN
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

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.JOURNAL
        )
    }

    /**
     * Here is updating the UI for empty list
     */
    private fun updateUIForEmptyList() {
        tvEmptyMsg.visibility = View.VISIBLE
        tvEmptyMsg.text = getString(R.string.no_journals_available)
    }

    /**
     * Here is adding data to list
     */
    private fun addDataToList(listData: ArrayList<JournalListingResponseModel>) {
        journalList?.addAll(listData)
        journalAdapter?.notifyDataSetChanged()
    }

    /**
     * When successful response or data retrieved from Api get Journals
     *
     * @param response is successful response of Api
     */
    override fun onResponseSuccess(response: CommonListingResponse) {
        if (this@JournalListingActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                if (response.data!!.isEmpty()) {
                    updateUIForEmptyList()
                } else {
                    rvJournals.visibility = View.VISIBLE

                    currentPage = response.currentPage

                    if (response.nextPageUrl != null)
                        loadMore = true

                    if (response.data != null) {
                        val journalListingData: ArrayList<JournalListingResponseModel>? =
                            GsonHelper.convertJsonStringToJavaObject(
                                from = response.data!!,
                                to = object :
                                    TypeToken<ArrayList<JournalListingResponseModel>>() {}.type
                            ) as ArrayList<JournalListingResponseModel>?

                        addDataToList(
                            listData = journalListingData!!
                        )
                    }
                }

                pbLoadMore.visibility = View.GONE
            }
        }
    }

    /**
     * When error occurred in response of api get Journal
     *
     * @param errorResponse for Error Message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@JournalListingActivity.baseContext != null) {
            runOnUiThread {
                if (this@JournalListingActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.JOURNAL -> {
                        currentPage = 0
                        journalList?.clear()

                        hitApiToGetJournalList()
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                finish()
            }
        }
    }

    override fun onBackPressed() = finish()

}
