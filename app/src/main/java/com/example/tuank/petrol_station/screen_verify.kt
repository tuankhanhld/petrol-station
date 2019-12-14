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
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class screen_verify : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_verify)

        val window = this@screen_verify.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@screen_verify, R.color.my_statusbar_color_green))

        val btnverify = findViewById<View>(R.id.btnverify) as Button
        val back = findViewById<View>(R.id.back) as TextView

        back.setOnClickListener(View.OnClickListener {
            view -> backtomain()
        })
        btnverify.setOnClickListener(View.OnClickListener {
            view -> forgetpass()
        })
    }
    private fun forgetpass(){

        val emailtxt = findViewById<View>(R.id.Emailre) as EditText
        var emailre = emailtxt.text.toString()
        Thread.sleep(1000)
        if(!emailre.isEmpty()){

            auth.sendPasswordResetEmail(emailre).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "check email de dat lai mat khau!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity ::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }
        }else{
            Toast.makeText(this,"Vui long nhap vao Email!", Toast.LENGTH_LONG).show()
        }
    }

    private fun backtomain(){
        startActivity(Intent(this, MainActivity ::class.java))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
}
