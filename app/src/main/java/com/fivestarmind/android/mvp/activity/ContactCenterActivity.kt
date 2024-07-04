package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivestarmind.android.*
import com.fivestarmind.android.application.AppController
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.CoachingStaffAdapter
import com.fivestarmind.android.mvp.adapter.ProductCategoryAdapter
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import com.fivestarmind.android.mvp.model.response.UserRoleResponseModel
import com.fivestarmind.android.mvp.model.response.UsersDataItemListing
import com.fivestarmind.android.mvp.model.response.UsersListingResponseModel
import com.fivestarmind.android.mvp.presenter.ContactCenterPresenter
import kotlinx.android.synthetic.main.activity_chat_user_list.chatListAudioPlayerView
import kotlinx.android.synthetic.main.activity_contact_center.contactCenterAudioPlayerView
import kotlinx.android.synthetic.main.activity_contact_center.etSearch
import kotlinx.android.synthetic.main.activity_contact_center.rvCoachingStaff
import kotlinx.android.synthetic.main.activity_contact_center.rvProductsCategories
import kotlinx.android.synthetic.main.activity_contact_center.tvNoDataFound
import kotlinx.android.synthetic.main.activity_contact_center.tvSubTitle
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.ivBackMenu
import kotlinx.android.synthetic.main.layout_toolbar.ivChat
import kotlinx.android.synthetic.main.layout_toolbar.tvTitle


class ContactCenterActivity : BaseActivity(), RecyclerViewItemListener, ProgramInterface,
    ContactCenterPresenter.ResponseCallBack {
    private var productCategoryAdapter: ProductCategoryAdapter? = null
    private var coachingStaffAdapter: CoachingStaffAdapter? = null
    private var productCategoryList: ArrayList<ProductCategoryDataModel>? = null
    private var usersListing: ArrayList<UsersDataItemListing>? = null
    private lateinit var presenter: ContactCenterPresenter
    private var productCategoryId = ""
    private var updateFilterName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_center)
        initData()
        searchCoach()
        callGetUserRoleApi()
        clickListener()
        initRecyclerView()
        updateCategoryIdInAdapter()
       // initializeMiniAudioPlayer(contactCenterAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)

        if(AppController.simpleExoplayer!!.isPlaying){
            AppController.simpleExoplayer!!.pause()
        }
    }

    override fun onResume() {
        super.onResume()
       // initializeMiniAudioPlayer(contactCenterAudioPlayerView, tvAudioTitleName, ivExoIcon, progressBarExo)
        if(AppController.simpleExoplayer!!.isPlaying){
            AppController.simpleExoplayer!!.pause()
        }
    }

    private fun callGetUserRoleApi() {
        if (isInternetConnected(shouldShowToast = true)) {
            // presenter.apiToGetProductCategories()
            showProgressDialog("")
            presenter.hitGetUserRoleApi(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt()
            )

        }
    }

    private fun callGetUsersListingApi(filter:String,search:String){
        if (isInternetConnected(shouldShowToast = true)) {
            presenter.hitGetUsersListingApi(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                filter = filter,
                search = search

            )
        }
    }

    private fun searchCoach() {
        etSearch.afterTextChanged { clickOnSearch(it) }
        etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        etSearch.clearFocus();
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0)
    }

    private fun initData() {
        presenter = ContactCenterPresenter(this)
        productCategoryList = arrayListOf()
        usersListing = arrayListOf()
        ivChat.visibility = View.VISIBLE
        setTitle()


    /*if (isInternetConnected(shouldShowToast = true)) {
            presenter.apiToGetProductCategories(SharedPreferencesHelper.getAuthToken())
        }*/
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        ivChat.setOnClickListener(clickListener)
   //     etSearch.setOnClickListener(clickListener)

    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()
                R.id.ivChat -> gotoChatUserListActivity()

            }
    }

    private fun clickOnSearch(it: String) {
      //  showProgressDialog("")

        if(it.length > 1 || it.length == 0) {
            presenter.hitGetUsersListingApi(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt(),
                filter = updateFilterName,
                search = it

            )
        }
    }

    private fun gotoChatUserListActivity(){

        startActivity(Intent(this@ContactCenterActivity, ChatUserListActivity::class.java).putExtra(Constants.App.FROM,"ShowBackButton"))
    }

    private fun setTitle() {
        tvTitle.text = getString(R.string.contact_center_cap)
    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        productCategoryAdapter =
            ProductCategoryAdapter(this@ContactCenterActivity, productCategoryList!!, this)

        rvProductsCategories.apply {
            layoutManager = LinearLayoutManager(
                this@ContactCenterActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = productCategoryAdapter
        }


        coachingStaffAdapter =
            CoachingStaffAdapter(this@ContactCenterActivity, usersListing!!,this)

        rvCoachingStaff.apply {
            layoutManager = LinearLayoutManager(
                this@ContactCenterActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = coachingStaffAdapter
        }
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        val data = model as UsersDataItemListing


        when (itemClickType) {
            ItemClickType.DETAIL -> onClickUserItem(data, position)
            else ->
                Log.e(TAG, "onRecyclerViewItemClick: itemClickType- INVALID")

        }

    }

    private fun onClickUserItem(data: UsersDataItemListing, position: Int) {

        startActivity(
            Intent(this@ContactCenterActivity, ChatActivity::class.java).putExtra(Constants.App.MESSAGE_USER_ID,data.id.toString()).putExtra(Constants.App.MESSAGE_USER_NAME,data.name).putExtra(Constants.App.MESSAGE_USER_IMAGE,data.profileImg))

    }


    private fun updateCategoryIdInAdapter() {
        productCategoryAdapter?.updateCategoryId(productCategoryId)
        productCategoryAdapter?.notifyDataSetChanged()



    }


    override fun onItemSelected(position: Int, productId: Int) {
    }

    override fun onListEmpty(isEmpty: Boolean) {
    }

    override fun onProductCategorySelected(position: Int, productId: Int, categoryName: String) {
        updateFilterName = categoryName
        productCategoryId = productId.toString()
        if (productCategoryList!!.size>0) {
            tvSubTitle.text = "Coaching "+ categoryName
        }
        updateCategoryIdInAdapter()
        callGetUsersListingApi(productCategoryList!!.get(position).name,"")

    }

    override fun onUserRoleResponseSuccess(response: UserRoleResponseModel) {
        if (this@ContactCenterActivity.baseContext != null) {
            runOnUiThread {
                if (this@ContactCenterActivity.baseContext != null) {
                    hideProgressDialog()

                    addDataToProductCategoryList(categoryList = response.data!!)

                    /*if (!isCategorySelected)
                        getFirstCategoryId(categoryList = response.data)
                    else hitApiToGetProductWithProductId()*/
                }
            }
        }
    }

    override fun onUsersListingResponseSuccess(response: UsersListingResponseModel) {
        if (this@ContactCenterActivity.baseContext != null) {
            runOnUiThread {
                if (this@ContactCenterActivity.baseContext != null) {
                    hideProgressDialog()

                    addDataToUsersListing(response.data)
                }
            }
        }
    }

    override fun onFailureResponse(message: String) {
        if (this@ContactCenterActivity.baseContext != null) {
            runOnUiThread {
                if (this@ContactCenterActivity.baseContext != null) {
                    hideProgressDialog()
                    Toast.makeText(this@ContactCenterActivity, message, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onUsersListingFailureResponse(message: String) {
        if (this@ContactCenterActivity.baseContext != null) {
            runOnUiThread {
                if (this@ContactCenterActivity.baseContext != null) {
                    hideProgressDialog()

                    tvNoDataFound.visibility=View.VISIBLE

                    Toast.makeText(this@ContactCenterActivity, message, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun addDataToUsersListing(usersDataItemListing: ArrayList<UsersDataItemListing>?) {
        usersListing!!.clear()

        if (usersDataItemListing!!.isNotEmpty()) {
            tvNoDataFound.visibility=View.GONE
            usersListing!!.addAll(usersDataItemListing)
        }else{
            tvNoDataFound.visibility=View.VISIBLE

        }

           coachingStaffAdapter!!.notifyDataSetChanged()
    }


    private fun addDataToProductCategoryList(categoryList: ArrayList<ProductCategoryDataModel>?) {
        productCategoryList!!.clear()

        if (categoryList!!.isNotEmpty()) {
            productCategoryList!!.addAll(categoryList)
            tvSubTitle.text = "Coaching "+ productCategoryList!!.get(0).name
            productCategoryAdapter?.updateCategoryId(productCategoryList!!.get(0).id)
            updateFilterName = productCategoryList!!.get(0).name
            callGetUsersListingApi(productCategoryList!!.get(0).name,"")

        }

        productCategoryAdapter?.notifyDataSetChanged()
    }




    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

}