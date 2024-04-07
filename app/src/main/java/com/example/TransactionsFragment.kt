package com.example

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R
import com.google.android.play.integrity.internal.c
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class TransactionsFragment : Fragment() {


    private lateinit var userRecyclerView: RecyclerView
    private lateinit var transactionList: ArrayList<RechargeDetails>
    private lateinit var adapter: TransactionAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var credit: Long = 0
    private var paid: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_transactioins, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Recharge")
        transactionList = ArrayList()
        adapter = TransactionAdapter(requireContext(),transactionList)
        userRecyclerView = rootView.findViewById(R.id.transaction_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        val date_set:ImageView=rootView.findViewById(R.id.date_set)


        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionList.clear()
                credit = 0
                paid = 0

                for (childSnapshot in snapshot.children) {
                    val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                    rechargeDetails?.let {
                        transactionList.add(it)
                        if (it.payment_status == "Paid") {
                            paid += (it.amount!!.toInt())
                        } else {
                            credit += (it.amount!!.toInt())
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                val creditText: TextView = rootView.findViewById(R.id.credit)
                val paidText: TextView = rootView.findViewById(R.id.paid)
                val totalText: TextView = rootView.findViewById(R.id.total)
                creditText.text = credit.toString()
                paidText.text = paid.toString()
                totalText.text = "₹ "+(paid + credit).toString()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled: $error")
            }
        })

        date_set.setOnClickListener {
            var selectedDate:String=""
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            transactionList.clear()
                            credit = 0
                            paid = 0

                            for (childSnapshot in snapshot.children) {
                                val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                                rechargeDetails?.let {
                                    if(it.date==selectedDate){
                                        transactionList.add(it)
                                        if (it.payment_status == "Paid") {
                                            paid += (it.amount!!.toInt())
                                        } else {
                                            credit += (it.amount!!.toInt())
                                        }
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged()
                            val creditText: TextView = rootView.findViewById(R.id.credit)
                            val paidText: TextView = rootView.findViewById(R.id.paid)
                            val totalText: TextView = rootView.findViewById(R.id.total)
                            creditText.text = credit.toString()
                            paidText.text = paid.toString()
                            totalText.text = "₹ "+(paid + credit).toString()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseError", "Database operation cancelled: $error")
                        }
                    })
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
        val allText:TextView=rootView.findViewById(R.id.allTextView)
        val paidText:TextView=rootView.findViewById(R.id.paidTextView)
        val creditText:TextView=rootView.findViewById(R.id.creditTextView)
        allText.setOnClickListener {
            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))

            transactionList.clear()


            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    transactionList.clear()
                    credit = 0
                    paid = 0

                    for (childSnapshot in snapshot.children) {
                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                        rechargeDetails?.let {
                            transactionList.add(it)
                            if (it.payment_status == "Paid") {
                                paid += (it.amount!!.toInt())
                            } else {
                                credit += (it.amount!!.toInt())
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                    val creditText: TextView = rootView.findViewById(R.id.credit)
                    val paidText: TextView = rootView.findViewById(R.id.paid)
                    val totalText: TextView = rootView.findViewById(R.id.total)
                    creditText.text = credit.toString()
                    paidText.text = paid.toString()
                    totalText.text = "₹ "+(paid + credit).toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Database operation cancelled: $error")
                }
            })

        }


        paidText.setOnClickListener {
            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))

            transactionList.clear()


            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    transactionList.clear()
                    credit = 0
                    paid = 0

                    for (childSnapshot in snapshot.children) {
                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                        rechargeDetails?.let {
                            if(it.payment_status=="Paid"){
                                transactionList.add(it)
                                paid += (it.amount!!.toInt())
                            }
                            credit=0
                        }
                    }
                    adapter.notifyDataSetChanged()
                    val creditText: TextView = rootView.findViewById(R.id.credit)
                    val paidText: TextView = rootView.findViewById(R.id.paid)
                    val totalText: TextView = rootView.findViewById(R.id.total)
                    creditText.text = credit.toString()
                    paidText.text = paid.toString()
                    totalText.text = "₹ "+(paid + credit).toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Database operation cancelled: $error")
                }
            })
        }

        creditText.setOnClickListener {
            allText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            paidText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            creditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))

            transactionList.clear()


            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    transactionList.clear()
                    credit = 0
                    paid = 0

                    for (childSnapshot in snapshot.children) {
                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                        rechargeDetails?.let {
                            if(it.payment_status=="Credit"){
                                transactionList.add(it)
                                credit+= (it.amount!!.toInt())
                            }
                            paid=0
                        }
                    }
                    adapter.notifyDataSetChanged()
                    val creditText: TextView = rootView.findViewById(R.id.credit)
                    val paidText: TextView = rootView.findViewById(R.id.paid)
                    val totalText: TextView = rootView.findViewById(R.id.total)
                    creditText.text = credit.toString()
                    paidText.text = paid.toString()
                    totalText.text = "₹ "+(paid + credit).toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Database operation cancelled: $error")
                }
            })
        }





        return rootView
    }
}