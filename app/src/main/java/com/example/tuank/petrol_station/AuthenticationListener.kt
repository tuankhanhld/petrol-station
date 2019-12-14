package com.example.tuank.petrol_station

interface AuthenticationListener {

    fun onAuthenticationSuccess(decryptPassword: String)

    fun onAuthenticationFailure(error: String? = null)
}