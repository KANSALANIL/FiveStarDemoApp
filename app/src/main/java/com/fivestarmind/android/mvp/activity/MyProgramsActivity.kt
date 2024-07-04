package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.adapter.MyProgramAdapter
import com.fivestarmind.android.mvp.model.response.MyProgramsResponseDataModel
import com.fivestarmind.android.mvp.model.response.MyProgramsResponseModel
import com.fivestarmind.android.mvp.presenter.MyProgramsPresenter
import kotlinx.android.synthetic.main.activity_my_program.*
import kotlinx.android.synthetic.main.layout_toolbar.*


class MyProgramsActivity : BaseActivity(), MyProgramsPresenter.ResponseCallBack {

    override val TAG = MyProgramsActivity::class.java.simpleName

    private var linearLayoutManager: LinearLayoutManager? = null
    private var presenter: MyProgramsPresenter? = null
    private val programsList = arrayListOf<MyProgramsResponseDataModel>()

    private var loadMore = false
    private var currentPage = 0
    private var myProgramAdapter = MyProgramAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_program)

        setToolbarTitle()
        initRecyclerView()

        initPresenter()
        init()

        setClickListener()
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.my_program)
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)
        rvPrograms.layoutManager = linearLayoutManager

        rvPrograms.apply {
            layoutManager = linearLayoutManager
            adapter = myProgramAdapter
        }
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = MyProgramsPresenter(this, this)
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        if (shouldProceedClick())
            ivBackMenu.setOnClickListener { onBackPressed() }
    }

    private fun init() {
        callToHitApiForGetProductProcess()
    }

    /**
     * Here connecting to server and uses UserProducts API
     */
    private fun callToHitApiForGetProductProcess() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")

            presenter!!.apiToGetProductResponse(
                SharedPreferencesHelper.getAuthToken(),
                ++currentPage,
                limit = Constants.App.Number.FIFTEEN
            )

            addOnScrollListener()
        }
    }

    private fun addOnScrollListener() {
        rvPrograms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val total = linearLayoutManager!!.itemCount
                    val lastVisibleItemCount = linearLayoutManager!!.findLastVisibleItemPosition()
                    if (loadMore) {
                        if (total > 0)
                            if (total - 1 == lastVisibleItemCount) {
                                loadMore = false
                                presenter!!.apiToGetProductResponse(
                                    SharedPreferencesHelper.getAuthToken(),
                                    ++currentPage,
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

    /**
     * when success response from api get user products
     *
     * @param response Success Response
     */
    override fun onResponseSuccess(response: MyProgramsResponseModel) {
        if (this@MyProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyProgramsActivity.baseContext != null) {

                    hideProgressDialog()
                    currentPage = response.currentPage!!

                    if (response.nextPageUrl != null)
                        loadMore = true

                    if (response.data!!.size > 0) {
                        programsList.addAll(response.data)
                        myProgramAdapter.updateResponseList(programsList)
                    } else
                        tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * when error occurred in success response
     *
     * @param errorResponse Error Response
     */
    override fun onFailureResponse(errorResponse: String) {
        if (this@MyProgramsActivity.baseContext != null) {
            runOnUiThread {
                if (this@MyProgramsActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
