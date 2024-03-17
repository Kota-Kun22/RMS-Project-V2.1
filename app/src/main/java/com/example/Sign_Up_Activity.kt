package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.rms_project_v2.R
import com.example.rms_project_v2.databinding.ActivitySignInBinding
import com.example.rms_project_v2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class Sign_Up_Activity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()

        binding.signInButtonOnSignUpPage.setOnClickListener {
            val intent = Intent(this, Sign_In_Activity::class.java)
            startActivity(intent)

        }

        binding.logInButton.setOnClickListener {
            val email= binding.signUpMail.text.toString()
            val pass= binding.typeYourPassForSignUp.text.toString()
            val confirmPass= binding.password.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, Sign_In_Activity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

    }
}