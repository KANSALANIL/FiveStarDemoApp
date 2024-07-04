package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.dialog.PositiveAlertDialog
import com.fivestarmind.android.enum.DialogEventType
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.adapter.CartAdapter
import com.fivestarmind.android.mvp.model.request.CouponRequestModel
import com.fivestarmind.android.mvp.model.response.*
import com.fivestarmind.android.mvp.presenter.CartPresenter
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class CartActivity : BaseActivity(), ProgramInterface, CartPresenter.ResponseCallBack {

    override var TAG = CartActivity::class.java.simpleName
    private var cartResponse = CartResponseModel()
    private lateinit var presenter: CartPresenter
    private var couponRequestModel = CouponRequestModel()
    private var cartAdapter: CartAdapter? = null
    private var cartLayoutManager: LinearLayoutManager? = null
    private var coupon = ""
    private var couponDiscount = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initUI()
        initPresenter()

        initRecyclerView()
        clickListener()
    }

    private fun initUI() {
        tvTitle.text = getString(R.string.cart)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = CartPresenter(this)
        checkForLogin()
    }

    /**
     * Check for user logged in or not
     */
    private fun checkForLogin() {
        when (AppHelper.isUserLoggedIn()) {
            true ->
                apiToGetCartDetails()

            false -> {
                startActivityForResult(
                    Intent(this, SignInActivity::class.java)
                        .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
                    Constants.App.RequestCode.CART
                )
            }
        }
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        cartAdapter = CartAdapter(this, this)

        cartLayoutManager = LinearLayoutManager(this@CartActivity, RecyclerView.VERTICAL, false)

        rvCart.apply {
            layoutManager = cartLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = cartAdapter
        }
    }

    /**
     * Click event on views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        tvApply.setOnClickListener(clickListener)
        ivCross.setOnClickListener(clickListener)
        btnCheckout.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

                R.id.tvApply -> clickedApply()

                R.id.ivCross -> clickedCrossToRemoveAppliedCoupon()

                R.id.btnCheckout -> clickedCheckout()
            }
    }

    /**
     * It is called when user click on apply view
     */
    private fun clickedApply() {
        if (etPromoCode.text.isNotEmpty())
            apiHitToApplyCoupon()
        else
            showToast(getString(R.string.empty_coupon_msg))
    }

    /**
     * It is called when user clicked on cross view to remove applied coupon
     */
    private fun clickedCrossToRemoveAppliedCoupon() {
        showHideViewForAppliedCoupon(isCouponApplied = false)

        coupon = ""
        apiToGetCartDetails()
    }

    /**
     * It is called when user click on checkout button
     */
    private fun clickedCheckout() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.apiToGetCouponsList(SharedPreferencesHelper.getAuthToken())
        }
    }

    /**
     * call for api get cart details
     */
    private fun apiToGetCartDetails() {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.getCartProductDetails(SharedPreferencesHelper.getAuthToken())
        }
    }

    /**
     * call for api hit to remove product from cart
     */
    private fun apiHitToRemoveProductFromCart(productId: Int) {
        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.apiToRemoveProductFromCart(SharedPreferencesHelper.getAuthToken(), productId)
        }
    }

    /**
     * call for api to apply coupon on cart items
     */
    private fun apiHitToApplyCoupon() {
        couponRequestModel.apply {
            number = etPromoCode.text.toString()
        }

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            presenter.apiToApplyCoupon(SharedPreferencesHelper.getAuthToken(), couponRequestModel)
        }
    }

    /**
     * Here is updating the calculation in views
     *
     * @param cartDetails
     */
    private fun setCartDetails(cartDetails: CartResponseModel) {
        updateSubTotal(cartDetails.totalAmount)
        updateDiscountValue(cartDetails.totalDiscount)
        updateTotal(cartDetails.totalAfterDiscount)
    }

    /**
     * Here is updating the subtotal in view
     *
     * @param subTotal price of the product
     */
    private fun updateSubTotal(subTotal: String) {
        if (subTotal.isNotEmpty())
            tvSubTotalValue.text = formatPrice(this, subTotal.toBigDecimal())
    }

    /**
     * Here is updating the discount in view
     *
     * @param discount price of the product
     */
    private fun updateDiscountValue(discount: String) {
        if (discount.isNotEmpty())
            tvDiscountValue.text = "-" + formatPrice(this, discount.toBigDecimal())
    }

    /**
     * Here is updating the total price of product in view
     *
     * @param total price of the product
     */
    private fun updateTotal(total: String) {
        if (total.isNotEmpty())
            tvTotalValue.text = formatPrice(this, total.toBigDecimal())
    }

    /**
     * Here is calculating the coupon discount
     *
     * @param couponDiscount
     */
    fun updateCouponDiscount(couponDiscount: Double) {
        this.couponDiscount = couponDiscount.toString()
        val subTotal: String = this.cartResponse.totalAmount
        val total: Double = (subTotal.toDouble() - couponDiscount)

        updateDiscountValue(discount = couponDiscount.toString())
        updateTotal(total = total.toString())
    }

    /**
     * Here is updating the UI for applied coupon
     *
     * @param responseCouponResponseModel
     */
    private fun updateUICouponApplied(responseCouponResponseModel: CouponResponseModel.CouponResponseFirstModel) {
        etPromoCode.text.clear()

        showHideViewForAppliedCoupon(isCouponApplied = true)

        tvCouponName.text = "(" + responseCouponResponseModel.coupon.number + ")"
        tvDiscountVal.text = getString(
            R.string.format_price_discount_on_code,
            responseCouponResponseModel.coupon.discount
        )
    }

    internal fun updateProductList(cartResponseModel: ArrayList<CartProductResponseModel>) {
        this.cartResponse.product = cartResponseModel
    }

    private fun showHideViewForAppliedCoupon(isCouponApplied: Boolean) {
        if (isCouponApplied) {
            clAppliedPromoCode.visibility = View.VISIBLE
            clPromoCode.visibility = View.GONE
        } else {
            clPromoCode.visibility = View.VISIBLE
            clAppliedPromoCode.visibility = View.GONE
        }
    }

    private fun showHideViewForEmptyCart(cartResponseModel: CartResponseModel) {
        if (cartResponseModel.product.isNotEmpty()) {
            constraintLayoutCart.visibility = View.VISIBLE
        } else {
            constraintLayoutCart.visibility = View.GONE
            showAlert()
        }
    }

    private fun afterCouponListSuccess(couponResponseModel: CouponResponseModel.CouponResponseSecondModel) {
        var isCouponFound = false

        if (coupon == "") {
            isCouponFound = true

        } else {
            for (i in 0 until couponResponseModel.coupons.size) {

                if (coupon.equals(couponResponseModel.coupons[i].number, true))
                    isCouponFound = true
            }
        }

        if (isCouponFound) {
            val intent = Intent(this@CartActivity, CardDetailActivity::class.java)
            intent.putExtra(Constants.App.COUPON, coupon)
            this.startActivityForResult(intent, Constants.App.RequestCode.CART_SCREEN)
        } else
            showAlert(getString(R.string.invalid_coupon_code))
    }

    /**
     * When successful response or data retrieved from get cart details Api
     *
     * @param response is successful response from Api
     */
    override fun onSuccessResponse(response: CartResponseModel) {
        if (this@CartActivity.baseContext != null) {
            runOnUiThread {
                if (this@CartActivity.baseContext != null) {
                    hideProgressDialog()
                    showHideViewForEmptyCart(cartResponseModel = response)

                    this.cartResponse = response
                    cartAdapter!!.updateCartList(this.cartResponse.product)
                    setCartDetails(response)
                }
            }
        }
    }

    /**
     * When successful response for api remove product from cart
     *
     * @param response is successful response from Api
     */
    override fun onSuccessToRemoveProduct(response: CommonResponse, productId: Int) {
        if (this@CartActivity.baseContext != null) {
            runOnUiThread {
                if (this@CartActivity.baseContext != null) {
                    hideProgressDialog()
                    cartAdapter!!.removeProduct(productId)
                }
            }
        }
    }

    /**
     * When successful response for api applying coupon on product
     *
     * @param response is successful response from Api
     */
    override fun onSuccessCouponResponse(response: CouponResponseModel.CouponResponseFirstModel) {
        if (this@CartActivity.baseContext != null) {
            runOnUiThread {
                if (this@CartActivity.baseContext != null) {
                    hideProgressDialog()
                    coupon = response.coupon.number

                    updateUICouponApplied(response)
                    cartAdapter!!.updateCouponResponse(response)
                }
            }
        }
    }

    /**
     * When successful response for api get coupon list
     *
     * @param response is successful response from Api
     */
    override fun onSuccessCouponResponseList(response: CouponResponseModel.CouponResponseSecondModel) {
        if (this@CartActivity.baseContext != null) {
            runOnUiThread {
                if (this@CartActivity.baseContext != null) {
                    hideProgressDialog()
                    afterCouponListSuccess(couponResponseModel = response)
                }
            }
        }
    }

    /**
     * Show alert if coupon code is invalid
     */
    private fun showAlert(message: String) {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@CartActivity,
            requestCode = Constants.App.RequestCode.INVALID_COUPON,
            message = message,
            positiveButtonText = getString(R.string.ok),
            listener = this@CartActivity
        )

        positiveAlertDialog.show()
    }

    /**
     * When error occurred in getting successful response of
     * get cart details,
     * remove product from cart,
     * applying coupon on product.
     *
     * @param errorResponse for Error message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@CartActivity.baseContext != null) {
            runOnUiThread {
                if (this@CartActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    /**
     * callback from holder to remove card item from card
     */
    override fun onItemSelected(position: Int, productId: Int) {
        var discount = 0.00

        val index = this.cartResponse.product.firstOrNull { it.productId == productId }
            ?.let { this.cartResponse.product.indexOf(it) }
            ?: -1
        if (index > -1) {

            this.cartResponse.totalAmount = (this.cartResponse.totalAmount.toDouble() -
                    this.cartResponse.product[index].price.toDouble()).toString()

            if (this.cartResponse.product[index].couponApplied) {
                discount = this.cartResponse.product[index].discount.toDouble()
            } else this.couponDiscount = "0.0"

            this.couponDiscount = (this.couponDiscount.toDouble() - discount).toString()
            updateDiscountValue(this.couponDiscount)

            updateTotal(
                (this.cartResponse.totalAmount.toDouble()
                        - this.couponDiscount.toDouble()).toString()
            )

            updateSubTotal(this.cartResponse.totalAmount)
        }
        apiHitToRemoveProductFromCart(productId)
    }

    override fun onListEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            rvCart.visibility = View.GONE
            showAlert()
        }
    }

    override fun onProductCategorySelected(position: Int, productId: Int, categoryName: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.CART -> {
                        apiToGetCartDetails()
                    }

                    Constants.App.RequestCode.CART_SCREEN -> {
                        val intent = Intent(this@CartActivity, MyProgramsActivity::class.java)
                        this.startActivityForResult(intent, Constants.App.RequestCode.MY_PROGRAM_SCREEN)
                        finish()
                    }
                }
            }

            Activity.RESULT_CANCELED -> {
                finish()
            }
        }
    }

    override fun onBackPressed() = finish()

    /**
     * Show alert if cart list is empty
     */
    private fun showAlert() {
        val positiveAlertDialog = PositiveAlertDialog(
            baseActivity = this@CartActivity,
            requestCode = Constants.App.RequestCode.POSITIVE_ALERT,
            message = getString(R.string.empty_cart_msg),
            positiveButtonText = getString(R.string.ok),
            listener = this@CartActivity
        )

        positiveAlertDialog.show()
    }

    override fun onDialogEventListener(
        dialogEventType: DialogEventType,
        requestCode: Int,
        model: Any?
    ) {
        when (dialogEventType) {
            DialogEventType.POSITIVE -> onDialogPositiveEvent(
                requestCode = requestCode,
                model = model
            )

            else ->
                super.onDialogEventListener(
                    dialogEventType = dialogEventType,
                    requestCode = requestCode,
                    model = model
                )
        }
    }

    override fun onDialogPositiveEvent(requestCode: Int, model: Any?) {
        Log.d(TAG, "onDialogPositiveEvent: requestCode- $requestCode")

        when (requestCode) {
            Constants.App.RequestCode.POSITIVE_ALERT -> {
                if (intent.extras != null) {
                    setResult(Activity.RESULT_OK)
                }
                finish()
            }

            Constants.App.RequestCode.INVALID_COUPON -> clickedCrossToRemoveAppliedCoupon()

            else ->
                super.onDialogPositiveEvent(requestCode = requestCode, model = model)
        }
    }
}
