package com.example.Customer.Transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Transcation.TransactionAdapter
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FamilyTrasactionHistory : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var transactionList: ArrayList<Transaction>
    private lateinit var transactionListTemp: ArrayList<Transaction>
    private lateinit var adapter: FamilyTransactionHistoryAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
//    private var pending: Double = 0.0
    private lateinit var deleteHistory: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageView
    private lateinit var dateSetImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_trasaction_history)

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        val intent = intent
        val hofNumber = intent.getStringExtra("hofNumber")
        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Transactions")
        transactionList = ArrayList()
        transactionListTemp = ArrayList()
        adapter = FamilyTransactionHistoryAdapter(this, transactionListTemp)
        userRecyclerView = findViewById(R.id.transaction_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter
        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backButton)

        dateSetImageView = findViewById(R.id.date_set)



        dateSetImageView.setOnClickListener {
            openDatePicker()
        }
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                startProgressBar()
            }
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        transactionList.clear()


                        for (childSnapshot in snapshot.children) {
                            val transaction = childSnapshot.getValue(Transaction::class.java)
                            transaction?.let {
                                transactionList.add(it)

                            }
                        }
                        Log.d("trTest1",transactionList.toString())
//                        Log.d("trTest1HOF",hofNumber.toString())

                        transactionListTemp.addAll(transactionList.filter {
                            Log.d("trTest","${it.hofNumber} == $hofNumber")
                            it.hofNumber == hofNumber })
                        Log.d("trTest1",transactionListTemp.toString())
                        adapter.notifyDataSetChanged()


                        stopProgressBar()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("TransactionsFragment", "Error fetching data", error.toException())
                        Toast.makeText(this@FamilyTrasactionHistory, "Failed to load data", Toast.LENGTH_SHORT).show()
                        stopProgressBar()
                    }
                }
            })
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

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            filterTransactionsByDate(calendar.time)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun filterTransactionsByDate(date: Date) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(date)
        val filteredList = transactionListTemp.filter {
            val transactionDate = dateFormat.parse(it.date)
            transactionDate?.let { transDate -> transDate.before(date) || transDate == date } ?: false
        }
        adapter = FamilyTransactionHistoryAdapter(this, ArrayList(filteredList))
        userRecyclerView.adapter = adapter
    }
}