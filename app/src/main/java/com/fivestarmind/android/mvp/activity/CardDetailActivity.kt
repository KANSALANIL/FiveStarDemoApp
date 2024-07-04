package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.fivestarmind.android.*
import com.fivestarmind.android.helper.SharedPreferencesHelper
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.model.request.UserPaymentRequestModel
import com.fivestarmind.android.mvp.model.response.ErrorResponse
import com.fivestarmind.android.mvp.model.response.UserPaymentResponseModel
import com.fivestarmind.android.mvp.presenter.CardDetailsPresenter
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_cart_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*


class CardDetailActivity : BaseActivity(), CardDetailsPresenter.ResponseCallBack {

    override var TAG = CardDetailActivity::class.java.simpleName
    private lateinit var presenter: CardDetailsPresenter
    private var userPaymentRequestModel = UserPaymentRequestModel()
    private lateinit var stripe: Stripe
    private var cal = Calendar.getInstance()
    private var date: Date? = null
    private var discountCoupon = ""
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_detail)

        initUI()
        initPresenter()

        getIntentData()
        initEditTextFilters()

        clickListener()
        clickForDatePicker()
    }

    private fun initUI() {
        stripe = Stripe(this, Constants.App.PublishKey.PUBLISH_KEY)
        tvTitle.text = getString(R.string.cart_detail)
        ivBackMenu.setImageResource(R.drawable.ic_close_white)
    }

    /**
     * Initialization of presenter
     */
    private fun initPresenter() {
        presenter = CardDetailsPresenter(this)
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.App.COUPON)) {
            true -> {
                discountCoupon = intent.getStringExtra(Constants.App.COUPON)!!
            }

            else -> {}
        }
    }

    /**
     * Setting filters to editTexts
     */
    private fun initEditTextFilters() {
        arrayOf(emojiFilter).apply {
            etCardHolder.filters = etCardHolder.filters + this
        }
    }

    /**
     * Click event for views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        btnSubmit.setOnClickListener(clickListener)
        etCardNumber.addTextChangedListener(styleCardNumberTextWatcher)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

                R.id.btnSubmit -> clickedSubmit()
            }
    }

    /**
     * It is called when user clicked on submit button
     */
    private fun clickedSubmit() {
        if (!validateDetails()) return

        if (isInternetConnected(shouldShowToast = true)) {
            showProgressDialog("")
            getCardDetails()
//            showToast(message = getString(R.string.still_to_implement))
        }
    }

    private var styleCardNumberTextWatcher: TextWatcher = object : TextWatcher {
        var isDelete = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            isDelete = before != 0
        }

        override fun afterTextChanged(s: Editable) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(s)
            s.apply {
                if (isNotEmpty() && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1)
                    else
                        stringBuilder.insert(length - 1, "-")
                    etCardNumber.setText(stringBuilder)
                    etCardNumber.setSelection(etCardNumber.text.length)
                }
            }
        }
    }

    /**
     * Selection of date calender view
     */
    private fun clickForDatePicker() {
        etExpiryDate.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    datePickerDialog = DatePickerDialog(
                        this@CardDetailActivity,
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    )

                    datePickerDialog.show()
                    datePickerDialog.datePicker.minDate = Date().time
                }
            }
            false
        }
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            Log.d(TAG, "year = $year and month = $monthOfYear")
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

    /**
     * Update selected date in view
     */
    private fun updateDateInView() {
        val myFormat = "MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etExpiryDate.text = sdf.format(cal.time)
        date = sdf.parse(etExpiryDate.text.toString())
    }

    /**
     * Here validating the field entries entered by the user
     *
     * @return status for validation success/failure
     */
    private fun validateDetails(): Boolean {
        var valid = true

        etCardHolder.error = when {
            etCardHolder.text!!.isEmpty() -> {
                valid = false
                getString(R.string.empty_cart_holder)
            }

            else -> null
        }

        etCardNumber.error = when {
            etCardNumber.text!!.isEmpty() -> {
                valid = false
                getString(R.string.empty_cart_number)
            }

            etCardNumber.text!!.trim().length < resources.getInteger(R.integer.card_number) -> {
                valid = false
                getString(R.string.please_enter_valid_card_number)
            }

            else -> null
        }

        etExpiryDate.error = when {
            etExpiryDate.text!!.isEmpty() -> {
                valid = false
                getString(R.string.empty_expiry_date)
            }

            else -> null
        }

        etCvNumber.error = when {
            etCvNumber.text!!.isEmpty() -> {
                valid = false
                getString(R.string.empty_cvv_no)
            }

            etCvNumber.text!!.trim().length < resources.getInteger(R.integer.cvv_number) -> {
                valid = false
                getString(R.string.validate_cvv)
            }

            else -> null
        }

        return valid
    }

    /**
     * Get field entries for user payment
     */
    private fun getCardDetails() {
        val card = etCardNumber.text.toString()
        val month = (cal.get(Calendar.MONTH) + 1).toString()
        val year = cal.get(Calendar.YEAR).toString()
        val cvv = etCvNumber.text.toString()

        createToken(card, month, year, cvv)
    }

    /**
     * Creation of token from user card details
     */
    private fun createToken(card: String, month: String, year: String, cvv: String) {
        val card = Card(card, Integer.valueOf(month), Integer.valueOf(year), cvv)
        card.currency = Constants.App.CURRENCY

        stripe.createToken(card, object : TokenCallback {
            override fun onSuccess(token: Token) {
                Log.d(TAG, "createToken token id = ${token.id}")
                hitApiForUserPayment(token.id)
            }

            override fun onError(error: Exception) {
                showToast(error.message!!)
                error.printStackTrace()
                hideProgressDialog()
            }
        })
    }

    /**
     *  Call for api User payment
     */
    private fun hitApiForUserPayment(paymentToken: String) {
        val currentDate =
            android.text.format.DateFormat.format("yyyy-MM-dd kk:mm:ss", Date()) as String
        Log.d(TAG, "Current Date -- $currentDate")

        userPaymentRequestModel.apply {
            coupon = discountCoupon
            purchase_date = currentDate
            token = paymentToken
        }

        if (isInternetConnected(shouldShowToast = true))
            presenter.apiPostForUserPayments(
                SharedPreferencesHelper.getAuthToken(),
                userPaymentRequestModel
            )
    }

    private fun afterPaymentSuccess() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * When successful response or data retrieved from user payment Api
     *
     * @param response is successful response from Api
     */
    override fun onSuccessResponse(response: UserPaymentResponseModel) {
        if (this@CardDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@CardDetailActivity.baseContext != null) {
                    hideProgressDialog()
                    showToast(response.message)

                    afterPaymentSuccess()
                }
            }
        }
    }

    /**
     * When error occurred in user payment api
     *
     * @param errorResponse for Error message
     */
    override fun onFailureResponse(errorResponse: ErrorResponse) {
        if (this@CardDetailActivity.baseContext != null) {
            runOnUiThread {
                if (this@CardDetailActivity.baseContext != null) {
                    hideProgressDialog()
                    responseFailure(errorResponse)
                }
            }
        }
    }

    override fun onBackPressed() = finish()
}
