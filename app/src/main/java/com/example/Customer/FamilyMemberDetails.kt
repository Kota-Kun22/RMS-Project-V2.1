package com.example.Customer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Customer.Transactions.FamilyTrasactionHistory
import com.example.entities.Member
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyMemberDetails:AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FamilyMemberDetailRecyclerViewAdapter
    private lateinit var hofName:TextView
    private lateinit var hofNumber:TextView
    private lateinit var messageButton:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_member_details)
        val count = intent.getIntExtra("count", 0)
//        val members = intent.getSerializableExtra("members") as? List<Member>
        val jsonString = intent.getStringExtra("memberListJson")
        val hofName = intent.getStringExtra("HOF_Name")
        val hofNumber = intent.getStringExtra("HOF_Number")
        findViewById<TextView>(R.id.HOFName).text = hofName
        findViewById<TextView>(R.id.HOFNumber).text = hofNumber
        findViewById<ImageView>(R.id.messageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val number = "+91" + hofNumber
            val url = "https://api.whatsapp.com/send?phone=$number"
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.family_transaction_history).setOnClickListener {
            val intent  = Intent(this@FamilyMemberDetails,FamilyTrasactionHistory::class.java)
            intent.putExtra("hofNumber",hofNumber.toString())
            startActivity(intent)
        }
        showTotalPending(hofNumber)

        // Convert the JSON string back to a list of Member objects using Gson
        val listType = object : TypeToken<List<Member>>() {}.type
        val gson = Gson()
        val members = gson.fromJson<List<Member>>(jsonString, listType)




        Log.d("Testpreet","In FamilyMemberDetails")
        Log.d("Testpreet",members.toString())
        recyclerView = findViewById(R.id.familyMember_rv)
        adapter = FamilyMemberDetailRecyclerViewAdapter(this,members!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showTotalPending(hofNumber: String?) {
        val transactionList = mutableListOf<Transaction>()
        val transactionListTemp = mutableListOf<Transaction>()
        val mDbRef = FirebaseDatabase.getInstance().getReference("Transactions")
        val totalPending = findViewById<TextView>(R.id.totalPending)
        totalPending.text = "Loading Total Pending..."
        GlobalScope.launch {
            withContext(Dispatchers.Main) {

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
//                        Log.d("trTest1",transactionList.toString())


                        transactionListTemp.addAll(transactionList.filter {
                            Log.d("trTest","${it.hofNumber} == $hofNumber")
                            it.hofNumber == hofNumber && it.pending>0})
                        val totalPendingAmount = transactionListTemp.sumOf { it.pending }
                        totalPending.text = "Total Pending: ₹$totalPendingAmount"
//                        Log.d("trTest1",transactionListTemp.toString())


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("TransactionsFragment", "Error fetching data", error.toException())
                        Toast.makeText(this@FamilyMemberDetails, "Failed to load data", Toast.LENGTH_SHORT).show()

                    }
                }
            })
        }
    }
}