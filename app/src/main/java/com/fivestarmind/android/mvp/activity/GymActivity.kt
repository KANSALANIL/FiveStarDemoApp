package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivestarmind.android.R
import com.fivestarmind.android.database.DatabaseHelper
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.DialogSesstionExpiredListener
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.GymCategoryAdapter
import com.fivestarmind.android.mvp.dialog.DialogSessionExpired
import com.fivestarmind.android.mvp.model.response.ProductCategoryDataModel
import com.fivestarmind.android.mvp.model.response.ProductCategoryResponseModel
import com.fivestarmind.android.mvp.model.response.TagResponseItem
import com.fivestarmind.android.mvp.presenter.GymPresenter
import com.fivestarmind.android.retrofit.ApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_filter.rvFilterCategory
import kotlinx.android.synthetic.main.activity_gym.gymMiniPlayer
import kotlinx.android.synthetic.main.activity_gym.ivLogoImage
import kotlinx.android.synthetic.main.activity_gym.pbGym
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName

class GymActivity : BaseActivity(), RecyclerViewItemListener, GymPresenter.ResponseCallBack,
    DialogSesstionExpiredListener {
    private lateinit var gymPresenter: GymPresenter
    private var tagList: ArrayList<TagResponseItem>? = null
    private var productCategoryList: java.util.ArrayList<ProductCategoryDataModel>? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var gymCategoryAdapter: GymCategoryAdapter? = null
    private var databaseHelper = DatabaseHelper(this)
    private var dialogSessionExpired: DialogSessionExpired? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym)
        init()
        setLogoImage()
        callProductCategoriesApi()
        initRecyclerView()
        initializeMiniAudioPlayer(gymMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)
    }

    override fun onResume() {
        super.onResume()
        callProductCategoriesApi()

        initializeMiniAudioPlayer(gymMiniPlayer, tvAudioTitleName, ivExoIcon, progressBarExo)
    }

    private fun init() {
        gymPresenter = GymPresenter(this)
        tagList = ArrayList()
        productCategoryList = ArrayList()
    }

    private fun setLogoImage() {
        pbGym.visibility = View.VISIBLE

        Picasso.get()
            .load(ApiClient.BASE_URL_PROFILE + SharedPreferencesHelper.getLogoImage())
            .placeholder(R.drawable.app_logo_placeholder)
            //   .into(ivOrganisationLogo, object : Callback {
            .into(ivLogoImage, object : Callback {
                override fun onSuccess() {
                    pbGym.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    pbGym.visibility = View.GONE
                }
            })
    }

    private fun callProductCategoriesApi() {

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("Please wait...")
            gymPresenter.apiToGetProductCategories(
                token = "Bearer " + SharedPreferencesHelper.getAuthToken(),
                appId = SharedPreferencesHelper.getAppID().toInt()
            )
        }

    }

    override fun onBackPressed() {

        this.parent.onBackPressed()

    }


    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)

        gymCategoryAdapter = GymCategoryAdapter(this, productCategoryList!!, this)

        rvFilterCategory.apply {
            layoutManager = linearLayoutManager
            adapter = gymCategoryAdapter
        }
    }

    fun clearLocalDB() {
        if (!isFinishing && dialogSessionExpired!=null && dialogSessionExpired!!.isShowing) {
            dialogSessionExpired!!.dismiss()
        }
        onSessionExpiredClearLocalDB(databaseHelper)
        startActivity(
            Intent(
                this@GymActivity,
                SignInActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()

    }

    override fun onDialogSesstionExpiredListener(dialogEventType: DialogEventType) {
        Log.d(TAG, "onDialogSesstionExpiredListener:")

        when (dialogEventType) {
            DialogEventType.POSITIVE -> clearLocalDB()

            else -> {}
        }

    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {

        val data = model as ProductCategoryDataModel
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

    private fun clickedCategoryItem(model: ProductCategoryDataModel, position: Int) {

        startActivityForResult(
            Intent(this@GymActivity, MenuDetailActivity::class.java)
                .putExtra("type", model.name)
                .putExtra("productId", model.id.toInt()),
            Constants.App.RequestCode.categorySelectedRequestCode
        )

    }

    override fun onProductCategoriesResponseSuccess(response: ProductCategoryResponseModel) {
        if (this@GymActivity.baseContext != null) {
            runOnUiThread {
                if (this@GymActivity.baseContext != null) {
                    hideProgressDialog()

                    addDataToProductCategoryList(categoryList = response.data!!.data!!)

                    /*if (!isCategorySelected)
                        getFirstCategoryId(categoryList = response.data)
                    else hitApiToGetProductWithProductId()*/
                }
            }
        }
    }

    private fun addDataToProductCategoryList(categoryList: ArrayList<ProductCategoryDataModel>?) {
        productCategoryList!!.clear()

        if (categoryList!!.isNotEmpty()) {
            productCategoryList!!.addAll(categoryList)

            gymCategoryAdapter?.notifyDataSetChanged()
        }


    }

    override fun onResFailure(message: String) {
        if (this@GymActivity.baseContext != null) {
            runOnUiThread {
                if (this@GymActivity.baseContext != null) {
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



                       //   showSestionExpiredtDialog("You are logged in with another device to continue with this device you need to login again",this,this)

                    }

                }
            }
        }
    }
}