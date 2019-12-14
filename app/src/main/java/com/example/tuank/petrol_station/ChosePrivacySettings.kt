package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChosePrivacySettings : AppCompatActivity() {
    lateinit var changePass: TextView
    lateinit var changePinCard: TextView
    lateinit var btnBack: ImageView

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()

    val use = auth.currentUser
    val id: String = use!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chose_privacy_settings)
        setMyStatus()

        changePass = findViewById(R.id.changePass)
        changePinCard = findViewById(R.id.changePinCard)
        btnBack = findViewById(R.id.btnBack)

        changePass.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ChosePrivacySettings, ChangePassword ::class.java))
        })
        changePinCard.setOnClickListener(View.OnClickListener {
            checkCardUserExist()
        })

        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@ChosePrivacySettings.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChosePrivacySettings, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private fun checkCardUserExist(){
        db.child("Account").child(id).child("RFID").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null){
                    startActivity(Intent(this@ChosePrivacySettings, ChangeCardPinCode ::class.java))
                }
                else{
                    showDialogMakeCard()
                }
            }
        })
    }

    private fun showDialogMakeCard() {
        val dialog = Dialog(this@ChosePrivacySettings)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.dialog_require_card)

        val btnScanDialog = dialog .findViewById(R.id.btnOkDialog) as Button

        btnScanDialog.setOnClickListener {
            dialog .dismiss()

        }
        dialog.show()
    }
}
