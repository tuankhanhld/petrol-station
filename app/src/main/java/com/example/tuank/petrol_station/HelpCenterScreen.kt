package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HelpCenterScreen : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var contentMessage: EditText
    lateinit var subjectFill: EditText
    lateinit var btnSendHelp: Button

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_center_screen)
        setMyStatus()
        btnBack = findViewById(R.id.btnBack)
        contentMessage = findViewById(R.id.contentMessage)
        subjectFill = findViewById(R.id.subjectFill)
        btnSendHelp = findViewById(R.id.btnSendHelp)

        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        btnSendHelp.setOnClickListener(View.OnClickListener {
            if (!contentMessage.text.isEmpty() && !subjectFill.text.isEmpty()){
                sendMessageHelp()
            }
            else{
                Toast.makeText(this@HelpCenterScreen, "No content filled!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendMessageHelp() {

        val time = SimpleDateFormat("dd/M/yyyy")
        val date = SimpleDateFormat("hh:mm:ss")

        val currentTime = time.format(Date())
        val currentDate = date.format(Date())

        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("content").setValue(contentMessage.text.toString())
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("email").setValue(use!!.email)
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("avatarUrl").setValue(use.photoUrl.toString())
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("date").setValue(currentDate)
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("time").setValue(currentTime)
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("reply").setValue("")
        db.child("customer_feedback").child("messages").child(id).child(subjectFill.text.toString()).child("status_read").setValue(1)
        Toast.makeText(this@HelpCenterScreen, "Send successful message!", Toast.LENGTH_LONG).show()

        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@HelpCenterScreen.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@HelpCenterScreen, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
