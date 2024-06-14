
package com.example.Transcation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MainActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionsFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var transactionList: ArrayList<Transaction>
    private lateinit var adapter: TransactionAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var pending: Double = 0.0
    private lateinit var deleteHistory: ImageView
    private var paid: Double = 0.0
    private lateinit var progressBar: ProgressBar
    private lateinit var allTextView: TextView
    private lateinit var paidTextView: TextView
    private lateinit var pendingTextView: TextView
    private lateinit var totalAmount: TextView

    private lateinit var dateSetImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_transactioins, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Transactions")
        transactionList = ArrayList()
        adapter = TransactionAdapter(requireContext(), transactionList)
        userRecyclerView = rootView.findViewById(R.id.transaction_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter
        deleteHistory = rootView.findViewById(R.id.delete_history)
        progressBar = rootView.findViewById(R.id.progressBar)
        allTextView = rootView.findViewById<TextView>(R.id.allTransactions)
        paidTextView = rootView.findViewById<TextView>(R.id.paidTransactions)
        pendingTextView = rootView.findViewById<TextView>(R.id.pendingTransactions)
        totalAmount = rootView.findViewById<TextView>(R.id.total)
        dateSetImageView = rootView.findViewById(R.id.date_set)

        allTextView.setOnClickListener {
            filterTransactions("All")
        }

        paidTextView.setOnClickListener {
            filterTransactions("Paid")
        }

        pendingTextView.setOnClickListener {
            filterTransactions("Pending")
        }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                startProgressBar()
            }
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        transactionList.clear()
                        pending = 0.0
                        paid = 0.0

                        for (childSnapshot in snapshot.children) {
                            val transaction = childSnapshot.getValue(Transaction::class.java)
                            transaction?.let {
                                transactionList.add(it)
                                pending += it.pending
                                paid += it.paid
                            }
                        }
                        adapter.notifyDataSetChanged()

                        val paidCredit = rootView.findViewById<TextView>(R.id.paidAmount)
                        val pendingCredit = rootView.findViewById<TextView>(R.id.pendingAmount)
                        paidCredit.text = "₹$paid"
                        pendingCredit.text = "₹$pending"
                        totalAmount.text = "₹ $paid"
                        stopProgressBar()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("TransactionsFragment", "Error fetching data", error.toException())
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                        stopProgressBar()
                    }
                }
            })
        }
        dateSetImageView.setOnClickListener {
            openDatePicker()
        }




        deleteHistory.setOnClickListener {
            deleteAllTransactions()
        }

        return rootView
    }




    private fun deleteAllTransactions() {
        // Show the confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All Transactions")
            .setMessage("Are you sure you want to delete all transactions?")
            .setPositiveButton("Yes") { _, _ ->
                // If the user confirms, show the progress dialog and start the deletion process
                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null)
                val progressDialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setCancelable(false)
                    .create()

                progressDialog.show()

                GlobalScope.launch {
                    val td = FirebaseDatabase.getInstance().getReference("Transactions")
                    td.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            GlobalScope.launch(Dispatchers.Main) {
                                if (snapshot.exists()) {
                                    for (childSnapshot in snapshot.children) {
                                        childSnapshot.ref.removeValue()
                                            .addOnSuccessListener {
                                                // Update UI if needed
                                            }
                                            .addOnFailureListener {
                                                // Update UI if needed
                                            }
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "All transactions deleted successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    transactionList.clear()
                                    adapter.notifyDataSetChanged()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "No transactions found.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                progressDialog.dismiss()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            GlobalScope.launch(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            }
                        }
                    })
                }
            }
            .setNegativeButton("No", null)
            .show()
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
    private fun filterTransactions(type: String) {
        val filteredList = when (type) {
            "All" -> {

                allTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
                paidTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                pendingTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                transactionList
            }
            "Paid" -> {
                allTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                paidTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
                pendingTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                transactionList.filter { it.paid > 0 }
            }
            "Pending" -> {
                allTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                paidTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
                pendingTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
                transactionList.filter { it.pending > 0 }
            }
            else -> ArrayList()
        }
        adapter = TransactionAdapter(requireContext(), ArrayList(filteredList))
        userRecyclerView.adapter = adapter
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            filterTransactionsByDate(calendar.time)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun filterTransactionsByDate(date: Date) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(date)
        val filteredList = transactionList.filter {
            val transactionDate = dateFormat.parse(it.date)
            transactionDate?.let { transDate -> transDate.before(date) || transDate == date } ?: false
        }
        adapter = TransactionAdapter(requireContext(), ArrayList(filteredList))
        userRecyclerView.adapter = adapter
    }
}

