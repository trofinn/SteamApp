package com.esgi.steamapp.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esgi.steamapp.R
import com.esgi.steamapp.fragments.SignInFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion)
        supportFragmentManager.beginTransaction().replace(R.id.container1, SignInFragment()).commit()
    }
}