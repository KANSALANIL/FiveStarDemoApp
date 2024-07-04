package com.fivestarmind.android.mvp.holder

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.Constants
import com.fivestarmind.android.interfaces.ProgramInterface
import com.fivestarmind.android.mvp.model.response.CartProductResponseModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cart_product.view.*
import java.math.BigDecimal

class CartHolder(private var view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(
        cartProductResponseModel: CartProductResponseModel,
        programInterface: ProgramInterface
    ) {

        itemView.apply {
            updateProductImage(thumbnail = cartProductResponseModel.thumbnail)
            updateProductName(productName = cartProductResponseModel.productName)

            // Handle visibilities for view if coupon applied or not & update response in views
            if (cartProductResponseModel.couponApplied)
                updateUIForAppliedCoupon(cartProductResponseModel = cartProductResponseModel)
            else
                updateUIForApplyCoupon(cartProductResponseModel = cartProductResponseModel)

            ivCross.setOnClickListener {
                clickedCross(
                    programInterface = programInterface,
                    cartProductResponseModel = cartProductResponseModel
                )
            }
        }
    }

    /**
     * Here is updating the product image
     *
     * @param thumbnail
     */
    private fun updateProductImage(thumbnail: String) {
        itemView.apply {
            if (thumbnail.isNotEmpty())
                Picasso.get()
                    .load(thumbnail)
                    .into(ivCartProduct, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            ivCartProduct.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
                        }
                    }) else ivCartProduct.setImageDrawable(context.resources.getDrawable(R.drawable.ic_placeholder_corner_8))
        }
    }

    /**
     * Here is updating the product Name
     *
     * @param productName
     */
    private fun updateProductName(productName: String?) {
        itemView.apply {
            productName?.apply {
                tvProductName.text = productName
            }
        }
    }

    /**
     * Here is updating the product price
     *
     * @param productPrice
     */
    private fun updateProductPrice(productPrice: BigDecimal) {
        itemView.apply {
            tvPrice.text = formatPrice(context, productPrice)
        }
    }

    /**
     * Here is updating the discounted product price
     *
     * @param discountedPrice
     */
    private fun updateDiscountPrice(discountedPrice: BigDecimal) {
        itemView.apply {
            tvDiscountedPrice.text = formatPrice(context, discountedPrice)

            tvDiscountedPrice.paintFlags =
                tvDiscountedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    /**
     * Here is updating the product price
     *
     * @param couponValue
     */
    private fun updateCouponValue(couponValue: String) {
        itemView.apply {
            tvCouponValue.text = context.getString(
                R.string.format_price_discount,
                "$couponValue% "
            )
        }
    }

    /**
     * Here is updating views when coupon is applied
     *
     * @param cartProductResponseModel
     */
    private fun updateUIForAppliedCoupon(cartProductResponseModel: CartProductResponseModel) {
        itemView.apply {
            ivCheck.visibility = View.VISIBLE
            tvCouponValue.visibility = View.VISIBLE
            tvDiscountedPrice.visibility = View.VISIBLE

            updateProductPrice(productPrice = cartProductResponseModel.priceAfterCouponApplied.toBigDecimal())
            updateDiscountPrice(discountedPrice = cartProductResponseModel.price.toBigDecimal())
            updateCouponValue(couponValue = cartProductResponseModel.couponDiscount)
        }
    }

    /**
     * Here is updating view for apply coupon
     */
    private fun updateUIForApplyCoupon(cartProductResponseModel: CartProductResponseModel) {
        itemView.apply {
            ivCheck.visibility = View.GONE
            tvCouponValue.visibility = View.GONE
            tvDiscountedPrice.visibility = View.GONE

            updateProductPrice(productPrice = cartProductResponseModel.price.toBigDecimal())
        }
    }

    /**
     * It is called when user click on cross icon
     */
    private fun clickedCross(
        programInterface: ProgramInterface,
        cartProductResponseModel: CartProductResponseModel
    ) {
        if (Constants.App.ProceedClick.shouldProceedClick())
            programInterface.onItemSelected(position, cartProductResponseModel.productId)
    }

    private fun formatPrice(context: Context, price: BigDecimal): String =
        String.format(
            context.getString(R.string.format_price),
            price.setScale(Constants.App.Number.TWO, BigDecimal.ROUND_HALF_EVEN).toString()
        )
}