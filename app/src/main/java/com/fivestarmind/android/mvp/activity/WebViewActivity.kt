package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.WebViewType
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_filter.filterMiniPlayer
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.item_journal.*
import kotlinx.android.synthetic.main.layout_home_mini_player.ivExoIcon
import kotlinx.android.synthetic.main.layout_home_mini_player.progressBarExo
import kotlinx.android.synthetic.main.layout_home_mini_player.tvAudioTitleName
import kotlinx.android.synthetic.main.layout_toolbar.*

class WebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        webView.getSettings().setBuiltInZoomControls(true)
        webView.getSettings().setDisplayZoomControls(false)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        progressBar
        setUpToolbar()
        setClickListener()
        setWebView()
        initializeMiniAudioPlayer(webViewMiniPlayer,tvAudioTitleName,ivExoIcon,progressBarExo)



    }

    /**
     * Here actionBar is being setup
     */
    private fun setUpToolbar() {
        val title: String =
            when (getCurrentWebviewType()) {
                WebViewType.PRIVACY_POLICY -> getString(R.string.privacy_policy)

                WebViewType.TERMS_AND_CONDITIONS -> getString(R.string.terms_and_conditions)

                WebViewType.FAQ -> getString(R.string.faq)
            }

        tvTitle.text = title
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
                R.id.ivBackMenu -> clickedBack()
            }
    }

    /**
     * It is called when user click on back view
     */
    private fun clickedBack() {
        onBackPressed()
    }

    /**
     * Here updating the web view with data. The url of terms/privacy/FAQ is loaded.
     */
    private fun setWebView() {
        webView?.apply {
            when (getCurrentWebviewType()) {
                WebViewType.PRIVACY_POLICY -> {
                   // loadUrl(ApiClient.BASE_URL_LINKS_NEW + Constants.Api.PRIVACY_POLICY)
                   // loadUrl(ApiClient.BASE_URL_LINKS_NEW)

                    /*progressBar.visibility = View.VISIBLE
                    webView.webViewClient = MyWebViewClient(progressBar)
                    webView.loadUrl("https://asklerch.com/privacy/")*/

                    loadWebUrl(WebViewType.PRIVACY_POLICY)
                    Log.d(
                        TAG,
                        "PRIVACY_POLICY --> ${ApiClient.BASE_URL_LINKS + Constants.Api.PRIVACY_POLICY}"
                    )
                }

                WebViewType.TERMS_AND_CONDITIONS -> {
                   // loadUrl(ApiClient.BASE_URL_LINKS_NEW + Constants.Api.TERMS)
                    loadWebUrl(WebViewType.TERMS_AND_CONDITIONS)

                    Log.d(
                        TAG,
                        "TERMS --> ${ApiClient.BASE_URL_LINKS + Constants.Api.TERMS}"
                    )
                }

                WebViewType.FAQ -> {
                    loadUrl(ApiClient.BASE_URL_LINKS + Constants.Api.FAQ)
                    Log.d(
                        TAG,
                        "FAQ --> ${ApiClient.BASE_URL_LINKS + Constants.Api.FAQ}"
                    )
                }
            }
        }
    }

    /**
     * It returns the current webviewType from the current activity's intent data
     *
     * @return current webviewType
     */
    private fun getCurrentWebviewType(): WebViewType =
        intent.getSerializableExtra(Constants.App.WEBVIEW_TYPE) as WebViewType

    override fun onBackPressed() = finish()


    private class MyWebViewClient(var progressBar: ProgressBar) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE)
            }
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {

            /*Privacy policy link  : http://103.149.154.53/5starmind/privacy-policy
             Terms&Condition link : http://103.149.154.53/5starmind/terms-condition*/

            println("on finish")
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE)
            }
        }
    }

    fun loadWebUrl(webViewType:WebViewType){

        progressBar.visibility = View.VISIBLE

        if (webViewType.toString().equals("PRIVACY_POLICY")) {
            webView.webViewClient = MyWebViewClient(progressBar)
            webView.loadUrl(ApiClient.BASE_URL_LINKS + Constants.Api.PRIVACY_POLICY)
        }else{

            webView.webViewClient = MyWebViewClient(progressBar)
            webView.loadUrl(ApiClient.BASE_URL_LINKS + Constants.Api.TERMS)
        }
    }
}