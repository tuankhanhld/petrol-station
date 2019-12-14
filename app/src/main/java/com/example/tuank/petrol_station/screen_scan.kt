package com.example.tuank.petrol_station

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Window
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_screen_scan.*
import android.Manifest.permission
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_screen_signup.*
import java.security.AccessController.getContext


class screen_scan : AppCompatActivity(), ZXingScannerView.ResultHandler {
    lateinit var database: DatabaseReference
    var auth = FirebaseAuth.getInstance()
    lateinit var sttType: String
    lateinit var id: String
    var check: Int = 0
    var MY_PERMISSIONS_REQUEST_CAMERA: Int = 1000
    init {
        database = FirebaseDatabase.getInstance().reference
    }
    lateinit var zXingScannerView: ZXingScannerView
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        sttType = intent.getStringExtra("typePay")
        zXingScannerView = ZXingScannerView(applicationContext)
        setContentView(zXingScannerView)
        zXingScannerView.setResultHandler(this)
        zXingScannerView.startCamera()
    }
    override fun onPause() {
        super.onPause()
        zXingScannerView.stopCamera()
    }
    override fun handleResult(result: Result) {
        val use = auth.currentUser
        val uid = use!!.uid

        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (stationName: DataSnapshot in p0.children){
                    if (result.text.toString() == stationName.key.toString() ){
                        check = 1
                        database.child("Account").child(uid).child("pay_id").setValue(result.text)
                        if (sttType == "gas"){
                            val intent = Intent(this@screen_scan, scr_home ::class.java)
                            intent.putExtra("creatGas","1")
                            startActivity(intent)
                            finish()
                        }
                        else if (sttType == "oil"){
                            val intent = Intent(this@screen_scan, scr_home ::class.java)
                            intent.putExtra("creatGas","2")
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                if (check == 0){
                    Toast.makeText(this@screen_scan, "Wrong station's name", Toast.LENGTH_LONG).show()
                    recreate()
                }

            }
        })
    }


}
