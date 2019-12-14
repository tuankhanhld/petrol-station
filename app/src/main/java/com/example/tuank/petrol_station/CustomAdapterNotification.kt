package com.example.tuank.petrol_station

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterNotification constructor(var context: Context, var arrayNotifiMess: ArrayList<DataNotificationItem>): BaseAdapter(){
    class ViewHolder(row: View){
        var date: TextView
        var time: TextView
        var from: TextView
        var subject: TextView
        var content: TextView
        init {
            date = row.findViewById(R.id.date)
            time = row.findViewById(R.id.time)
            from = row.findViewById(R.id.from)
            subject = row.findViewById(R.id.subject)
            content = row.findViewById(R.id.content)
        }
    }

    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder : ViewHolder
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.notifi_item, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }
        var dataNotificationItem: DataNotificationItem = getItem(position) as DataNotificationItem
        viewHolder.date.text = dataNotificationItem.date
        viewHolder.time.text = dataNotificationItem.time
        viewHolder.from.text = dataNotificationItem.from
        viewHolder.subject.text = dataNotificationItem.sub
        viewHolder.content.text = dataNotificationItem.content

        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arrayNotifiMess.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayNotifiMess.size
    }
}