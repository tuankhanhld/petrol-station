package com.example.tuank.Users

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.tuank.petrol_station.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception


class CustomRecycleUser constructor(var arrayRc: ArrayList<UsersData>): RecyclerView.Adapter<CustomRecycleUser.ViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        var v = LayoutInflater.from(p0?.context).inflate(R.layout.recent_users_rc_item,p0, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayRc.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val data: UsersData = arrayRc[p1]

        p0?.userName.text = data.userName
        Picasso.get()
                .load(data.userAvatarUrl)
                .into(object: Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        p0?.userAvatar.setImageBitmap(bitmap)
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }


                })
    }
    class ViewHolder(row: View): RecyclerView.ViewHolder(row){
        var userAvatar: CircleImageView
        var userName: TextView
        init {
            userAvatar = row.findViewById(R.id.userAvatar)
            userName = row.findViewById(R.id.userName)
        }
    }
}
