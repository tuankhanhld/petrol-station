package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationMessageScreen : AppCompatActivity() {
    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    lateinit var arrayListNotificationMess: ArrayList<DataNotificationItem>
    lateinit var listViewNotificationMess: ListView
    lateinit var btnBack: ImageView
    var init: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_message_screen)
        setStatusBar()
        listViewNotificationMess = findViewById(R.id.listViewNotificationMess)
        btnBack = findViewById(R.id.btnBack)
        arrayListNotificationMess = ArrayList()
        btnBack.setOnClickListener(View.OnClickListener {
            backToMain()
        })

        getDataNotifi()
    }

    fun getDataNotifi(){
        listViewNotificationMess.adapter = null
        arrayListNotificationMess.clear()
        val use = auth.currentUser
        val id: String = use!!.uid
        db.child("customer_feedback").child("messages").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            var dataDateNo: String = ""
            var dataTimeNo: String = ""
            var dataContentNo: String = ""
            var dataFromNo: String = ""
            var dataSubNo: String = ""
            var dataEmailNo: String = ""
            override fun onDataChange(p0: DataSnapshot) {
                for (dtSubject: DataSnapshot in p0.children) {
                    dataSubNo = dtSubject.key.toString()
                        dataDateNo = dtSubject.child("date").value.toString()
                        dataTimeNo = dtSubject.child("time").value.toString()
                        dataFromNo = "Admin"
                        dataContentNo = dtSubject.child("reply").value.toString()
                        if (dataContentNo != ""){
                            db.child("customer_feedback").child("messages").child(id).child(dataSubNo).child("status_read").setValue(0)
                            addToNotifiList(dataDateNo, dataTimeNo, dataFromNo, dataSubNo, dataContentNo)
                        }
                }
            }
        })
    }

    private fun addToNotifiList(date: String, time: String, from: String, sub: String, content: String) {
        arrayListNotificationMess.add(DataNotificationItem(date, time, from, sub, content))
        listViewNotificationMess.adapter = CustomAdapterNotification(this@NotificationMessageScreen, arrayListNotificationMess)
    }

    private fun backToMain() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
            finish()
        } else {
            getSupportFragmentManager().popBackStack();
            finish()
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBar() {
        val window = this@NotificationMessageScreen.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@NotificationMessageScreen, R.color.my_statusbar_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
