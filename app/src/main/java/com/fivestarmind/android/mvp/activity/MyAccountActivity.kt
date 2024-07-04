package com.fivestarmind.android.mvp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.fivestarmind.android.R
import com.fivestarmind.android.helper.AppHelper
import com.fivestarmind.android.interfaces.Constants
import kotlinx.android.synthetic.main.activity_my_account.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class MyAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        setToolbarTitle()
        checkIsLoggedIn()
        setClickListener()
    }

    /**
     * Here is updating the title of the screen
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.my_account)
    }

    /**
     * Check for user logged in or not
     */
    private fun checkIsLoggedIn() {
        when (AppHelper.isUserLoggedIn()) {
            true -> scrollView.visibility = View.VISIBLE
            false -> callToOpenLoginActivity()
        }
    }

    /**
     * Click event on views
     */
    private fun setClickListener() {
        ivBackMenu.setOnClickListener(clickListener)
        clProfile.setOnClickListener(clickListener)
        clMyPrograms.setOnClickListener(clickListener)
        clMyMemberships.setOnClickListener(clickListener)
        clMyFavorite.setOnClickListener(clickListener)
        clTransactions.setOnClickListener(clickListener)
        clCart.setOnClickListener(clickListener)
        clAddJournal.setOnClickListener(clickListener)
        clChangePassword.setOnClickListener(clickListener)
//        clSettings.setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> clickedBack()

                R.id.clProfile -> clickedProfile()

                R.id.clMyPrograms -> clickedMyPrograms()

                R.id.clMyMemberships -> clickedMyMemberships()

                R.id.clMyFavorite -> clickedMyFavorite()

                R.id.clTransactions -> clickedTransactions()

                R.id.clCart -> clickedCart()

                R.id.clAddJournal -> clickedAddJournal()

                R.id.clChangePassword -> clickedChangePassword()

//                R.id.clSettings -> clickedSettings()
            }
    }

    /**
     * It is called when user clicked on back icon
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * It is called when user click on profile view
     */
    private fun clickedProfile() {
        startActivity(Intent(this@MyAccountActivity, ProfileActivity::class.java))
    }

    /**
     * It is called when user clicked on my programs view
     */
    private fun clickedMyPrograms() {
        startActivity(Intent(this@MyAccountActivity, MyProgramsActivity::class.java))
    }


    /**
     * It is called when user clicked on my memberships view
     */
    private fun clickedMyMemberships() {
        startActivity(Intent(this@MyAccountActivity, MyMembershipActivity::class.java))
    }

    /**
     * It is called when user clicked on my favorite view
     */
    private fun clickedMyFavorite() {
        startActivity(Intent(this@MyAccountActivity, MyFavoriteActivity::class.java))
    }

    /**
     * It is called when user clicked on transactions view
     */
    private fun clickedTransactions() {
        startActivity(Intent(this@MyAccountActivity, TransactionsActivity::class.java))
    }

    /**
     * It is called when user clicked on cart view
     */
    private fun clickedCart() {
        startActivity(Intent(this@MyAccountActivity, CartActivity::class.java))
    }

    /**
     * It is called when user clicked on add journal view
     */
    private fun clickedAddJournal() {
        startActivity(Intent(this@MyAccountActivity, AddJournalActivity::class.java))
    }

    /**
     * It is called when user clicked on change password view
     */
    private fun clickedChangePassword() {
        startActivity(Intent(this@MyAccountActivity, ChangePasswordActivity::class.java))
    }

    /**
     * It is called when user clicked on settings view
     */
    private fun clickedSettings() {
        startActivity(Intent(this@MyAccountActivity, SettingsActivity::class.java))
    }

    private fun callToOpenLoginActivity() {
        startActivityForResult(
            Intent(this, SignInActivity::class.java)
                .putExtra(Constants.App.BundleKey.COMING_FROM, TAG),
            Constants.App.RequestCode.MY_ACCOUNT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.App.RequestCode.MY_ACCOUNT -> {
                        scrollView.visibility = View.VISIBLE
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
