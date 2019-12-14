package com.example.tuank.petrol_station

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.example.tuank.users_message.CustomListViewMessageBox
import com.example.tuank.users_message.UserMessageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter
import android.view.MotionEvent
import android.view.View.OnTouchListener





class MessageBox : AppCompatActivity() {
    lateinit var btnBack: ImageView
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    lateinit var arrayListMessageSender: ArrayList<UserMessageData>
    lateinit var arrayListMessageReceiver: ArrayList<UserMessageData>
    lateinit var messageContainer: ListView
    lateinit var receiveAvatar: CircleImageView
    lateinit var receiveName: TextView
    lateinit var receiverPhone: TextView
    lateinit var typeContent: EditText
    lateinit var btnSendMessage: Button

    val user = auth.currentUser
    val id: String = user!!.uid

    lateinit var userPhone: String
    lateinit var userId: String
    lateinit var userName: String
    lateinit var userAvtUrl: String

    lateinit var messHis: ArrayList<UserMessageData>
    lateinit var adapter: CustomListViewMessageBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_box)

        btnBack = findViewById(R.id.btnBack)
        messageContainer = findViewById(R.id.messageContainer)
        receiveAvatar = findViewById(R.id.receiveAvatar)
        receiveName = findViewById(R.id.receiveName)
        receiverPhone = findViewById(R.id.receiverPhone)
        typeContent = findViewById(R.id.typeContent)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        arrayListMessageSender = ArrayList()
        arrayListMessageReceiver = ArrayList()
        messHis = ArrayList()
        //get extra data
        userId = intent.getStringExtra("userId").toString()
        userName = intent.getStringExtra("userName").toString()
        userAvtUrl = intent.getStringExtra("userAvtUrl").toString()
        userPhone = intent.getStringExtra("userPhone").toString()
        setHeaderMessageBox()
        getHistoryMessage()
        getTextChange()

        btnSendMessage.setOnClickListener(View.OnClickListener {
            if (!typeContent.text.isEmpty()){
                addMessageSender()
            }
        })
        btnBack.setOnClickListener(View.OnClickListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
        })
    }

    private fun addMessageSender() {
        val time = SimpleDateFormat("hh:mm:ss")
        val cTime = SimpleDateFormat("yyyy_M_dd hh:mm:ss")
        val currentDateFull = cTime.format(Date())

        db.child("Account").child(userId).child("messages").child(id).child("receiver").child(currentDateFull).setValue(typeContent.text.toString())
        db.child("Account").child(id).child("messages").child(userId).child("sender").child(currentDateFull).setValue(typeContent.text.toString())
        arrayListMessageSender.add(UserMessageData(true, user?.photoUrl.toString(), typeContent.text.toString(), time.format(Date()).toString()))
        adapter.notifyDataSetChanged()
//        arrayListMessageSender.sortedWith(compareBy { it.messageTimeSender }).forEach {
//            messHis.add(it) }
//        adapter.updateData(UserMessageData(true, user?.photoUrl.toString(), typeContent.text.toString(), time.format(Date()).toString()))
//        messageContainer.adapter = CustomListViewMessageBox(this@MessageBox, arrayListMessageSender)
        typeContent.text = null
        scrollMyListViewToBottom()
        getStatusTyping()
    }
    private fun getMessageReceiver(){
        db.child("Account").child(id).child("messages").child(userId).child("receiver").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(data: DataSnapshot in dataSnapshot.children)
                {
                    if(!arrayListMessageSender.contains(UserMessageData(false, userAvtUrl, data.value.toString(), data.key.toString().split(" ")[1]))){
                        arrayListMessageSender.add(UserMessageData(false, userAvtUrl, data.value.toString(), data.key.toString().split(" ")[1]))
                        adapter.notifyDataSetChanged()
//                        arrayListMessageSender.sortedWith(compareBy { it.messageTimeSender }).forEach {
//                            messHis.add(it) }
//                        messageContainer.adapter = CustomListViewMessageBox(this@MessageBox, arrayListMessageSender)
                        scrollMyListViewToBottom()
                    }
                }
            }
        })
    }
    private fun getHistoryMessage(){
        db.child("Account").child(id).child("messages").child(userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(data: DataSnapshot in dataSnapshot.children)
                {
                    for(dataHistory: DataSnapshot in data.children)
                    {
                        if(data.key == "sender"){
                            arrayListMessageSender.add(UserMessageData(true, user?.photoUrl.toString(), dataHistory.value.toString(), dataHistory.key.toString().split(" ")[1]))
                        }
                        else if (data.key == "receiver"){
                            arrayListMessageSender.add(UserMessageData(false, userAvtUrl, dataHistory.value.toString(), dataHistory.key.toString().split(" ")[1]))
                        }
                    }
                }
                arrayListMessageSender.sortedWith(compareBy { it.messageTimeSender }).forEach {
                    messHis.add(it) }
                arrayListMessageSender.clear()
                arrayListMessageSender = messHis
                adapter = CustomListViewMessageBox(this@MessageBox, arrayListMessageSender)
                messageContainer.adapter = adapter

                scrollMyListViewToBottom()
                getMessageReceiver()
            }
        })
    }
    fun selector(p: UserMessageData): String = p.messageTimeSender
    private fun setHeaderMessageBox(){
        receiveName.text = userName
        receiverPhone.text = userPhone
        Picasso.get()
                .load(userAvtUrl)
                .into(object: Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        receiveAvatar.setImageBitmap(bitmap)
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    }
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }
                })
    }

    private fun scrollMyListViewToBottom() {
        messageContainer.post(Runnable {
            // Select the last row so it will scroll into view...
            messageContainer.setSelection(messageContainer.getCount() - 1)
        })
    }
    private fun getTextChange(){
        typeContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                db.child("Account").child(id).child("messages").child(userId).child("typing").setValue(1)
            }

        })

    }
    private fun getStatusTyping(){
        db.child("Account").child(userId).child("messages").child(id).child("typing").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value.toString().toInt() == 1){
                    adapter.setStatusSeen("Typing")
                }
                else if (dataSnapshot.value.toString().toInt() == 2){
                    adapter.setStatusSeen("Seen")
                }
            }
        })
    }
}
