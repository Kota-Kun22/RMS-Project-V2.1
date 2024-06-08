package com.example.Authnic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rms_project_v2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class signUp_activity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInPageSignUpButton.setOnClickListener {
            val intent = Intent(this, SignIn_activity::class.java)
            startActivity(intent)
        }
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val email = binding.signInMail.text.toString()
            val pass = binding.singUpPassword.text.toString()
            val confirmPass = binding.reTypePassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, SignIn_activity::class.java)
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