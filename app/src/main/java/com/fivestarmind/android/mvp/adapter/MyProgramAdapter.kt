package com.fivestarmind.android.mvp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fivestarmind.android.R
import com.fivestarmind.android.mvp.activity.MyProgramsActivity
import com.fivestarmind.android.mvp.holder.MyProgramsHolder
import com.fivestarmind.android.mvp.model.response.MyProgramsResponseDataModel

class MyProgramAdapter(private var activity: MyProgramsActivity) : RecyclerView.Adapter<MyProgramsHolder>() {

    private var programsList = ArrayList<MyProgramsResponseDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyProgramsHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_my_program, parent, false)
        return MyProgramsHolder(view, activity = activity)
    }

    override fun getItemCount(): Int {
        return programsList.size
    }

    internal fun updateResponseList(responseDataList: ArrayList<MyProgramsResponseDataModel>) {
        this.programsList = responseDataList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyProgramsHolder, position: Int) {
        holder.bindView(programsList[position])
    }
}