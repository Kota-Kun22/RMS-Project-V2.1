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
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingActivity : AppCompatActivity() {

    private lateinit var FdatabaseTransaction: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var checkBox:CheckBox

    private fun deleteTransaction(transactionId: String) {
        val transactionRef = FdatabaseTransaction.orderByChild("id").equalTo(transactionId)
        transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@PendingActivity, "Transaction deleted successfully.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@PendingActivity, MainActivity::class.java))
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@PendingActivity, "Failed to delete transaction.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this@PendingActivity, "Transaction not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PendingActivity, "$error", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.credit_activity)

        val name = intent.getStringExtra("name")
        val number = intent.getStringExtra("number")
        val pendingAmount = intent.getStringExtra("pendingAmount")?.toDouble() ?: 0.0
        val telecom = intent.getStringExtra("telecom")
        val date = intent.getStringExtra("date")
        val id = intent.getStringExtra("id")
        val totalAmount = intent.getDoubleExtra("totalAmount",0.0)
//        val paid = intent.getStringExtra("paid")
        checkBox = findViewById(R.id.checkBox)

        val user_name = findViewById<TextView>(R.id.name6)
        user_name.text = name
        val phone_no = findViewById<TextView>(R.id.Phone_number_temp6)
        phone_no.text = number
        val amountActual = findViewById<TextView>(R.id.total_credit)
        amountActual.text = "₹$pendingAmount"
        val back: ImageView = findViewById(R.id.backMark)
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        val enteredAmount = findViewById<EditText>(R.id.entered_amount)
        val remaining = findViewById<TextView>(R.id.remainingPending)
        remaining.text = "₹$pendingAmount"
        val a1 = pendingAmount

        val markAll: CheckBox = findViewById(R.id.checkBox)
        markAll.setOnClickListener {
            enteredAmount.setText("$pendingAmount")
        }

        enteredAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                val entered = s.toString().toDoubleOrNull() ?: 0.0
                val a3 = a1 - entered
                if (a3 >= 0) {
                    remaining.text = "₹$a3"
                    remaining.setTextColor(ContextCompat.getColor(this@PendingActivity, R.color.remain))
                } else {
                    Toast.makeText(this@PendingActivity, "Entered amount exceeds the credit", Toast.LENGTH_SHORT).show()
                }
            }
        })

        FdatabaseTransaction = FirebaseDatabase.getInstance().getReference("Transactions")
        firebaseAuth = FirebaseAuth.getInstance()

        val submit = findViewById<TextView>(R.id.save_credit)
        submit.setOnClickListener {
            val enteredAmountValue = enteredAmount.text.toString().toDouble()
            if (enteredAmountValue <= 0 || enteredAmountValue > pendingAmount || (checkBox.isChecked && enteredAmountValue!=pendingAmount)) {
                Toast.makeText(this, "Please enter a valid amount or recheck checkbox", Toast.LENGTH_SHORT).show()
            } else {
                if(checkBox.isChecked){
                    var transaction: Transaction = Transaction(id+"german",name!!,date!!,telecom!!,totalAmount!!.toDouble(),pendingAmount, 0.0, number!!)
                    FdatabaseTransaction.push().setValue(transaction)
                        .addOnSuccessListener {
                            Toast.makeText(this@PendingActivity, "transaction details saved successfully.", Toast.LENGTH_SHORT).show()
                            submit.isEnabled = true
                            submit.text = "Submit"
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@PendingActivity, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
                        }

                }
                else{

                    var toBeSet : Double = totalAmount!!.toDouble()-enteredAmountValue
                    var transactionPaid: Transaction = Transaction(id+"german",name!!,date!!,telecom!!,       enteredAmountValue,       enteredAmountValue,         0.0, number!!)
                    var transactionPending: Transaction = Transaction(id+"american",name!!,date!!,telecom!!,  toBeSet,                  0.0,                 toBeSet, number!!)
                    FdatabaseTransaction.push().setValue(transactionPaid)
                        .addOnSuccessListener {
                            Toast.makeText(this@PendingActivity, "transaction details saved successfully.", Toast.LENGTH_SHORT).show()
                            submit.isEnabled = true
                            submit.text = "Submit"
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@PendingActivity, "Failed to save recharge details.", Toast.LENGTH_SHORT).show()
                        }
                        if(toBeSet!=0.0){
                            FdatabaseTransaction.push().setValue(transactionPending)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "transaction details saved successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    submit.isEnabled = true
                                    submit.text = "Submit"
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "Failed to save recharge details.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                }
                deleteTransaction(id!!)













//                val transactionRef = Fdatabase.orderByChild("id").equalTo(number)
//                Fdatabase.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            for (childSnapshot in snapshot.children) {
//                                val transaction = childSnapshot.getValue(Transaction::class.java)
//                                if (transaction != null && transaction.pending == pendingAmount) {
//                                    val updatedPaid = transaction.paid + enteredAmountValue
//                                    val remainingAmount = pendingAmount - enteredAmountValue
//                                    val updatedTransaction = Transaction(
//                                        id = transaction.id,
//                                        name = transaction.name,
//                                        number = transaction.number,
//                                        amount = transaction.amount,
//                                        date = transaction.date,
//                                        paid = updatedPaid,
//                                        pending = remainingAmount,
//                                        telecom = transaction.telecom
//                                    )
//
//                                    childSnapshot.ref.setValue(updatedTransaction)
//                                        .addOnSuccessListener {
//                                            Toast.makeText(this@PendingActivity, "Transaction updated successfully.", Toast.LENGTH_SHORT).show()
//                                            startActivity(Intent(this@PendingActivity, MainActivity::class.java))
//                                        }
//                                        .addOnFailureListener {
//                                            Toast.makeText(this@PendingActivity, "Failed to update transaction.", Toast.LENGTH_SHORT).show()
//                                        }
//                                }
//                            }
//                        } else {
//                            Toast.makeText(this@PendingActivity, "Transaction not found.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Toast.makeText(this@PendingActivity, "$error", Toast.LENGTH_SHORT).show()
//                    }
//                })
            }
        }

    }
}



//
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
//class PendingActivity : AppCompatActivity() {
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
//                    remaining.setTextColor(ContextCompat.getColor(this@PendingActivity, R.color.remain))
//                    remaining.text = "₹$a3"
//                }
//                else {
//                    Toast.makeText(this@PendingActivity, "Entered amount exceeds the credit", Toast.LENGTH_SHORT).show()
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
//                                        Toast.makeText(this@PendingActivity, "Recharge details removed successfully.", Toast.LENGTH_SHORT).show()
//                                    }
//                                    .addOnFailureListener {
//                                        Toast.makeText(this@PendingActivity, "Failed to remove recharge details.", Toast.LENGTH_SHORT).show()
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
//                Fdatabase.push().setValue(t1)
//                    .addOnSuccessListener {
//                        //Toast.makeText(this, "Recharge details updated successfully.", Toast.LENGTH_SHORT).show()
//                        //startActivity(Intent(this,MainActivity::class.java))
//                    }
//                    .addOnFailureListener {
//                        //Toast.makeText(this, "Failed to update recharge details.", Toast.LENGTH_SHORT).show()
//                    }
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
