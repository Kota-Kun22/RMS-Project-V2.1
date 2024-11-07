package com.example.Authnic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.MainActivity
import com.example.rms_project_v2.R
import com.example.rms_project_v2.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignIn_activity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar=binding.progressBar

        firebaseAuth=FirebaseAuth.getInstance()


        binding.LogINButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val email = binding.signInMail.text.toString()
            val pass = binding.TypePasswordSignIN.editText!!.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
            progressBar.visibility = View.GONE
        }
        val singUp = findViewById<Button>(R.id.sign_in_page_signUp_button)
        singUp.setOnClickListener {
            val intent = Intent(this, signUp_activity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            }
    }
}