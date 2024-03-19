package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.rms_project_v2.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addCustomer: ImageView =findViewById(R.id.add_customer)
        addCustomer.setOnClickListener{
            startActivity(Intent(this,AddCustomerPopUp::class.java))
        }
    }
}