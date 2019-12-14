package com.example.tuank.petrol_station

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class LostCardNotify : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var btnNotifyLostCard: Button
    lateinit var imgLost: ImageView
    lateinit var txtLostType: TextView

    val db = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid

    var typeLost: Int = 0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_card_notify)
        //set status bar
        setStatusBar()

        btnBack = findViewById(R.id.btnBack)
        btnNotifyLostCard = findViewById(R.id.btnNotifyLostCard)
        imgLost = findViewById(R.id.imgLost)
        txtLostType = findViewById(R.id.txtLostType)
        typeLost = intent.getStringExtra("type lost").toInt()

        if (typeLost == 0){
            checkStatusNotifiCardDrop()
        }else if (typeLost == 1){
            checkStatusNotifiCardHack()
        }
        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })
        btnNotifyLostCard.setOnClickListener(View.OnClickListener {
            if (typeLost == 0){
                sendNotifiDrop()
            }else if (typeLost == 1){
                sendNotifiHack()
            }

        })

    }

    private fun setStatusBar() {
        val window = this@LostCardNotify.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this@LostCardNotify, R.color.my_statusbar_color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
    }

    fun backToMain(){
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    fun sendNotifiDrop(){
        db.child("Card drop").child(id).setValue(1)
        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card_done)
        txtLostType.setText(R.string.drop2)

    }
    fun sendNotifiHack(){
        db.child("Card lost").child(id).setValue(1)
        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card_done)
        txtLostType.setText(R.string.hack2)
    }

    fun checkStatusNotifiCardDrop(){
        imgLost.setImageResource(R.drawable.lost_card_drop)
        db.child("Card drop").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (dtId: DataSnapshot in p0.children){
                    val idExist: String = dtId.key.toString()
                    if(idExist != id){
                        txtLostType.setText(R.string.drop1)
                        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card)
                    }else{
                        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card_done)
                        txtLostType.setText(R.string.drop2)
                    }

                }
            }
        })
    }
    fun checkStatusNotifiCardHack(){
        imgLost.setImageResource(R.drawable.lost_card_hack)
        db.child("Card lost").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                for (dtId: DataSnapshot in p0.children) {
                    val idExist: String = dtId.key.toString()
                    if (idExist != id) {
                        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card)
                        txtLostType.setText(R.string.hack1)
                    }else{
                        btnNotifyLostCard.setBackgroundResource(R.drawable.btn_notify_lost_card_done)
                        txtLostType.setText(R.string.hack2)
                    }
                }
            }
        })
    }
}
