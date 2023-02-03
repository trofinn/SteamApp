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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.esgi.steamapp.activity.ForgotPasswordActivity
import com.esgi.steamapp.R
import com.esgi.steamapp.activity.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    lateinit var email : EditText
    lateinit var password : EditText
    private lateinit var login_button : Button
    private lateinit var forgot_password : TextView
    private lateinit var signup_button : Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        login_button = view.findViewById(R.id.login)
        forgot_password = view.findViewById(R.id.forgot_password)
        signup_button = view.findViewById(R.id.new_account)
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener() {
            login()
        }

        forgot_password.setOnClickListener() {
            val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        signup_button.setOnClickListener() {
            val intent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()
        if(emailText.isBlank()) {
            email.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0)
            return
        }
        else {email.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        if(passwordText.isBlank()) {
            password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0)
            return
        }
        else {password.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        auth.signInWithEmailAndPassword(emailText.trim(),passwordText.trim()).addOnCompleteListener() {
                task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Connexion Success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    SignInFragmentDirections.actionSignInFragmentToHomePageFragment(emailText,"","","","",emailText))
            }
            else {
                Log.w(ContentValues.TAG, "createUserWithEmail:failure ", task.exception)
                Toast.makeText(requireContext(), "Connexion Echoué", Toast.LENGTH_SHORT).show()
            }
        }
    }
}