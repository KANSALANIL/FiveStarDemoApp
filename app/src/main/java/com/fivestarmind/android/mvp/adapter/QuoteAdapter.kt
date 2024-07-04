package com.fivestarmind.android.mvp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.interfaces.RecyclerViewItemListener
import com.fivestarmind.android.mvp.holder.QuoteHolder
import com.fivestarmind.android.mvp.model.response.TodayQuoteItem

class QuoteAdapter(
    private var activity: Context,
    private var todayQuoteList: ArrayList<TodayQuoteItem>,
    private val listener: RecyclerViewItemListener
) : RecyclerView.Adapter<QuoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): QuoteHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_today_quote, parent, false)
        return QuoteHolder(view, listener = listener)
    }

    override fun getItemCount(): Int {
        return todayQuoteList.size
    }


    override fun onBindViewHolder(holder: QuoteHolder, position: Int) {
        //holder.bindView(programsList[position], activity)
        holder.bindView(todayQuoteList.get(position),activity)
    }
}