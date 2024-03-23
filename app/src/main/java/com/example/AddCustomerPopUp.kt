package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddCustomerPopUp: AppCompatActivity() {

    private lateinit var Fdatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_recyclerciew)

        Fdatabase = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()

        val saveDetails: TextView = findViewById(R.id.saveDetails)
        saveDetails.setOnClickListener {
            val name: EditText = findViewById(R.id.user_name1)
            val dob: EditText = findViewById(R.id.date_of_birth1)
            val number: EditText = findViewById(R.id.mobile_number1)
            val telecom: EditText = findViewById(R.id.telecom_type1)
            val email: EditText = findViewById(R.id.emailAddress1)

            val userName = name.text.toString().trim()
            val userDob = dob.text.toString().trim()
            val userNumber = number.text.toString().trim()
            val userTelecom = telecom.text.toString().trim()
            val userEmail = email.text.toString().trim()

            // Check if current user is not null before proceeding
            firebaseAuth.currentUser?.let { currentUser ->
                val user = NewUser(currentUser.uid, userName, userDob, userNumber, userTelecom, userEmail)

                Fdatabase.child(currentUser.uid).push().setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
            }

            // Handle back button click
            val back: ImageView = findViewById(R.id.back_icon)
            back.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}

