package com.fivestarmind.android.mvp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.mvp.pdfAdapter.FileDownloader
import com.fivestarmind.android.mvp.pdfAdapter.PageAdaptor
import com.fivestarmind.android.mvp.pdfAdapter.PdfReader
import com.fivestarmind.android.retrofit.ApiClient
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_open_pdf_file.*
import okhttp3.OkHttpClient
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit


class OpenPdfFileActivity : BaseActivity() {
    companion object {
         //private const val FILE_NAME = "TestPdf.pdf"
         private var FILE_NAME: String? = null

      //  private const val URL = "https://www.hq.nasa.gov/alsj/a17/A17_FlightPlan.pdf"
       // private const val URL = "https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-download-10-mb.pdf"
        var pdfLink: String? = null
        var pdfTitle: String? = null

    }

    private var disposable = Disposables.disposed()
    private var pdfReader: PdfReader? = null

    private val fileDownloader by lazy {
        FileDownloader(
            OkHttpClient.Builder().build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_pdf_file)

        getDataFromIntent()
        setToolbarTitle()
        setListener()

        RxJavaPlugins.setErrorHandler {
            Log.e("Error", it.localizedMessage)
        }

        viewPager.adapter = PageAdaptor()
        // prevent slide view pager
        viewPager.isUserInputEnabled = false

        FILE_NAME = getFileNameFromUrl(URL(ApiClient.BASE_URL_MEDIA + pdfLink!!))
        Log.d(TAG, "FileName: " + FILE_NAME)

        val targetFile = File(cacheDir, FILE_NAME)
        //showProgressDialog("")
       progressBar.visibility = View.VISIBLE
        tvProgramTitle.visibility = View.VISIBLE

        // disposable = fileDownloader.download(URL, targetFile)
        disposable = fileDownloader.download(ApiClient.BASE_URL_MEDIA + pdfLink!!, targetFile)
            .throttleFirst(2, TimeUnit.SECONDS)
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                //  progressBar.setProgress(it)
                       Log.d(TAG,"PDF Downloaded: "+"${it}% Downloaded")


              //  Toast.makeText(this, "$it% Downloaded", Toast.LENGTH_SHORT).show()
            }, {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                tvProgramTitle.visibility = View.GONE
              //  hideProgressDialog()
            }, { 
            //    Toast.makeText(this, "Complete Downloaded", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                tvProgramTitle.visibility = View.GONE
               // hideProgressDialog()

                pdfReader = PdfReader(targetFile).apply {
                    (viewPager.adapter as PageAdaptor).setupPdfRenderer(this)

                    updatePageCount()
                    setOnClickListener(viewPager)
                }

            })

//        TabLayoutMediator(pdf_page_tab, viewPager) { tab, position ->
//              tab.text = (position + 1).toString()
//          }.attach()

    }

    /**
     * Here is getting data from intent
     */
    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.App.PDF_LINK) && null != intent.getStringExtra(Constants.App.PDF_LINK)) {
            pdfLink = intent.getStringExtra(Constants.App.PDF_LINK)!!
            pdfTitle = intent.getStringExtra(Constants.App.PDF_NAME)!!
            //videoId = intent.getIntExtra(Constants.App.VIDEO_ID, Constants.App.Number.ZERO)
            // isMastered = intent.getIntExtra(Constants.App.IS_MASTERED, Constants.App.Number.ZERO)
        }
    }


    /**
     * Here is handling click on views
     */
    private fun setListener() {
        ivBack.setOnClickListener(clickListener)



    }

    private val clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBack -> onBackPressed()

            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        disposable.dispose()

    }

    private fun setOnClickListener(viewPager: ViewPager2) {

        Log.d("TAG", " itemCount " + viewPager.adapter!!.itemCount)
        ivPdfBackword!!.setOnClickListener {
            jumpPreviousPage(viewPager)
        }

        ivPdfForeword!!.setOnClickListener {
            jumpNextPage(viewPager)
        }
    }

    private fun setToolbarTitle() {
        tvHeading.text = pdfTitle
    }

    fun jumpNextPage(viewPager: ViewPager2) {
        viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        updatePageCount()
    }

    fun jumpPreviousPage(viewPager: ViewPager2) {

        viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        updatePageCount()

    }

    private fun updatePageCount() {
        if(viewPager.currentItem==0){
            ivPdfBackword.visibility = View.INVISIBLE
        }else{
            ivPdfBackword.visibility = View.VISIBLE

        }
        if (viewPager.adapter!!.itemCount == viewPager.currentItem+1){
            ivPdfForeword.visibility = View.INVISIBLE

        }else{
            ivPdfForeword.visibility = View.VISIBLE

        }

        tvPdfPageCount!!.text = getString(
            R.string.format_page_count,
            viewPager.currentItem+1, viewPager.adapter!!.itemCount
        )
    }


    fun getFileNameFromUrl(url: URL): String? {
        val urlPath: String = url.path
        return urlPath.substring(urlPath.lastIndexOf('/') + 1)
    }


}