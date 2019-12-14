package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangePassword : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var oldPass: EditText
    lateinit var newPass: EditText
    lateinit var rePass: EditText
    lateinit var btnChangePass: Button

    var db = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()

    val use = auth.currentUser
    val id: String = use!!.uid

    lateinit var proGressbar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setMyStatus()
        btnBack = findViewById(R.id.btnBack)
        oldPass = findViewById(R.id.oldPass)
        newPass = findViewById(R.id.newPass)
        rePass = findViewById(R.id.rePass)
        btnChangePass = findViewById(R.id.btnChangePass)
        proGressbar = findViewById(R.id.proGressbar)
        proGressbar.visibility = View.INVISIBLE
        btnBack.setOnClickListener(View.OnClickListener {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        })
        btnChangePass.setOnClickListener(View.OnClickListener {
            if (oldPass.text.isEmpty() || newPass.text.isEmpty() || rePass.text.isEmpty()){
                Toast.makeText(this@ChangePassword, "please fill all", Toast.LENGTH_LONG).show()
            }
            else if (newPass.text == rePass.text){
                Toast.makeText(this@ChangePassword, "password and re-password not same", Toast.LENGTH_LONG).show()
            }else{
                changePassWord()
            }

        })

    }

    private fun changePassWord() {
        btnChangePass.visibility = View.INVISIBLE
        proGressbar.visibility = View.VISIBLE
        use!!.updatePassword(newPass.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                proGressbar.visibility = View.INVISIBLE
                btnChangePass.visibility = View.VISIBLE
                Toast.makeText(this@ChangePassword, "Change password success", Toast.LENGTH_LONG).show()
            } else {
                proGressbar.visibility = View.INVISIBLE
                btnChangePass.visibility = View.VISIBLE
                Toast.makeText(this@ChangePassword, "Failed! please, try again", Toast.LENGTH_LONG).show()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMyStatus() {
        val window = this@ChangePassword.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@ChangePassword, R.color.my_statusbar_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
