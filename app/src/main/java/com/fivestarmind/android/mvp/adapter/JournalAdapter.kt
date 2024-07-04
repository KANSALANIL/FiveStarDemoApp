package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.mvp.activity.JournalListingActivity
import com.fivestarmind.android.mvp.holder.JournalViewHolder
import com.fivestarmind.android.mvp.model.response.JournalListingResponseModel

class JournalAdapter(
    private val journalList: ArrayList<JournalListingResponseModel>,
    private var activity: JournalListingActivity
) : RecyclerView.Adapter<JournalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): JournalViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_journal, parent, false)
        return JournalViewHolder(itemView, activity = activity)
    }

    internal fun clearJournalList() {
        this.journalList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.journalList.size

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) =
        holder.bindView(journalModel = this.journalList[position],position = position)
}