package com.example.tuank.petrol_station

import android.animation.ValueAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_screen_scan.*
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.ProgressBar
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    public var auth = FirebaseAuth.getInstance()
    var db = FirebaseDatabase.getInstance().reference
    var k: Int = 0
//    var t: Int = 0
    lateinit var proGressbar: ProgressBar
    lateinit var btnsignin: Button
    lateinit var androidId: String

    //login states
    private lateinit var sharedPreferences: SharedPreferences
    var LOGIN_STATE_KEY: String = "111"
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val window = this@MainActivity.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
//        @SuppressLint("HardwareIds")
//        androidId = Settings.Secure.getString(contentResolver,
//                Settings.Secure.ANDROID_ID)
//        db.child("devices manager").child("android_id_login").child(androidId).removeValue()

        val btnshow = findViewById<View>(R.id.btnshow) as Button
        btnsignin = findViewById(R.id.btnsignin)
        val btnsignup = findViewById<View>(R.id.btnsignup1) as Button
        val btnforgot = findViewById<View>(R.id.btnforgot) as Button
        proGressbar = findViewById(R.id.proGressbar)
        proGressbar.visibility = View.INVISIBLE
        val am = AccountManager.get(this) // "this" references the current Context
        val accounts = am.getAccountsByType("com.google")
//        button.setOnClickListener(View.OnClickListener {
//            view -> click()
//        })
        btnsignin.setOnClickListener(View.OnClickListener {
            view -> login()
        })
        btnsignup.setOnClickListener(View.OnClickListener {
            view -> register()
        })
        btnforgot.setOnClickListener(View.OnClickListener {
            view -> forgot()
        })
        btnshow.setOnClickListener(View.OnClickListener {
            view -> showpass()
        })
    }
    @SuppressLint("HardwareIds")
    public fun login() {
        btnsignin.visibility = View.INVISIBLE
        proGressbar.visibility = View.VISIBLE
//        val filename = "resources/data.txt"
//        val mydata = File(filename)
        val Emailtxt = findViewById<View>(R.id.Emaillog) as EditText
        val Passwordtxt = findViewById<View>(R.id.password) as EditText

        var email = Emailtxt.text.toString()
        var password = Passwordtxt.text.toString()

        if(!email.isEmpty() && !password.isEmpty()){
            this.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, OnCompleteListener {task ->
                if (task.isSuccessful){

                    val use = auth.currentUser
                    val id: String = use!!.uid
                    db.child("id_auth").setValue(id)

                    saveLoginState()
                    savePasswordSharedPreferences(password)
                    proGressbar.visibility = View.INVISIBLE
                    btnsignin.visibility = View.VISIBLE
                    startActivity(Intent(this, scr_home ::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }else{
                    proGressbar.visibility = View.INVISIBLE
                    btnsignin.visibility = View.VISIBLE
                    Toast.makeText(this, "login failed!", Toast.LENGTH_LONG).show()
                }
            })
        }else{
            proGressbar.visibility = View.INVISIBLE
            btnsignin.visibility = View.VISIBLE
            Toast.makeText(this, "xin dien dien day du!",Toast.LENGTH_LONG).show()
        }
    }

    private fun saveLoginState() {
        sharedPreferences.edit().putString(LOGIN_STATE_KEY, "1").apply()
    }

    private fun register(){
        startActivity(Intent(this,screen_signup ::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    private fun forgot(){
        startActivity(Intent(this, screen_verify ::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
    private fun showpass(){
        if (k == 0) {
            btnshow.setBackgroundResource(R.drawable.show)
            password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            k = k + 1
        }else{
            btnshow.setBackgroundResource(R.drawable.hide)
            password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            k = k - 1
        }
    }
    private fun savePasswordSharedPreferences(password: String) {
        if (Utils.checkSensorState(this)) {
            val encoded = Utils.encryptString(password)
            sharedPreferences.edit().putString("PUBLIC_KEY_PASSWORD", encoded).apply()
            sharedPreferences.edit().putString("PUBLIC_KEY_TURN_ON_FINGERPRINT", "1").apply()
        }
    }

}
