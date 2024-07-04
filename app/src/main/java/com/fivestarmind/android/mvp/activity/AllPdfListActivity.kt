package com.fivestarmind.android.mvp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fivestarmind.android.R
import com.fivestarmind.android.enum.ItemClickType
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.adapter.AllPdfListAdapter
import kotlinx.android.synthetic.main.activity_all_pdf_list.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class AllPdfListActivity : BaseActivity(), RecyclerViewItemListener {
    private var linearLayoutManager: LinearLayoutManager? = null
    private var allPdfListAdapter: AllPdfListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_pdf_list)

        setToolbarTitle()
        clickListener()
        initRecyclerView()
    }

    /**
     * Here is updating the title of the toolbar
     */
    private fun setToolbarTitle() {
        tvTitle.text = getString(R.string.pdf_titel)
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun initRecyclerView() {
        allPdfListAdapter = AllPdfListAdapter(this, this)
        linearLayoutManager = LinearLayoutManager(this)
        rvAllPdf.layoutManager = linearLayoutManager

        rvAllPdf.apply {
            layoutManager = linearLayoutManager
            adapter = allPdfListAdapter
        }
    }

    /**
     * Click event for views
     */
    private fun clickListener() {
        ivBackMenu.setOnClickListener(clickListener)
    }

    private var clickListener = View.OnClickListener { view ->
        if (shouldProceedClick())
            when (view.id) {
                R.id.ivBackMenu -> onBackPressed()

            }
    }

    override fun onRecyclerViewItemClick(
        itemClickType: ItemClickType,
        model: Any?,
        position: Int,
        view: View
    ) {
        when (itemClickType) {
            //ItemClickType.LIKE_UNLIKE ->

            ItemClickType.OPEN_PDF -> startActivity(
                Intent(
                    this@AllPdfListActivity,
                    OpenPdfFileActivity::class.java
                )
            )


            else -> {}
        }


    }



}