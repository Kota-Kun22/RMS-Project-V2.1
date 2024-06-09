
package com.example.Transcation

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MainActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.util.Calendar

class TransactionsFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var transactionList: ArrayList<Transaction>
    private lateinit var adapter: TransactionAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var pending: Double = 0.0
    private lateinit var deleteHistory: ImageView
    private var paid: Double = 0.0

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

        val date_set: ImageView = rootView.findViewById(R.id.date_set)

        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TransactionsFragment", "Error fetching data", error.toException())
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })

        date_set.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                filterTransactionsByDate(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }
        deleteHistory.setOnClickListener {
            deleteAllTransactions()
        }

        return rootView
    }

    private fun filterTransactionsByDate(selectedDate: String) {
        val filteredList = transactionList.filter { it.date == selectedDate }
        adapter = TransactionAdapter(requireContext(), ArrayList(filteredList))
        userRecyclerView.adapter = adapter
    }
    private fun deleteAllTransactions() {
        val td = FirebaseDatabase.getInstance().getReference("Transactions")
        td.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
//                                Toast.makeText(requireContext(), "Transaction deleted successfully.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
//                                Toast.makeText(requireContext(), "Failed to delete transaction.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    Toast.makeText(requireContext(), "All transactions deleted successfully.", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "No transactions found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

//
//package com.example.Transcation
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.Transcationpackage.TransactionAdapter
//import com.example.entities.Transaction
//import com.example.rms_project_v2.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//import java.util.Calendar
//
//class TransactionsFragment : Fragment() {
//
//    private lateinit var userRecyclerView: RecyclerView
//    private lateinit var transactionList: ArrayList<Transaction>
//    private lateinit var adapter: TransactionAdapter
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var mDbRef: DatabaseReference
//    private var pending: Long = 0
//    private var paid: Long = 0
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_transactioins, container, false)
//
//        firebaseAuth = FirebaseAuth.getInstance()
//        mDbRef = FirebaseDatabase.getInstance().getReference("Transactions")
//        transactionList = ArrayList()
//        adapter = TransactionAdapter(requireContext(), transactionList)
//        userRecyclerView = rootView.findViewById(R.id.transaction_rv)
//        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        userRecyclerView.adapter = adapter
//
//        val date_set: ImageView = rootView.findViewById(R.id.date_set)
//
//        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                transactionList.clear()
//                pending = 0
//                paid = 0
//
//                for (childSnapshot in snapshot.children) {
//                    val transaction = childSnapshot.getValue(Transaction::class.java)
//                    transaction?.let {
//                        transactionList.add(it)
//                        if (it.pending > 0) {
//                            pending += it.pending.toLong()
//                        } else {
//                            paid += it.paid.toLong()
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged()
//                val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
//                val paidText: TextView = rootView.findViewById(R.id.paidAmount)
//                val totalText: TextView = rootView.findViewById(R.id.total)
//                creditText.text = pending.toString()
//                paidText.text = paid.toString()
//                totalText.text = "₹ " + (paid + pending).toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseError", "Database operation cancelled: $error")
//            }
//        })
//
//        val delete: ImageView = rootView.findViewById(R.id.delete_history)
//        delete.setOnClickListener {
//            val firebaseDatabase = FirebaseDatabase.getInstance()
//            val rootReference = firebaseDatabase.reference
//            val transactionsReference = rootReference.child("Transactions")
//            transactionsReference.removeValue()
//                .addOnSuccessListener {
//                    Toast.makeText(requireContext(), "Transaction history deleted", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(requireContext(), "Failed to delete transaction history: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            transactionList.clear()
//            val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
//            val paidText: TextView = rootView.findViewById(R.id.paidAmount)
//            val totalText: TextView = rootView.findViewById(R.id.total)
//            creditText.text = "0"
//            paidText.text = "0"
//            totalText.text = "₹ 0"
//            adapter.notifyDataSetChanged()
//        }
//
//        date_set.setOnClickListener {
//            var selectedDate: String = ""
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//            val datePickerDialog = DatePickerDialog(
//                requireContext(),
//                { _, selectedYear, selectedMonth, selectedDay ->
//                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                    mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            transactionList.clear()
//                            pending = 0
//                            paid = 0
//
//                            for (childSnapshot in snapshot.children) {
//                                val transaction = childSnapshot.getValue(Transaction::class.java)
//                                transaction?.let {
//                                    if (it.date == selectedDate) {
//                                        transactionList.add(it)
//                                        if (it.pending > 0) {
//                                            pending += it.pending.toLong()
//                                        } else {
//                                            paid += it.paid.toLong()
//                                        }
//                                    }
//                                }
//                            }
//                            adapter.notifyDataSetChanged()
//                            val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
//                            val paidText: TextView = rootView.findViewById(R.id.paidAmount)
//                            val totalText: TextView = rootView.findViewById(R.id.total)
//                            creditText.text = pending.toString()
//                            paidText.text = paid.toString()
//                            totalText.text = "₹ " + (paid + pending).toString()
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.e("FirebaseError", "Database operation cancelled: $error")
//                        }
//                    })
//                },
//                year, month, dayOfMonth
//            )
//            datePickerDialog.show()
//        }
//
//        return rootView
//    }
//}
//
//
////package com.example.Transcation
////
////import android.app.DatePickerDialog
////import android.os.Bundle
////import android.util.Log
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.ImageView
////import android.widget.TextView
////import android.widget.Toast
////import androidx.core.content.ContextCompat
////import androidx.fragment.app.Fragment
////import androidx.recyclerview.widget.LinearLayoutManager
////import androidx.recyclerview.widget.RecyclerView
////import com.example.Recharge.RechargeDetails
////import com.example.entities.Transaction
////import com.example.rms_project_v2.R
////import com.google.firebase.auth.FirebaseAuth
////import com.google.firebase.database.DataSnapshot
////import com.google.firebase.database.DatabaseError
////import com.google.firebase.database.DatabaseReference
////import com.google.firebase.database.FirebaseDatabase
////import com.google.firebase.database.ValueEventListener
////import java.util.Calendar
////
////
////
////
////
////
////
////
////
////
////
////
////class TransactionsFragment : Fragment() {
////
////
////    private lateinit var userRecyclerView: RecyclerView
////    private lateinit var transactionList: ArrayList<RechargeDetails>
////    private lateinit var adapter: TransactionAdapter
////    private lateinit var firebaseAuth: FirebaseAuth
////    private lateinit var mDbRef: DatabaseReference
////    private var pending: Long = 0
////    private var paid: Long = 0
////
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View? {
////        val rootView = inflater.inflate(R.layout.fragment_transactioins, container, false)
////
////        firebaseAuth = FirebaseAuth.getInstance()
////        mDbRef = FirebaseDatabase.getInstance().getReference("Recharge")
////        transactionList = ArrayList()
////        adapter = TransactionAdapter(requireContext(),transactionList)
////        userRecyclerView = rootView.findViewById(R.id.transaction_rv)
////        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
////        userRecyclerView.adapter = adapter
////
////        val date_set:ImageView=rootView.findViewById(R.id.date_set)
////
////
////        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////            override fun onDataChange(snapshot: DataSnapshot) {
////                transactionList.clear()
////                pending = 0
////                paid = 0
////
////                for (childSnapshot in snapshot.children) {
////                    val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
////                    rechargeDetails?.let {
////                        transactionList.add(it)
////                        if (it.payment_status == "Paid") {
////                            paid += (it.amount!!.toInt())
////                        } else {
////                            pending += (it.amount!!.toInt())
////                        }
////                    }
////                }
////                adapter.notifyDataSetChanged()
////                val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////                val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////                val totalText: TextView = rootView.findViewById(R.id.total)
////                creditText.text = pending.toString()
////                paidText.text = paid.toString()
////                totalText.text = "₹ "+(paid + pending).toString()
////            }
////            override fun onCancelled(error: DatabaseError) {
////                Log.e("FirebaseError", "Database operation cancelled: $error")
////            }
////        })
////
////        val delete:ImageView=rootView.findViewById(R.id.delete_history)
////        delete.setOnClickListener {
////            val firebaseDatabase = FirebaseDatabase.getInstance()
////            val rootReference = firebaseDatabase.reference
////            val rechargeReference = rootReference.child("Recharge")
////            rechargeReference.removeValue()
////                .addOnSuccessListener {
////                    Toast.makeText(requireContext(), "Transaction history deleted", Toast.LENGTH_SHORT).show()
////                }
////                .addOnFailureListener { e ->
////                    Toast.makeText(requireContext(), "Failed to delete transaction history: ${e.message}", Toast.LENGTH_SHORT).show()
////                }
////            transactionList.clear()
////            val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////            val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////            val totalText: TextView = rootView.findViewById(R.id.total)
////            creditText.text = "0"
////            paidText.text = "0"
////            totalText.text = "₹ 0"
////            adapter.notifyDataSetChanged()
////        }
////
////
////
////        date_set.setOnClickListener {
////            var selectedDate:String=""
////            val calendar = Calendar.getInstance()
////            val year = calendar.get(Calendar.YEAR)
////            val month = calendar.get(Calendar.MONTH)
////            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
////            val datePickerDialog = DatePickerDialog(
////                requireContext(),
////                { _, selectedYear, selectedMonth, selectedDay ->
////                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
////                    mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////                        override fun onDataChange(snapshot: DataSnapshot) {
////                            transactionList.clear()
////                            pending = 0
////                            paid = 0
////
////                            for (childSnapshot in snapshot.children) {
////                                val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
////                                rechargeDetails?.let {
////                                    if(it.date==selectedDate){
////                                        transactionList.add(it)
////                                        if (it.payment_status == "Paid") {
////                                            paid += (it.amount!!.toInt())
////                                        } else {
////                                            pending += (it.amount!!.toInt())
////                                        }
////                                    }
////                                }
////                            }
////                            adapter.notifyDataSetChanged()
////                            val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////                            val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////                            val totalText: TextView = rootView.findViewById(R.id.total)
////                            creditText.text = pending.toString()
////                            paidText.text = paid.toString()
////                            totalText.text = "₹ "+(paid + pending).toString()
////                        }
////                        override fun onCancelled(error: DatabaseError) {
////                            Log.e("FirebaseError", "Database operation cancelled: $error")
////                        }
////                    })
////                },
////                year,
////                month,
////                dayOfMonth
////            )
////            datePickerDialog.show()
////        }
////        val allText:TextView=rootView.findViewById(R.id.allTextView)
////        val paidText:TextView=rootView.findViewById(R.id.paidTextView)
////        val creditText:TextView=rootView.findViewById(R.id.pendingTextView)
////        allText.setOnClickListener {
////            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
////            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////
////            transactionList.clear()
////
////
////            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    transactionList.clear()
////                    pending = 0
////                    paid = 0
////
////                    for (childSnapshot in snapshot.children) {
////                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
////                        rechargeDetails?.let {
////                            transactionList.add(it)
////                            if (it.payment_status == "Paid") {
////                                paid += (it.amount!!.toInt())
////                            } else {
////                                pending += (it.amount!!.toInt())
////                            }
////                        }
////                    }
////                    adapter.notifyDataSetChanged()
////                    val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////                    val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////                    val totalText: TextView = rootView.findViewById(R.id.total)
////                    creditText.text = pending.toString()
////                    paidText.text = paid.toString()
////                    totalText.text = "₹ "+(paid + pending).toString()
////                }
////                override fun onCancelled(error: DatabaseError) {
////                    Log.e("FirebaseError", "Database operation cancelled: $error")
////                }
////            })
////
////        }
////
////
////        paidText.setOnClickListener {
////            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
////            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////
////            transactionList.clear()
////
////
////            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    transactionList.clear()
////                    pending = 0
////                    paid = 0
////
////                    for (childSnapshot in snapshot.children) {
////                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
////                        rechargeDetails?.let {
////                            if(it.payment_status=="Paid"){
////                                transactionList.add(it)
////                                paid += (it.amount!!.toInt())
////                            }
////                            else{
////                                pending+=it.amount!!.toInt()
////                            }
////
////                        }
////                    }
////                    adapter.notifyDataSetChanged()
////                    val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////                    val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////                    val totalText: TextView = rootView.findViewById(R.id.total)
////                    creditText.text = pending.toString()
////                    paidText.text = paid.toString()
////                    totalText.text = "₹ "+(paid + pending).toString()
////                }
////                override fun onCancelled(error: DatabaseError) {
////                    Log.e("FirebaseError", "Database operation cancelled: $error")
////                }
////            })
////        }
////
////        creditText.setOnClickListener {
////            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
////            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
////
////            transactionList.clear()
////
////
////            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////                override fun onDataChange(snapshot: DataSnapshot) {
////                    transactionList.clear()
////                    pending = 0
////                    paid = 0
////
////                    for (childSnapshot in snapshot.children) {
////                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
////                        rechargeDetails?.let {
////                            if(it.payment_status=="Credit"){
////                                transactionList.add(it)
////                                pending+= (it.amount!!.toInt())
////                            }
////                            else{
////                                paid+=(it.amount!!.toInt())
////                            }
////
////                        }
////                    }
////                    adapter.notifyDataSetChanged()
////                    val creditText: TextView = rootView.findViewById(R.id.pendingAmount)
////                    val paidText: TextView = rootView.findViewById(R.id.paidAmount)
////                    val totalText: TextView = rootView.findViewById(R.id.total)
////                    creditText.text = pending.toString()
////                    paidText.text = paid.toString()
////                    totalText.text = "₹ "+(paid + pending).toString()
////                }
////                override fun onCancelled(error: DatabaseError) {
////                    Log.e("FirebaseError", "Database operation cancelled: $error")
////                }
////            })
////        }
////
////
////
////
////
////        return rootView
////    }
////}