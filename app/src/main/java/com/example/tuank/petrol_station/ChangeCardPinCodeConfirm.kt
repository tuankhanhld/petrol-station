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

class ChangeCardPinCodeConfirm : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var pinCode: EditText
    lateinit var btnChangeFinalPin: Button

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_card_pin_code_confirm)

        setMyStatus()
        btnBack = findViewById(R.id.btnBack)
        pinCode = findViewById(R.id.pinCode)
        btnChangeFinalPin = findViewById(R.id.btnChangeFinalPin)

        val confPin = intent.getStringExtra("pincode")
        btnBack.setOnClickListener(View.OnClickListener {
            backToPrevious()
        })

        btnChangeFinalPin.setOnClickListener(View.OnClickListener {
            if (!pinCode.text.isEmpty() && pinCode.text.toString() == confPin){
                checkNewPin()
            }else{
                backToPrevious()
                startActivity(Intent(this@ChangeCardPinCodeConfirm, ChangeCardPinCodeNew::class.java))
            }

        })
    }

    private fun backToPrevious() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private fun checkNewPin() {
        db.child("Account").child(id).child("PINCODE").setValue(pinCode.text.toString().toInt())
        Toast.makeText(this@ChangeCardPinCodeConfirm, "Changed PINCODE successful!", Toast.LENGTH_LONG).show()
        startActivity(Intent(this@ChangeCardPinCodeConfirm, scr_home::class.java))
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@ChangeCardPinCodeConfirm.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChangeCardPinCodeConfirm, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
