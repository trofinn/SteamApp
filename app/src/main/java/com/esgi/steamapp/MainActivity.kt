package com.esgi.steamapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion)
        supportFragmentManager.beginTransaction().replace(R.id.container1,SignInFragment()).commit()
    }
}