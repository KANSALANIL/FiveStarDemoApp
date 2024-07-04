package com.fivestarmind.android.mvp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.fivestarmind.android.R
import com.fivestarmind.android.mvp.model.response.FilterItemModel
import kotlinx.android.synthetic.main.custome_spinner.view.spinnerName

class FilterAdapter(context: Context, var filterList : ArrayList<FilterItemModel>) : BaseAdapter() {

    var inflater : LayoutInflater = LayoutInflater.from(context)



    override fun getCount(): Int {
      return  filterList.size
    }

    override fun getItem(position: Int): Any {
        return filterList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
     //   val view = inflater.inflate(position,R.layout.custome_spinner)
        val view = inflater.inflate(R.layout.custome_spinner,parent,false)

        view.spinnerName.text = filterList.get(position).name
        return view
    }


}

