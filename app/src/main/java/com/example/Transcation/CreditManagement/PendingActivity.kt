package com.example.Transcation.CreditManagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.R.color
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.example.rms_project_v2.databinding.CreditActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PendingActivity : AppCompatActivity() {

    private lateinit var FdatabaseTransaction: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: CreditActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val jsonString = intent.getStringExtra("currentTransaction")

        val listType = object : TypeToken<Transaction>() {}.type
        val gson = Gson()
        val currentTransaction = gson.fromJson<Transaction>(jsonString, listType)


        val name = currentTransaction.name
        val number = currentTransaction.number
        val pendingAmount = currentTransaction.pending
        val telecom = currentTransaction.telecom
        val date = currentTransaction.date
        val id = currentTransaction.id
        val hofName = currentTransaction.hofName
        val hofNumber = currentTransaction.hofNumber
        val totalAmount = currentTransaction.amount

        binding.name6.text = name
        binding.PhoneNumberTemp6.text = number
        binding.totalCredit.text = "₹$pendingAmount"
        binding.remainingPending.text = "₹$pendingAmount"

        binding.backMark.setOnClickListener {
            finish()
//            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.checkBox.setOnClickListener {
            binding.enteredAmount.setText("$pendingAmount")
        }

        binding.enteredAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                val entered = s.toString().toDoubleOrNull() ?: 0.0
                val remainingAmount = pendingAmount - entered
                if (remainingAmount >= 0) {
                    binding.remainingPending.text = "₹$remainingAmount"
                    binding.remainingPending.setTextColor(ContextCompat.getColor(this@PendingActivity, R.color.remain))
                } else {
                    Toast.makeText(this@PendingActivity, "Entered amount exceeds the credit", Toast.LENGTH_SHORT).show()
                }
            }
        })

        FdatabaseTransaction = FirebaseDatabase.getInstance().getReference("Transactions")
        firebaseAuth = FirebaseAuth.getInstance()

        binding.saveCredit.setOnClickListener {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    startProgressBar()
                }
                val enteredAmountValue = binding.enteredAmount.text.toString().toDouble()
                if (enteredAmountValue <= 0 || enteredAmountValue > pendingAmount || (binding.checkBox.isChecked && enteredAmountValue != pendingAmount)) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@PendingActivity,
                            "Please enter a valid amount or recheck checkbox",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (binding.checkBox.isChecked) {
                        val transactionID = UUID.randomUUID().toString()
                        val transaction = Transaction(
                            transactionID,
                            name!!,
                            date!!,
                            telecom!!,
                            totalAmount.toDouble(),
                            pendingAmount,
                            0.0,
                            number!!,
                            hofName,
                            hofNumber
                        )
                        FdatabaseTransaction.push().setValue(transaction)
                            .addOnSuccessListener {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "Transaction details saved successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.saveCredit.isEnabled = true
                                    binding.saveCredit.text = "Submit"
                                    startActivity(Intent(this@PendingActivity, MainActivity::class.java))
                                }
                            }
                            .addOnFailureListener {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "Failed to save recharge details.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        val toBeSet = totalAmount.toDouble() - enteredAmountValue
                        val transactionPaidId = UUID.randomUUID().toString()
                        val transactionPendingId = UUID.randomUUID().toString()

                        Log.d("t1tst", "Transaction ID: $transactionPaidId") // Debug print

                        val transactionPaid = Transaction(
                            transactionPaidId,
                            name!!,
                            date!!,
                            telecom!!,
                            enteredAmountValue,
                            enteredAmountValue,
                            0.0,
                            number!!,
                            hofName,
                            hofNumber
                        )

                        val transactionPending = Transaction(
                            transactionPendingId,
                            name,
                            date,
                            telecom,
                            toBeSet,
                            0.0,
                            toBeSet,
                            number,
                            hofName,
                            hofNumber
                        )
                        FdatabaseTransaction.push().setValue(transactionPaid)
                            .addOnSuccessListener {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "Transaction details saved successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.saveCredit.isEnabled = true
                                    binding.saveCredit.text = "Submit"
                                    startActivity(Intent(this@PendingActivity, MainActivity::class.java))
                                }
                            }
                            .addOnFailureListener {
                                GlobalScope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@PendingActivity,
                                        "Failed to save recharge details.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        if (toBeSet != 0.0) {
                            FdatabaseTransaction.push().setValue(transactionPending)
                                .addOnSuccessListener {
                                    GlobalScope.launch(Dispatchers.Main) {
//                                        Toast.makeText(
//                                            this@PendingActivity,
//                                            "Transaction details saved successfully.",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
                                        binding.saveCredit.isEnabled = true
                                        binding.saveCredit.text = "Submit"
                                        startActivity(Intent(this@PendingActivity, MainActivity::class.java))
                                    }
                                }
                                .addOnFailureListener {
                                    GlobalScope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@PendingActivity,
                                            "Failed to save recharge details.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
                    deleteTransaction(id)
                }
                stopProgressBar()
            }
        }
    }

    private fun deleteTransaction(transactionId: String) {
        GlobalScope.launch {
            val transactionRef = FdatabaseTransaction.orderByChild("id").equalTo(transactionId)
            transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.removeValue()
                                .addOnSuccessListener {
//                                    Toast.makeText(
//                                        this@PendingActivity,
//                                        "Transaction deleted successfully.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@PendingActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                                .addOnFailureListener {
//                                    Toast.makeText(
//                                        this@PendingActivity,
//                                        "Failed to delete transaction.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                        }
                    } else {
//                        Toast.makeText(
//                            this@PendingActivity,
//                            "Transaction not found.",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PendingActivity, "$error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private suspend fun startProgressBar() {
        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private suspend fun stopProgressBar() {
        withContext(Dispatchers.Main) {
            binding.progressBar.visibility = View.GONE
        }
    }
}
