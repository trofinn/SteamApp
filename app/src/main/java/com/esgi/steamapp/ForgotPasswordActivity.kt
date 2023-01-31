package com.esgi.steamapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)
        supportFragmentManager.beginTransaction().replace(R.id.container1,ForgotPasswordFragment()).commit()
    }
}