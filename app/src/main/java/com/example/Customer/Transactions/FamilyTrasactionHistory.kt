package com.example.Customer.Transactions

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_family_trasaction_history)
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
}