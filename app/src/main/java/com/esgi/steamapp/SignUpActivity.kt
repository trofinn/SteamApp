package com.esgi.steamapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        supportFragmentManager.beginTransaction().replace(R.id.container1,SignUpFragment()).commit()
    }
}