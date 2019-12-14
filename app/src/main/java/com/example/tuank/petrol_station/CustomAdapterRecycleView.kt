package com.example.tuank.petrol_station

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*

class CustomAdapterRecycleView constructor(var arrayRc: ArrayList<DataRcCard>): RecyclerView.Adapter<CustomAdapterRecycleView.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        var v = LayoutInflater.from(p0?.context).inflate(R.layout.card_security_item,p0, false)

        lateinit var anim200: Animation
        anim200 = AnimationUtils.loadAnimation(v.context, R.anim.move_to_up)
        anim200.duration = 200
        anim200.startOffset = 200
        v.startAnimation(anim200)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayRc.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val data: DataRcCard = arrayRc[p1]
        p0?.title.text = data.title
        p0?.img_security_card.setImageResource(data.img)
        p0?.bgItem.setBackgroundResource(data.bg)
    }

    class ViewHolder(row: View): RecyclerView.ViewHolder(row){
        var img_security_card: ImageView
        var title: TextView
        var bgItem: LinearLayout
        init {
            img_security_card = row.findViewById(R.id.img_security_card)
            title = row.findViewById(R.id.title)
            bgItem = row.findViewById(R.id.bgItem)
        }
    }
}

