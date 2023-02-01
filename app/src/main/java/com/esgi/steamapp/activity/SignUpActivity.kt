package com.esgi.steamapp.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esgi.steamapp.R
import com.esgi.steamapp.fragments.SignUpFragment
import com.esgi.steamapp.R
import com.esgi.steamapp.fragments.SignUpFragment

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        supportFragmentManager.beginTransaction().replace(R.id.container1, SignUpFragment()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.container1, SignUpFragment()).commit()
    }
}