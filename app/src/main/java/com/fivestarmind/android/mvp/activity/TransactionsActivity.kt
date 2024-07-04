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
import com.fivestarmind.android.mvp.adapter.TransactionsAdapter
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.TransactionsPresenter
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class TransactionsActivity : BaseActivity(), TransactionsPresenter.ResponseCallBack {

    override val TAG = TransactionsActivity::class.java.simpleName
    private lateinit var presenter: TransactionsPresenter

    private var transactionsAdapter: TransactionsAdapter? = null
    private var transactionsLayoutManager: LinearLayoutManager? = null
    private var transactionsList: ArrayList<TransactionsResponseDataModel>? = null

    private var loadMore = false
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

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
        tvTitle.text = getString(R.string.transactions)
    }

    /**
     * Here initializing the data
     */
    private fun initData() {
        transactionsList = arrayListOf()
    }

    /**
     * Here recyclerViews are setup with it's adapter
     */
    private fun initRecyclerViewWithAdapter() {
        // Here setup is done for SelectUsers
        transactionsAdapter = TransactionsAdapter(
            transactionsList = transactionsList!!
        )

        transactionsLayoutManager =
            LinearLayoutManager(this@TransactionsActivity, RecyclerView.VERTICAL, false)

        rvTransactions.apply {
            layoutManager = this@TransactionsActivity.transactionsLayoutManager
            adapter = transactionsAdapter
        }

        addOnScrollListener()
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = TransactionsPresenter(this)
        checkForLogin()
    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLogin() {
        when (AppHelper.isUserLoggedIn()) {
            true -> callGetJournalListApiProcess()
            false -> callToOpenLoginActivity()
        }
    }

    private fun callGetJournalListApiProcess() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            hitApiToGetJournalList()
        }
    }

    /**
     * Call for api get journals
     */
    private fun hitApiToGetJournalList() {
        presenter.getJournalList(
            SharedPreferencesHelper.getAuthToken(), ++currentPage,
            limit = Constants.App.Number.FIFTEEN
        )

        addOnScrollListener()
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
        rvTransactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val total = transactionsLayoutManager!!.itemCount
                    val lastVisibleItemCount =
                        transactionsLayoutManager!!.findLastVisibleItemPosition()
                    if (loadMore) {
                        if (total > 0)
                            if (total - 1 == lastVisibleItemCount) {
                                loadMore = false

                                presenter.getJournalList(
                                    SharedPreferencesHelper.getAuthToken(), ++currentPage,
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

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.TRANSACTIONS
        )
    }

    /**
     * Here is updating the UI for empty list
     */
    private fun updateUIForEmptyList() {
        tvEmptyMsg.visibility = View.VISIBLE
        tvEmptyMsg.text = getString(R.string.no_transactions_available)
    }

    /**
     * Here is adding data to list
     */
    private fun addDataToList(listData: ArrayList<TransactionsResponseDataModel>) {
        transactionsList?.addAll(listData)
        transactionsAdapter?.notifyDataSetChanged()
    }

    /**
     * When successful response or data retrieved from Api get Journals
     *
     * @param response is successful response of Api
     */
    override fun onResponseSuccess(response: TransactionsResponseModel) {
        if (this@TransactionsActivity.baseContext != null) {
            runOnUiThread {
                hideProgressDialog()

                if (response.data!!.isEmpty()) {
                    updateUIForEmptyList()
                } else {
                    rvTransactions.visibility = View.VISIBLE

                    currentPage = response.currentPage

                    if (response.nextPageUrl != null)
                        loadMore = true

                    val transactionListingData: ArrayList<TransactionsResponseDataModel>? =
                        GsonHelper.convertJsonStringToJavaObject(
                            from = response.data,
                            to = object :
                                TypeToken<ArrayList<TransactionsResponseDataModel>>() {}.type
                        ) as ArrayList<TransactionsResponseDataModel>?

                    addDataToList(
                        listData = transactionListingData!!
                    )
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
        if (this@TransactionsActivity.baseContext != null) {
            runOnUiThread {
                if (this@TransactionsActivity.baseContext != null) {
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
                        transactionsList?.clear()

                        callGetJournalListApiProcess()
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
