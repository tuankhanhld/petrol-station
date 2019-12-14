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
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangeCardPinCodeNew : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var pinCode: EditText
    lateinit var btnChangeConfirmPin: Button

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_card_pin_code_new)

        setMyStatus()
        btnBack = findViewById(R.id.btnBack)
        pinCode = findViewById(R.id.pinCode)
        btnChangeConfirmPin = findViewById(R.id.btnChangeConfirmPin)

        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        btnChangeConfirmPin.setOnClickListener(View.OnClickListener {
            if (!pinCode.text.isEmpty()){
                checkNewPin()
            }

        })
    }

    private fun checkNewPin() {
        val intent = Intent(this@ChangeCardPinCodeNew, ChangeCardPinCodeConfirm ::class.java)
        intent.putExtra("pincode",pinCode.text.toString())
        startActivity(intent)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@ChangeCardPinCodeNew.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChangeCardPinCodeNew, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
