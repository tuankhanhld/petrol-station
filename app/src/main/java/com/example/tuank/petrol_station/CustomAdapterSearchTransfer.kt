package com.example.tuank.petrol_station

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterSearchTransfer constructor(var context: Context, var arraySearchTrans: ArrayList<dataSearchTransfer>): BaseAdapter(){
    class ViewHolder(row: View){
        var nameSearchTransfer: TextView
        var rfidSearchTransfer: TextView

        init {
            nameSearchTransfer = row.findViewById(R.id.nameSearchTransfer)
            rfidSearchTransfer = row.findViewById(R.id.rfidSearchTransfer)
        }
    }

    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder : ViewHolder
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.transfer_user_cell, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }
        var dataSearchTransfer: dataSearchTransfer = getItem(position) as dataSearchTransfer
        viewHolder.nameSearchTransfer.text = dataSearchTransfer.nameReceiver
        viewHolder.rfidSearchTransfer.text = dataSearchTransfer.refIdReceiver

        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arraySearchTrans.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arraySearchTrans.size
    }

}