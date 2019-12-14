package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class NotificationLostCard : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var imgDetect: ImageView
    lateinit var btnYes: Button
    lateinit var btnNo: Button
    lateinit var imageExpand: ImageView
    lateinit var notifiCardHolder: RelativeLayout
    lateinit var url: String

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_lost_card)
        setStatusBar()
        btnBack = findViewById(R.id.btnBack)
        imgDetect = findViewById(R.id.imgDetect)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)
        imageExpand = findViewById(R.id.imageExpand)
        notifiCardHolder = findViewById(R.id.notifiCardHolder)

        getInforDetect()

        btnYes.setOnClickListener(View.OnClickListener {
            setDatabaseDetect(0)
        })
        btnNo.setOnClickListener(View.OnClickListener {
            setDatabaseDetect(1)
        })
        imgDetect.setOnClickListener(View.OnClickListener {
            Picasso.get().load(url).into(imageExpand)
        })
        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })
    }

    private fun setDatabaseDetect(statusConfirm: Int) {
        db.child("Account").child(id).child("response").child("card lost").setValue("none")
        db.child("Card lost").child(id).setValue(statusConfirm)
        imageExpand.visibility = View.GONE
    }

    private fun getInforDetect() {
        db.child("Account").child(id).child("response").child("card lost").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val url = p0.value.toString()
                if ( url == "none"){
                    notifiCardHolder.visibility = View.GONE
                }else{
                    Picasso.get()
                            .load(url)
                            .into(imgDetect)
                    setUrlImgDetect(url)
                }
            }
        })
    }

    private fun setUrlImgDetect(url: String) {
        this.url = url
    }
    fun backToMain(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBar() {
        val window = this@NotificationLostCard.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@NotificationLostCard, R.color.my_statusbar_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
