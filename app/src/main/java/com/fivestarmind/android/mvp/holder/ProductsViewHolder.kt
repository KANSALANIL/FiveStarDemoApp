package com.fivestarmind.android.mvp.holder

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.activity.BaseActivity
import com.fivestarmind.android.mvp.activity.ProgramsActivity
import com.fivestarmind.android.mvp.adapter.ProductPhaseAdapter
import com.fivestarmind.android.mvp.model.response.AllProductsDataModel
import com.fivestarmind.android.mvp.model.response.PhaseVideoModel
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsViewHolder(
    itemView: View,
    var activity: BaseActivity,
    private var recyclerViewItemListener: RecyclerViewItemListener,
    private var isFromHome: Boolean
) :
    RecyclerView.ViewHolder(itemView) {

    private var phaseVideoList: ArrayList<PhaseVideoModel>? = null
    private var productPhaseAdapter: ProductPhaseAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    private var productModel: AllProductsDataModel? = null

    /**
     * Click event on views handles here
     */
    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (Constants.App.ProceedClick.shouldProceedClick())
            when (view.id) {
                R.id.tvViewAll -> clickedViewAll()
            }
    }

    init {
        setUpRecyclerViewsWithAdaptersAndListeners()

        itemView.apply {
            tvViewAll.setOnClickListener(clickListener)
        }
    }

    /**
     * Here recyclerViews are setup with it's adapter and it's listeners
     */
    private fun setUpRecyclerViewsWithAdaptersAndListeners() {
        phaseVideoList = arrayListOf()

        itemView.apply {
            productPhaseAdapter = ProductPhaseAdapter(
                productsList = phaseVideoList!!,
                activity = activity,
                recyclerViewItemListener = recyclerViewItemListener
            )

            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            rvPhase.apply {
                layoutManager = this@ProductsViewHolder.layoutManager
                itemAnimator = DefaultItemAnimator()
                adapter = productPhaseAdapter
            }
        }
    }

    fun bindView(model: AllProductsDataModel) {
        this.productModel = model

        showHideViewAllView()
        itemView.apply {
            productModel?.apply {
                updateProductName(productName = productName)

                updateDataInList(listData = phases)
            }
        }
    }

    private fun showHideViewAllView() {
        itemView.apply {
            if (isFromHome) tvViewAll.visibility = View.VISIBLE
            else tvViewAll.visibility = View.INVISIBLE
        }
    }

    private fun updateDataInList(listData: ArrayList<PhaseVideoModel>?) {
        addDataToList(listData = listData)
    }

    private fun addDataToList(listData: ArrayList<PhaseVideoModel>?) {
        phaseVideoList!!.clear()

        if (listData!!.isNotEmpty()) {
            val index =
                listData.firstOrNull { it.listType == itemView.context.getString(R.string.without_phase) }
                    ?.let { listData.indexOf(it) }
                    ?: -1
            if (index > -1) {
                phaseVideoList!!.addAll(listData[index].videoListing)
                phaseVideoList!!.addAll(listData)

                val index =
                    phaseVideoList!!.firstOrNull { it.title == itemView.context.getString(R.string.phase_zero) }
                        ?.let { phaseVideoList!!.indexOf(it) }
                        ?: -1
                if (index > -1) {
                    phaseVideoList!!.remove(phaseVideoList!![index])
                }

            } else
                phaseVideoList!!.addAll(listData)

            productPhaseAdapter?.updateProductImage(image = productModel?.image)
            productPhaseAdapter?.notifyDataSetChanged()
        }
    }

    /**
     * Here is updating the product name
     */
    private fun updateProductName(productName: String?) {
        itemView.apply {
            productName?.apply {
                tvProductName.text = this
            }
        }
    }

    /**
     * It is called when user clicked on view all view
     */
    private fun clickedViewAll() {
        val intent = Intent(activity, ProgramsActivity::class.java)
        activity.startActivity(intent)
    }
}