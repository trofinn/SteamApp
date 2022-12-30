package com.esgi.steamapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var email : EditText
    private lateinit var send_mail : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var back_button : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)
        email = findViewById(R.id.email)
        send_mail = findViewById(R.id.send_email_button)
        back_button = findViewById(R.id.back_button)

        send_mail.setOnClickListener() {
            sendRecoveryMail()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        back_button.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendRecoveryMail() {
        val email = email.text.toString().trim()

        if(email.isBlank()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener{
            task -> if (task.isSuccessful) {
                Log.d(TAG,"Email sent")
        }
        }
    }
}