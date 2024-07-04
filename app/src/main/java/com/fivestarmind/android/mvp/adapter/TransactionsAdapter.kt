package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.mvp.holder.TransactionsViewHolder
import com.fivestarmind.android.mvp.model.response.TransactionsResponseDataModel

class TransactionsAdapter(
    private val transactionsList: ArrayList<TransactionsResponseDataModel>
) : RecyclerView.Adapter<TransactionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TransactionsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionsViewHolder(itemView)
    }

    internal fun clearTransactionsList() {
        this.transactionsList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.transactionsList.size

    override fun onBindViewHolder(holder: TransactionsViewHolder, position: Int) =
        holder.bindView(model = this.transactionsList[position], position = position)
}