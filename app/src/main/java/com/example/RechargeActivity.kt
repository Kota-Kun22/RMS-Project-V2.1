package com.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.rms_project_v2.R

class RechargeActivity : AppCompatActivity() {
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

    }
}