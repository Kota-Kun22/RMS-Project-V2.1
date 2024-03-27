package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RechargeActivity : AppCompatActivity() {
    private lateinit var Fdatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge)

        val validitySpinner = findViewById<Spinner>(R.id.spinner_Validity)
        val paymentStatusSpinner = findViewById<Spinner>(R.id.spinner_paymentStatus)
        val validityPlans = arrayOf("Select Validity","28 Days","56 Days","84 Days","180 Days","365 Days")
        val paymentStatus = arrayOf("Payment Status","Paid","Credit")
        val arrayAdap1 =

                ArrayAdapter(this@RechargeActivity,android.R.layout.simple_spinner_dropdown_item,validityPlans)
                validitySpinner.adapter= arrayAdap1
        val arrayAdap2 =

            ArrayAdapter(this@RechargeActivity,android.R.layout.simple_spinner_dropdown_item,paymentStatus)
        paymentStatusSpinner.adapter= arrayAdap2
        val name=intent.getStringExtra("name")
        val number=intent.getStringExtra("number")
        val telecom=intent.getStringExtra("telecom")

        val customerName:TextView=findViewById(R.id.customer_name)
        customerName.text=name
        val customerNumber:TextView=findViewById(R.id.editText)
        customerNumber.text=number
        val customerTelecom:TextView=findViewById(R.id.editText2)
        customerTelecom.text=telecom

        Fdatabase = FirebaseDatabase.getInstance().getReference("Recharge")
        firebaseAuth = FirebaseAuth.getInstance()

        val submit: Button =findViewById(R.id.submit)
        submit.setOnClickListener {
            val amount: EditText = findViewById(R.id.editText3)
            val rechargeAmount = amount.text.toString().trim()
            val validity = validitySpinner.selectedItem.toString()
            val paymentStatus = paymentStatusSpinner.selectedItem.toString()


            if (validity == "Select Validity" || paymentStatus == "Payment Status" || rechargeAmount.isEmpty()) {
                Toast.makeText(this@RechargeActivity, "Please select validity and payment status, and enter recharge amount.", Toast.LENGTH_SHORT).show()
            }
            else {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val recharge = RechargeDetails(currentUser.uid, customerName.text.toString(),customerNumber.text.toString(),customerTelecom.text.toString(),amount.text.toString(),paymentStatus?:"",validity?:"")
                    Fdatabase.push().setValue(recharge)
                        .addOnSuccessListener {
                            Toast.makeText(this@RechargeActivity, "Recharge details saved successfully.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@RechargeActivity, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this@RechargeActivity, "User not authenticated.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val back:ImageView=findViewById(R.id.imageView)
        back.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}