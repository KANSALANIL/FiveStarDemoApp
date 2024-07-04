package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.fivestarmind.android.*
import com.fivestarmind.android.interfaces.Constants
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_cart_detail.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.text.SimpleDateFormat
import java.util.*


class MembershipCardDetailActivity : BaseActivity() {

    override var TAG = MembershipCardDetailActivity::class.java.simpleName
    private lateinit var stripe: Stripe
    private var cal = Calendar.getInstance()
    private var date: Date? = null
    private var discountCoupon = ""
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_detail)

        initUI()

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
                        this@MembershipCardDetailActivity,
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
                hideProgressDialog()
                afterTokenSuccess(token.id)
            }

            override fun onError(error: Exception) {
                showToast(error.message!!)
                error.printStackTrace()
                hideProgressDialog()
            }
        })
    }

    private fun afterTokenSuccess(token: String) {
        val intent: Intent = Intent()
            .apply {
                putExtra(Constants.App.PAYMENT_TOKEN, token)
            }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        val intent: Intent = Intent()
            .apply {
            }

        setResult(Activity.RESULT_FIRST_USER, intent)
        finish()
    }
}
