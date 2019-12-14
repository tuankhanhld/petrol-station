package com.example.tuank.fingerprint_authentication


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.tuank.petrol_station.AuthenticationListener
import com.example.tuank.petrol_station.FingerprintHandler
import com.example.tuank.petrol_station.Utils
import com.example.tuank.petrol_station.scr_home
import kotlinx.android.synthetic.main.activity_fingerprint_login.*

@SuppressLint("Registered")
class FingerprintAuthen: AppCompatActivity(), AuthenticationListener {
    var check: Int = 0
    private lateinit var sharedPreferences: SharedPreferences
    private var fingerprintHandler: FingerprintHandler? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
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
        println("kkkkkkkkkkkkkk")
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationFailure(error: String?) {
        error?.let {
        }
    }

}