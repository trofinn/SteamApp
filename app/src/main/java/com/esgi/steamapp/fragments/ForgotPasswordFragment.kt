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
import androidx.navigation.fragment.findNavController
import com.esgi.steamapp.activity.MainActivity
import com.esgi.steamapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgotPasswordFragment : Fragment() {
    lateinit var email: EditText
    private lateinit var sendMail: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton = view.findViewById(R.id.back_button)
        email = view.findViewById(R.id.email)
        email.setText(ForgotPasswordFragmentArgs.fromBundle(requireArguments()).email)
        sendMail = view.findViewById(R.id.send_email_button)
        auth = Firebase.auth
        sendMail.setOnClickListener() {
            sendRecoveryMail()
        }

        backButton.setOnClickListener() {
            findNavController().navigateUp()
        }
    }

    private fun sendRecoveryMail() {
        val email = email.text.toString().trim()

        if (email.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Tous les champs sont obligatoires",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController().navigateUp()
                Toast.makeText(
                    requireContext(), "Recovery email sent",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(ContentValues.TAG, "Recovery email sent")
            }
        }
    }

}