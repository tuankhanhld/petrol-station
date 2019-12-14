package com.example.tuank.petrol_station

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterHistory constructor(var context: Context, var arrayHistory: ArrayList<PayHis>): BaseAdapter(){
    class ViewHolder(row: View){
        var typePayHistory: TextView
        var amountHistory: TextView
        var moneyPayHistory: TextView
        var dateHistory: TextView

        init {
            typePayHistory = row.findViewById(R.id.typePayHistory)
            amountHistory = row.findViewById(R.id.amountHistory)
            moneyPayHistory = row.findViewById(R.id.moneyPayHistory)
            dateHistory = row.findViewById(R.id.dateHistory)
        }
    }

    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder : ViewHolder
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.history_cell, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }
        var payHis: PayHis = getItem(position) as PayHis
        viewHolder.typePayHistory.text = payHis.typeHis
        viewHolder.amountHistory.text = payHis.amountHis
        viewHolder.moneyPayHistory.text = payHis.moneyHis
        viewHolder.dateHistory.text = payHis.dateHis

        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arrayHistory.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayHistory.size
    }

}

class CustomAdapterRechargeHistory constructor(var context: Context, var arrayHistory: ArrayList<RechargeHis>): BaseAdapter(){
    class ViewHolder(row: View){
        var recharge_his_email: TextView
        var recharge_his_series: TextView
        var recharge_his_money: TextView
        var recharge_his_time: TextView

        init {
            recharge_his_email = row.findViewById(R.id.recharge_his_email)
            recharge_his_series = row.findViewById(R.id.recharge_his_series)
            recharge_his_money = row.findViewById(R.id.recharge_his_money)
            recharge_his_time = row.findViewById(R.id.recharge_his_time)
        }
    }

    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder : ViewHolder
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.recharge_his_cell, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }
        var rechargeHis: RechargeHis = getItem(position) as RechargeHis
        viewHolder.recharge_his_email.text = rechargeHis.emailHis
        viewHolder.recharge_his_series.text = rechargeHis.seriesNum
        viewHolder.recharge_his_money.text = rechargeHis.moneyHis
        viewHolder.recharge_his_time.text = rechargeHis.dateHis

        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arrayHistory.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayHistory.size
    }
}

class CustomAdapterTransferHistory constructor(var context: Context, var arrayHistory: ArrayList<TransferHis>): BaseAdapter(){
    class ViewHolder(row: View){
        var transfer_his_from: TextView
        var transfer_his_to: TextView
        var transfer_his_money: TextView
        var transfer_his_time: TextView

        init {
            transfer_his_from = row.findViewById(R.id.transfer_his_from)
            transfer_his_to = row.findViewById(R.id.transfer_his_to)
            transfer_his_money = row.findViewById(R.id.transfer_his_money)
            transfer_his_time = row.findViewById(R.id.transfer_his_time)
        }
    }

    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        var view: View?
        var viewHolder : ViewHolder
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.transfer_his_cell, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }
        var transferHis: TransferHis = getItem(position) as TransferHis
        viewHolder.transfer_his_from.text = transferHis.from
        viewHolder.transfer_his_to.text = transferHis.to
        viewHolder.transfer_his_money.text = transferHis.moneyHis
        viewHolder.transfer_his_time.text = transferHis.dateHis

        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arrayHistory.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayHistory.size
    }
}