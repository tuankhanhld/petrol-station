package com.example.tuank.users_message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.tuank.petrol_station.R
import com.example.tuank.petrol_station.dataSearchTransfer
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import android.text.method.TextKeyListener.clear



class CustomListViewMessageBox constructor(var context: Context, var arrayMessage: ArrayList<UserMessageData>): BaseAdapter(){
    class ViewHolder(row: View, status: Boolean){
        var messageContent: TextView
        var messageTime: TextView
        var senderAvatar: CircleImageView

        init {
            messageContent = row.findViewById(R.id.messageContent)
            messageTime = row.findViewById(R.id.messageTime)
            senderAvatar = if (status){
                row.findViewById(R.id.senderAvatar)
            } else{
                row.findViewById(R.id.receiveAvatar)
            }
        }
    }
    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View {
        val userMessageData: UserMessageData = getItem(position) as UserMessageData
        var view: View?
        var viewHolder : ViewHolder
        var layoutResource: Int
        if(userMessageData.isSender){
            layoutResource = R.layout.sender_message_item
        }else{
            layoutResource = R.layout.receiver_message_item
        }
        if (convertview == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(layoutResource, null)
            viewHolder = ViewHolder(view,userMessageData.isSender)
            view.tag = viewHolder

        }else{
            view = convertview
            viewHolder = convertview.tag as ViewHolder
        }

        viewHolder.messageContent.text = userMessageData.messageContentSender
        viewHolder.messageTime.text = userMessageData.messageTimeSender

        Picasso.get()
                .load(userMessageData.userAvatarUrlSender)
                .into(object: Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        viewHolder.senderAvatar.setImageBitmap(bitmap)
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }
                })
        return view as View
    }

    override fun getItem(p0: Int): Any {
        return arrayMessage.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return arrayMessage.size
    }

    fun setStatusSeen(status: String){
        if (arrayMessage[arrayMessage.size-1].isSender){
            arrayMessage[arrayMessage.size-1].messageTimeSender = status
        }
    }
}