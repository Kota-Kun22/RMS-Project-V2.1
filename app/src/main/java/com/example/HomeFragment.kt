package com.example

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.NewUser
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<RechargeDetails>
    private lateinit var adapter: CustomerFragmentAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Recharge")
        userList = ArrayList()
        adapter = CustomerFragmentAdapter(requireContext(), userList)
        userRecyclerView = rootView.findViewById(R.id.home_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        loadAllCustomers()

        val searchEditText = rootView.findViewById<EditText>(R.id.search_bar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCustomers(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return rootView
    }

    private fun loadAllCustomers() {
        /*mDbRef.child(firebaseAuth.currentUser?.uid ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(NewUser::class.java)
                    currentUser?.let {
                        userList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled: $error")
            }
        })*/


        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (childSnapshot in snapshot.children) {
                    val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                    rechargeDetails?.let {
                        userList.add(it)
                        userList.sortBy { rechargeDetails ->
                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                            val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
                            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val date: Date = format.parse(expiry)
                            date
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled: $error")
            }
        })

    }

    private fun filterCustomers(query: String) {
        if (query.isEmpty()) {
            loadAllCustomers()
        } else {
            val filteredList = userList.filter { user ->
                user.name?.contains(query, ignoreCase = true) ?: false ||
                        user.phone_no?.contains(query) ?: false ||
                        user.telecom?.contains(query, ignoreCase = true) ?: false
            }
            adapter.setData(filteredList as ArrayList<RechargeDetails>)
        }
    }
    fun addDaysToDate(dateString: String, daysToAdd: Int): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf.parse(dateString)
        if (date != null) {
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
            return sdf.format(calendar.time)
        } else {
            return ""
        }
    }
}
