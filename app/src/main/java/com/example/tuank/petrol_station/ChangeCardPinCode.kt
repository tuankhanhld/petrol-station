package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangeCardPinCode : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var pinCode: EditText
    lateinit var btnChangeNewPin: Button
    lateinit var txtStatus: TextView

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    val use = auth.currentUser
    val id: String = use!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_card_pin_code)
        setMyStatus()
        btnBack = findViewById(R.id.btnBack)
        pinCode = findViewById(R.id.pinCode)
        btnChangeNewPin = findViewById(R.id.btnChangeNewPin)
        txtStatus = findViewById(R.id.txtStatus)

        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })

        btnChangeNewPin.setOnClickListener(View.OnClickListener {
            if (!pinCode.text.isEmpty()){
                checkOldPin()
            }

        })
    }

    private fun checkOldPin() {
        db.child("Account").child(id).child("PINCODE").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value.toString() == pinCode.text.toString()){
                    txtStatus.text = null
                    startActivity(Intent(this@ChangeCardPinCode, ChangeCardPinCodeNew::class.java))
                }else {
                    txtStatus.text = "Password incorrect"
                }

            }
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@ChangeCardPinCode.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChangeCardPinCode, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
