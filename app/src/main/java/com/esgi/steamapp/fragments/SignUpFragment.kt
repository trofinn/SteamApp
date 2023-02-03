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
    lateinit var userName : EditText
    lateinit var email : EditText
    lateinit var password : EditText
    lateinit var passwordVerification : EditText
    private lateinit var signUp : Button
    private lateinit var backButton : ImageButton
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById(R.id.user_name)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        passwordVerification = view.findViewById(R.id.password_verification)
        signUp = view.findViewById(R.id.new_account)
        backButton = view.findViewById(R.id.back_button)
        auth = FirebaseAuth.getInstance()
        signUp.setOnClickListener() {
            signUpUser()
        }

        backButton.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser() {
        var userNameText = userName.text.toString()
        var emailText = email.text.toString()
        var passwordText = password.text.toString()
        var passwordVerificationText = passwordVerification.text.toString()

        if (userNameText.isBlank()) { userName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0); return}
        else {userName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        if (emailText.isBlank()) { email.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0); return }
        else {email.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        if (passwordVerificationText.isBlank()) { passwordVerification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0); return}
        else {passwordVerification.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        if (passwordText.isBlank()) { password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0); return}
        else {password.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)}
        if (passwordText != passwordVerificationText) {
            passwordVerification.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0)
            password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.warning,0)
            Toast.makeText(requireContext(), "Le mot de passe ne corresponde pas", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener() {
            if (it.isSuccessful) {
                startActivity(Intent(requireContext(), SignUpActivity::class.java))
                Toast.makeText(requireContext(), "Inscription complete", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(),"Inscription echoué ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}