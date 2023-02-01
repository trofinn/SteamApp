package com.esgi.steamapp.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.esgi.steamapp.activity.MainActivity
import com.esgi.steamapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgotPasswordFragment : Fragment() {
    lateinit var email : EditText
    private lateinit var send_mail : Button
    private lateinit var auth : FirebaseAuth
    private lateinit var back_button : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_button = view.findViewById(R.id.back_button)
        email = view.findViewById(R.id.email)
        send_mail = view.findViewById(R.id.send_email_button)
        auth = Firebase.auth
        send_mail.setOnClickListener() {
            sendRecoveryMail()
        }

        back_button.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun sendRecoveryMail() {
        val email = email.text.toString().trim()

        if(email.isBlank()) {
            Toast.makeText(requireContext(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener{
                task -> if (task.isSuccessful) {
                    send_mail.setOnClickListener() {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    Log.d(ContentValues.TAG,"Email sent") }
        }
    }

}