package com.example.tuank.petrol_station

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal

@TargetApi(Build.VERSION_CODES.M)
internal class FingerprintHandler(private val mContext: Context,
                                  private val mSharedPreferences: SharedPreferences,
                                  private val mListener: AuthenticationListener) : FingerprintManager.AuthenticationCallback() {

    private val cancellationSignal: CancellationSignal?

    init {
        cancellationSignal = CancellationSignal()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun startAuth(fingerprintManager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        mListener.onAuthenticationFailure(errorCode.toString()+ " : " + errString.toString())
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        mListener.onAuthenticationFailure(helpCode.toString()+ " : " + helpString.toString())
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        val cipher = result.cryptoObject.cipher
        val encoded = mSharedPreferences.getString(FingerprintLogin.PUBLIC_KEY_PASSWORD, null)
        Utils.decryptString(encoded, cipher)?.let {
            mListener.onAuthenticationSuccess(it)
        } ?: run {
            mListener.onAuthenticationFailure()
            (mContext as FingerprintLogin).showToast("onAuthenticationFailed")
        }
    }

    override fun onAuthenticationFailed() {
        (mContext as FingerprintLogin).showToast("onAuthenticationFailed")
        mListener.onAuthenticationFailure()
    }

    fun cancel() {
        cancellationSignal?.cancel()
    }
}