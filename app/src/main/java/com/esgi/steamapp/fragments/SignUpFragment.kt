package com.esgi.steamapp.fragments

import android.content.Intent
import android.os.Bundle
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
import com.esgi.steamapp.activity.SignUpActivity
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {
    lateinit var user_name : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var password_verification : EditText
    private lateinit var sign_up : Button
    private lateinit var back_button : ImageButton
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_name = view.findViewById(R.id.user_name)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        password_verification = view.findViewById(R.id.password_verification)
        sign_up = view.findViewById(R.id.new_account)
        back_button = view.findViewById(R.id.back_button)
        auth = FirebaseAuth.getInstance()
        sign_up.setOnClickListener() {
            signUpUser()
        }

        back_button.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser() {
        var user_name = user_name.text.toString()
        var email = email.text.toString()
        var password = password.text.toString()
        var password_verification = password_verification.text.toString()

        if (user_name.isBlank() || email.isBlank() || password.isBlank() || password_verification.isBlank()) {
            Toast.makeText(requireContext(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != password_verification) {
            Toast.makeText(requireContext(), "Le mot de passe ne corresponde pas", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener() {
            if (it.isSuccessful) {
                startActivity(Intent(requireContext(), SignUpActivity::class.java))
                Toast.makeText(requireContext(), "Inscription complete", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(),"Inscription echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }
}