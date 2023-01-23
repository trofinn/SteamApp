package com.esgi.steamapp.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.esgi.steamapp.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var email : EditText
    lateinit var password : EditText
    private lateinit var login_button : Button
    private lateinit var forgot_password : TextView
    private lateinit var signup_button : Button

    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this,
            R.drawable.background
        ))

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login_button = findViewById(R.id.login)
        forgot_password = findViewById(R.id.forgot_password)
        signup_button = findViewById(R.id.new_account)

        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener() {
            login()
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        forgot_password.setOnClickListener() {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        signup_button.setOnClickListener() {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login() {
        val email = email.text.toString().trim()
        val password = password.text.toString().trim()
        if(email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email.trim(),password.trim()).addOnCompleteListener(this) {
            task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Connexion Success", Toast.LENGTH_SHORT).show()
            }
            else {
                Log.w(TAG,"eeeeeeeeeeeee {${email.toString()}}")
                Log.w(TAG, "createUserWithEmail:failure ", task.exception)
                Toast.makeText(this, "Connexion Echoué", Toast.LENGTH_SHORT).show()
            }

        }

    }
}