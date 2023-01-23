package com.esgi.steamapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esgi.steamapp.R
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var user_name : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var password_verification : EditText
    private lateinit var sign_up : Button
    private lateinit var back_button : ImageButton
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        user_name = findViewById(R.id.user_name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        password_verification = findViewById(R.id.password_verification)
        sign_up = findViewById(R.id.new_account)
        back_button = findViewById(R.id.back_button)
        auth = FirebaseAuth.getInstance()

        sign_up.setOnClickListener() {
            signUpUser()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        back_button.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signUpUser() {
        val user_name = user_name.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()
        val password_verification = password_verification.text.toString()

        if (user_name.isBlank() || email.isBlank() || password.isBlank() || password_verification.isBlank()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != password_verification) {
            Toast.makeText(this, "Le mot de passe ne corresponde pas", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Inscription complete", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this,"Inscription echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }

}