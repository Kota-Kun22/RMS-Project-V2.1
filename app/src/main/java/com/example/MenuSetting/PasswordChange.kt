package com.example.MenuSetting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.MainActivity
import com.example.rms_project_v2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class PasswordChange : AppCompatActivity() {

    private lateinit var currentPasswordInput: TextInputLayout
    private lateinit var newPasswordInput: TextInputLayout
    private lateinit var confirmPasswordInput: TextInputLayout
    private lateinit var saveButton: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appenius: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_change_activity)

        currentPasswordInput = findViewById(R.id.currentPassword)
        newPasswordInput = findViewById(R.id.newPassword)
        confirmPasswordInput = findViewById(R.id.confirmPassword)
        saveButton = findViewById(R.id.save)
        firebaseAuth = FirebaseAuth.getInstance()
//        appenius = findViewById(R.id.appeniusPrivateLimited)
//        appenius.setOnClickListener{
//            val intent = Intent(Intent.ACTION_VIEW)
//            val url = "https://appeniusprivatelimited.com/"
//            intent.data = Uri.parse(url)
//            this.startActivity(intent)
//        }

        saveButton.setOnClickListener {
            val currentPassword = currentPasswordInput.editText?.text.toString()
            val newPassword = newPasswordInput.editText?.text.toString()
            val confirmPassword = confirmPasswordInput.editText?.text.toString()

            if (currentPassword.isEmpty()) {
                showToast("Please enter your current password")
                return@setOnClickListener
            }

            if (newPassword.isEmpty()) {
                showToast("Please enter a new password")
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                showToast("Please confirm your password")
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            val user = firebaseAuth.currentUser
            if (user != null) {
                reauthenticateAndChangePassword(user, currentPassword, newPassword)
            } else {
                showToast("No authenticated user found")
            }
        }
    }

    private fun reauthenticateAndChangePassword(user: FirebaseUser, currentPassword: String, newPassword: String) {
        val userEmail = user.email
        if (userEmail != null) {
            val credential = EmailAuthProvider.getCredential(userEmail, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    showToast("Password changed successfully")
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    showToast("Error: ${updateTask.exception?.message}")
                                }
                            }
                    } else {
                        showToast("Re-authentication failed: ${reauthTask.exception?.message}")
                    }
                }
        } else {
            showToast("User email is null")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
