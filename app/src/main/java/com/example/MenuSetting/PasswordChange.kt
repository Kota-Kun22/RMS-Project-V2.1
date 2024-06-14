package com.example.MenuSetting

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rms_project_v2.R
import android.widget.Toast
import com.example.MainActivity
import com.google.firebase.auth.FirebaseAuth

class PasswordChange : AppCompatActivity() {
    private lateinit var newPass: EditText
    private lateinit var rePass: EditText
    private lateinit var save: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_change_activity)

        newPass = findViewById(R.id.newPassword)
        rePass = findViewById(R.id.confirmPassword)
        save = findViewById(R.id.save)

        save.setOnClickListener {
            val newPassword = newPass.text.toString()
            val confirmPassword = rePass.text.toString()

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

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Password changed successfully")
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            showToast("Error: ${task.exception?.message}")
                        }
                    }
            } else {
                showToast("No authenticated user found")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
