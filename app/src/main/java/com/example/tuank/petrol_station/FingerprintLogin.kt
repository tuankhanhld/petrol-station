package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_fingerprint_login.*

class FingerprintLogin : AppCompatActivity(), AuthenticationListener {

    companion object {
        const val PUBLIC_KEY_PASSWORD = "PUBLIC_KEY_PASSWORD"
        const val PUBLIC_KEY_TURN_ON_FINGERPRINT = "PUBLIC_KEY_TURN_ON_FINGERPRINT"
    }
    lateinit var btnBack: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var fingerprintHandler: FingerprintHandler? = null
    //dialog
    private lateinit var dialog: Dialog
    var check = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = this@FingerprintLogin.window

        setStatusBar()

        setContentView(R.layout.activity_fingerprint_login)
        btnBack = findViewById(R.id.btnBack)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        btn_signup.setOnClickListener { signUp() }

        btnBack.setOnClickListener(View.OnClickListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
        })
        btnAdd.setOnClickListener { addFingerprint() }
    }

    private fun addFingerprint() {
        showDialogCheckPassCode()


    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.contains(PUBLIC_KEY_PASSWORD)) {
            showFingerprintIcon()
        } else {
            hideFingerPrintIcon()
        }
    }


    private fun hideFingerPrintIcon() {
        fingerprint_icon.visibility = GONE
        btnAdd.visibility = GONE
        btn_signup.visibility  = VISIBLE
        txtStatus.setText(R.string.Register_your_passcode)
    }

    private fun showFingerprintIcon() {
        fingerprint_icon.visibility = VISIBLE
        txtStatus.visibility = VISIBLE
        body.visibility = GONE
        txtStatus.setText(R.string.Register_done)
        btn_signup.visibility  = GONE
        btnAdd.visibility = VISIBLE
        initSensor()
    }

    override fun onStop() {
        super.onStop()
        fingerprintHandler?.cancel()
    }

    private fun signUp() {
        val password = editText.text.toString()
        if (password.isNotEmpty()) {
            savePassword(password)
            showFingerprintIcon()
        } else
            showToast(getString(R.string.empty_password))
    }

    private fun savePassword(password: String) {
        if (Utils.checkSensorState(this)) {
            val encoded = Utils.encryptString(password)
            sharedPreferences.edit().putString(PUBLIC_KEY_PASSWORD, encoded).apply()
            sharedPreferences.edit().putString(PUBLIC_KEY_TURN_ON_FINGERPRINT, "1").apply()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initSensor() {
        if (Utils.checkSensorState(this)) {
            val cryptoObject = Utils.cryptoObject
            if (cryptoObject != null) {
                val fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
                fingerprintHandler = FingerprintHandler(this, sharedPreferences, this)
                fingerprintHandler?.startAuth(fingerprintManager, cryptoObject)
            }
        }
    }

    override fun onAuthenticationSuccess(decryptPassword: String) {
        if (check == 0){
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
        else if (check == 1){
            hideFingerPrintIcon()
            body.visibility = VISIBLE
            setC(0)
            dialog.dismiss()
        }
    }

    public fun setC(check: Int){
        this.check = check
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationFailure(error: String?) {
        error?.let {
            txtStatus.text = it
        }
    }

    fun showToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showDialogCheckPassCode() {
        check = 1
        dialog = Dialog(this@FingerprintLogin)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(false)
        dialog .setContentView(R.layout.scan_fingerprint_dialog)

        val btnCancelDialog = dialog .findViewById(R.id.btnCancelDialog) as Button

        btnCancelDialog.setOnClickListener {
            dialog .dismiss()
        }
        dialog.show()
        initSensor()
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBar() {
        val window = this@FingerprintLogin.getWindow()

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@FingerprintLogin, R.color.my_statusbar_color))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (window.decorView.rootView != null) {
                window.decorView.rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

}
