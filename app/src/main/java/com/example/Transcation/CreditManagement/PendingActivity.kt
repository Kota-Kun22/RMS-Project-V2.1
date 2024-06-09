package com.example.Transcation.CreditManagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.Recharge.RechargeDetails
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingActivity : AppCompatActivity() {

    private lateinit var Fdatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.credit_activity)
        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("number")
        val amount = intent.getStringExtra("amount")
        val telecom = intent.getStringExtra("telecom")
        val validity = intent.getStringExtra("validity")
        val date = intent.getStringExtra("date")
        val hof = intent.getStringExtra("hof")
        val hofNumber = intent.getStringExtra("hofNumber")
        val user_name = findViewById<TextView>(R.id.name6)
        user_name.text = name
        val phone_no = findViewById<TextView>(R.id.Phone_number_temp6)
        phone_no.text = number
        val amountActual = findViewById<TextView>(R.id.total_credit)
        amountActual.text = "₹$amount"
        val back: ImageView = findViewById(R.id.backMark)
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        val enteredAmount = findViewById<EditText>(R.id.entered_amount)
        val remaining = findViewById<TextView>(R.id.remaining_credit)
        remaining.text = "₹" + amount
        val a1 = amount?.toIntOrNull() ?: 0
        val markAll: CheckBox = findViewById(R.id.checkBox)
        markAll.setOnClickListener {
            enteredAmount.setText("$amount")
        }

        // Add a TextWatcher to capture changes in the entered amount
        enteredAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                val entered = s.toString().toIntOrNull() ?: 0
                val a3 = a1 - entered
                if (a3 > 0) {
                    remaining.text = "₹$a3"
                } else if (a3 == 0) {
                    remaining.setTextColor(ContextCompat.getColor(this@PendingActivity, R.color.remain))
                    remaining.text = "₹$a3"
                } else {
                    Toast.makeText(this@PendingActivity, "Entered amount exceeds the credit", Toast.LENGTH_SHORT).show()
                }
            }
        })

        Fdatabase = FirebaseDatabase.getInstance().getReference("Recharge")
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        val submit = findViewById<TextView>(R.id.save_credit)
        submit.setOnClickListener {
            val enteredAmountValue = enteredAmount.text.toString().toIntOrNull() ?: 0
            if (enteredAmountValue <= 0 || enteredAmountValue > amount!!.toInt()) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            } else {
                val toRemove = RechargeDetails(currentUser?.uid, name, number, telecom, amount, "Credit", validity, date, hof, hofNumber)
                val toRemoveRef = Fdatabase.orderByChild("name").equalTo(toRemove.name)
                toRemoveRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                            if (rechargeDetails != null && rechargeDetails.matches(toRemove)) {
                                childSnapshot.ref.removeValue()
                                    .addOnSuccessListener {
                                        Toast.makeText(this@PendingActivity, "Recharge details removed successfully.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@PendingActivity, "Failed to remove recharge details.", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
                val t1 = RechargeDetails(currentUser?.uid, name, number, telecom, enteredAmountValue.toString(), "Paid", validity, date, hof, hofNumber)
                val remainingAmount = a1 - enteredAmountValue
                if (remainingAmount > 0) {
                    val t2 = RechargeDetails(currentUser?.uid, name, number, telecom, remainingAmount.toString(), "Pending", validity, date, hof, hofNumber)
                    Fdatabase.push().setValue(t2)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Recharge details saved successfully.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Fdatabase.push().setValue(t1)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Recharge details saved successfully.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}















//package com.example.Transcation.CreditManagement
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.CheckBox
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.example.MainActivity
//import com.example.Recharge.RechargeDetails
//import com.example.rms_project_v2.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class CreditActivity : AppCompatActivity() {
//
//    private lateinit var Fdatabase: DatabaseReference
//    private lateinit var firebaseAuth: FirebaseAuth
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.credit_activity)
//        val name = intent.getStringExtra("name")
//        val number = intent.getStringExtra("number")
//        val amount = intent.getStringExtra("amount")
//        val telecom = intent.getStringExtra("telecom")
//        val validity = intent.getStringExtra("validity")
//        val date = intent.getStringExtra("date")
//        val hof = intent.getStringExtra("hof")
//        val hofNumber = intent.getStringExtra("hofNumber")
//        val user_name = findViewById<TextView>(R.id.name6)
//        user_name.text = name
//        val phone_no = findViewById<TextView>(R.id.Phone_number_temp6)
//        phone_no.text = number
//        val amountActual = findViewById<TextView>(R.id.total_credit)
//        amountActual.text = "₹$amount"
//        val back:ImageView=findViewById(R.id.backMark)
//        back.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//        val enteredAmount = findViewById<EditText>(R.id.entered_amount)
//        val remaining = findViewById<TextView>(R.id.remaining_credit)
//        remaining.text="₹"+amount
//        val a1 = amount?.toIntOrNull() ?: 0
//        val markAll:CheckBox=findViewById(R.id.checkBox)
//        markAll.setOnClickListener {
//            enteredAmount.setText("$amount")
//        }
//
//        // Add a TextWatcher to capture changes in the entered amount
//        enteredAmount.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // Not needed
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Not needed
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                val entered = s.toString().toIntOrNull() ?: 0
//                val a3 = a1 - entered
//                if (a3 > 0) {
//                    remaining.text = "₹$a3"
//                }
//                else if (a3==0){
//                    remaining.setTextColor(ContextCompat.getColor(this@CreditActivity, R.color.remain))
//                    remaining.text = "₹$a3"
//                }
//                else {
//                    Toast.makeText(this@CreditActivity, "Entered amount exceeds the credit", Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//
//        Fdatabase = FirebaseDatabase.getInstance().getReference("Recharge")
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        val currentUser = firebaseAuth.currentUser
//
//        val submit=findViewById<TextView>(R.id.save_credit)
//        submit.setOnClickListener {
//            val enteredAmountValue = enteredAmount.text.toString().toIntOrNull() ?: 0
//            if (enteredAmountValue <= 0 || enteredAmountValue>amount!!.toInt()) {
//                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
//            } else {
//                val toRemove= RechargeDetails(currentUser?.uid,name,number,telecom,amount,"Credit",validity, date,hof,hofNumber)
//                val toRemoveRef = Fdatabase.orderByChild("name").equalTo(toRemove.name)
//                toRemoveRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (childSnapshot in snapshot.children) {
//                            val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
//                            if (rechargeDetails != null && rechargeDetails.matches(toRemove)) {
//                                childSnapshot.ref.removeValue()
//                                    .addOnSuccessListener {
//                                        Toast.makeText(this@CreditActivity, "Recharge details removed successfully.", Toast.LENGTH_SHORT).show()
//                                    }
//                                    .addOnFailureListener {
//                                        Toast.makeText(this@CreditActivity, "Failed to remove recharge details.", Toast.LENGTH_SHORT).show()
//                                    }
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error
//                    }
//                })
//                val t1 = RechargeDetails(currentUser?.uid, name, number, telecom, enteredAmountValue.toString(), "Paid", validity, date,hof,hofNumber)
//                val remainingAmount = a1 - enteredAmountValue
//                val t2 = RechargeDetails(currentUser?.uid, name, number, telecom, remainingAmount.toString(), "Pending", validity, date,hof,hofNumber)
////                Fdatabase.push().setValue(t1)
////                    .addOnSuccessListener {
////                        //Toast.makeText(this, "Recharge details updated successfully.", Toast.LENGTH_SHORT).show()
////                        //startActivity(Intent(this,MainActivity::class.java))
////                    }
////                    .addOnFailureListener {
////                        //Toast.makeText(this, "Failed to update recharge details.", Toast.LENGTH_SHORT).show()
////                    }
//                Fdatabase.push().setValue(t2)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Recharge details saved successfully.", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, MainActivity::class.java))
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
//                    }
//            }
//        }
//    }
//}
