package com.example.Recharge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.MainActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class RechargeActivity : AppCompatActivity() {
    private lateinit var Fdatabase: DatabaseReference
    private lateinit var FdatabaseTransaction: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge)

        val validitySpinner = findViewById<Spinner>(R.id.spinner_Validity)
        val paymentStatusSpinner = findViewById<Spinner>(R.id.spinner_paymentStatus)
        val validityPlans = arrayOf("Select Validity","28 Days","56 Days","84 Days","180 Days","365 Days")
        val paymentStatus = arrayOf("Payment Status","Paid","Pending")
        val arrayAdap1 =

                ArrayAdapter(this@RechargeActivity,android.R.layout.simple_spinner_dropdown_item,validityPlans)
                validitySpinner.adapter= arrayAdap1
        val arrayAdap2 =

            ArrayAdapter(this@RechargeActivity,android.R.layout.simple_spinner_dropdown_item,paymentStatus)
        paymentStatusSpinner.adapter= arrayAdap2
        val name=intent.getStringExtra("name")
        val number=intent.getStringExtra("number")
        val telecom=intent.getStringExtra("telecom")
        val hof=intent.getStringExtra("hof")
        val hofNumber=intent.getStringExtra("hofNumber")
        progressBar  = findViewById(R.id.progressBar)

        val customerName:TextView=findViewById(R.id.customer_name)
        customerName.text=name
        val customerNumber:TextView=findViewById(R.id.editText)
        customerNumber.text=number
        val customerTelecom:TextView=findViewById(R.id.editText2)
        customerTelecom.text=telecom

        Fdatabase = FirebaseDatabase.getInstance().getReference("Recharge")
        FdatabaseTransaction = FirebaseDatabase.getInstance().getReference("Transactions")
        firebaseAuth = FirebaseAuth.getInstance()

        val submit: Button =findViewById(R.id.submit)
        submit.setOnClickListener {
            // Disable the submit button and show the ProgressBar
            submit.isEnabled = false
            submit.text = "Loading..."
            progressBar.visibility = View.VISIBLE

            val amount: EditText = findViewById(R.id.editText3)
            val rechargeAmount = amount.text.toString().trim()
            val validity = validitySpinner.selectedItem.toString()
            val paymentStatus = paymentStatusSpinner.selectedItem.toString()

            if (validity == "Select Validity" || paymentStatus == "Payment Status" || rechargeAmount.isEmpty()) {
                Toast.makeText(this@RechargeActivity, "Please select validity and payment status, and enter recharge amount.", Toast.LENGTH_SHORT).show()
                // Enable the submit button and hide the ProgressBar
                submit.isEnabled = true
                submit.text = "Submit"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            } else {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH) + 1
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val dateString = "$day/$month/$year"
                    val recharge = RechargeDetails(currentUser.uid, customerName.text.toString(),customerNumber.text.toString(),customerTelecom.text.toString(),amount.text.toString(),paymentStatus?:"",validity?:"",dateString,hof,hofNumber)

                    // Save recharge details
                    Fdatabase.push().setValue(recharge)
                        .addOnSuccessListener {
                            // Toast message for successful recharge details save
                            Toast.makeText(this@RechargeActivity, "Recharge details saved successfully.", Toast.LENGTH_SHORT).show()

                            // Save transaction details
                            var paid = if(paymentStatus=="Paid") amount.text.toString().toDouble() else 0.0;
                            var pending  =  if(paymentStatus=="Pending") amount.text.toString().toDouble() else 0.0;
                            var transaction: Transaction = Transaction(currentUser.uid,customerName.text.toString(),dateString,customerTelecom.text.toString(),amount.text.toString().toDouble(),paid, pending, customerNumber.text.toString(),hofNumber.toString())

                            FdatabaseTransaction.push().setValue(transaction)
                                .addOnSuccessListener {
                                    // Toast message for successful transaction details save
                                    Toast.makeText(this@RechargeActivity, "Transaction details saved successfully.", Toast.LENGTH_SHORT).show()
                                    // Navigate to MainActivity
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                                .addOnFailureListener {
                                    // Toast message for transaction details save failure
                                    Toast.makeText(this@RechargeActivity, "Failed to save transaction details.", Toast.LENGTH_SHORT).show()
                                }

                            // Enable the submit button and hide the ProgressBar
                            submit.isEnabled = true
                            submit.text = "Submit"
                            progressBar.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            // Toast message for recharge details save failure
                            Toast.makeText(this@RechargeActivity, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()

                            // Enable the submit button and hide the ProgressBar
                            submit.isEnabled = true
                            submit.text = "Submit"
                            progressBar.visibility = View.GONE
                        }
                } else {
                    // Toast message for user not authenticated
                    Toast.makeText(this@RechargeActivity, "User not authenticated.", Toast.LENGTH_SHORT).show()

                    // Enable the submit button and hide the ProgressBar
                    submit.isEnabled = true
                    submit.text = "Submit"
                    progressBar.visibility = View.GONE
                }
            }
        }

        val back:ImageView=findViewById(R.id.imageView)
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private suspend fun stopProgressBar() {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE
        }
    }
    private suspend fun startProgressBar() {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
        }
    }
}