package com.fivestarmind.android.mvp.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.mvp.model.response.TransactionsResponseDataModel
import kotlinx.android.synthetic.main.item_transaction.view.*

class TransactionsViewHolder(private var itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    private var model: TransactionsResponseDataModel? = null

    fun bindView(model: TransactionsResponseDataModel, position: Int) {
        this.model = model

        model.apply {
            showHideViewForTransactionId()
            updateTransactionId()
            updateDateTime()
            updatePrice()
            updateStatus()
            updatePaymentType()
        }
    }

    private fun showHideViewForTransactionId() {
        itemView.apply {
            if (model?.payId.isNullOrEmpty())
                tvTransactionId.visibility = View.GONE
            else tvTransactionId.visibility = View.VISIBLE
        }
    }

    /**
     * Here is updating the transaction id in view
     */
    private fun updateTransactionId() {
        itemView.apply {
            model?.payId?.apply {
                tvTransactionId.text = this
            }
        }
    }

    /**
     * Here is updating the date and time in view
     */
    private fun updateDateTime() {
        itemView.apply {
            model?.date?.apply {
                tvDateTime.text = this
            }
        }
    }

    /**
     * Here is updating the price in view
     */
    private fun updatePrice() {
        itemView.apply {
            model?.amount?.apply {
                tvPrice!!.text = context.getString(
                    R.string.format_price,
                    "${model?.amount} "
                )
            }
        }
    }

    /**
     * Here is updating the price in view
     */
    private fun updatePaymentType() {
        itemView.apply {
            model?.paymentType?.apply {
                tvPaymentType.text = this
            }
        }
    }

    /**
     * Here is updating the status in view
     */
    private fun updateStatus() {
        itemView.apply {
            model?.status?.apply {
                tvStatus.text = this
            }
        }
    }
}