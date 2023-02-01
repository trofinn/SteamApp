package com.esgi.steamapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esgi.steamapp.R
import com.esgi.steamapp.fragments.ForgotPasswordFragment

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)
        supportFragmentManager.beginTransaction().replace(R.id.container1, ForgotPasswordFragment()).commit()
    }
}