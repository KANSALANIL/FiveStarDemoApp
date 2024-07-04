package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.activity.CartActivity
import com.fivestarmind.android.mvp.holder.CartHolder
import com.fivestarmind.android.mvp.model.response.CartProductResponseModel
import com.fivestarmind.android.mvp.model.response.CouponResponseModel

class CartAdapter(
    private var cartActivity: CartActivity,
    private var programInterface: ProgramInterface
) : RecyclerView.Adapter<CartHolder>() {

    private var productList = ArrayList<CartProductResponseModel>()
    private var couponDiscount = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CartHolder {

        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    internal fun updateCartList(productList: ArrayList<CartProductResponseModel>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    private fun visibleView() {
        if (productList.size == 0) {
            programInterface.onListEmpty(true)
        }
    }

    internal fun removeProduct(productId: Int) {
        val index =
            productList.firstOrNull { it.productId == productId }?.let { productList.indexOf(it) }
                ?: -1
        if (index > -1) {
            productList.removeAt(index)
            notifyItemRemoved(index)
            visibleView()
        }
    }

    /**
     * Updating UI and calculation after applying coupon on product
     *
     * @param couponResponseModel is success response of Api apply coupon on product
     */
    internal fun updateCouponResponse(couponResponseModel: CouponResponseModel.CouponResponseFirstModel) {
        couponDiscount = 0.0
        when (couponResponseModel.coupon.couponType) {
            Constants.App.CouponType.ALL -> {
                productList.asSequence().forEach {
                    it.couponApplied = true
                    it.priceAfterCouponApplied = it.price.toDouble() -
                            (it.price.toDouble() * couponResponseModel.coupon.discount.toDouble() / 100)
                    it.couponDiscount = couponResponseModel.coupon.discount
                    it.discount += it.price.toDouble() - it.priceAfterCouponApplied
                    this.couponDiscount = this.couponDiscount + it.discount.toDouble()
                }
            }
            Constants.App.CouponType.MULTIPLE -> {
                updateCouponStatus(couponResponseModel)
            }
            Constants.App.CouponType.SINGLE -> {
                updateCouponStatus(couponResponseModel)
            }
        }
        cartActivity.updateProductList(this.productList)
        cartActivity.updateCouponDiscount(couponDiscount)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        holder.bindView(productList[position], programInterface)
    }

    private fun updateCouponStatus(couponResponseModel: CouponResponseModel.CouponResponseFirstModel) {
        productList.asSequence().forEach { cartResponseModel ->
            val index = cartResponseModel.couponProduct.firstOrNull { couponProduct ->
                couponProduct.couponId == couponResponseModel.coupon.id
            }?.let { cartResponseModel.couponProduct.indexOf(it) }
                ?: -1
            if (index > -1) {
                cartResponseModel.couponApplied = true
                cartResponseModel.priceAfterCouponApplied = cartResponseModel.price.toDouble() -
                        (cartResponseModel.price.toDouble() * couponResponseModel.coupon.discount.toDouble() / 100)
                cartResponseModel.couponDiscount = couponResponseModel.coupon.discount

                cartResponseModel.discount += cartResponseModel.price.toDouble() - cartResponseModel.priceAfterCouponApplied
                this.couponDiscount = this.couponDiscount + cartResponseModel.discount.toDouble()
            }
        }
    }
}