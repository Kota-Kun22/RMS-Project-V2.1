package com.example.Home

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Recharge.RechargeDetails
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

        val allTv: TextView = rootView.findViewById(R.id.allTv)
        val expiredTv: TextView = rootView.findViewById(R.id.expiredTv)
        val esTv: TextView = rootView.findViewById(R.id.esTv)
        allTv.setOnClickListener {
            userList.clear()
            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            loadAllCustomers()
        }
        expiredTv.setOnClickListener {
            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))

            userList.clear()
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()

                    for (childSnapshot in snapshot.children) {
                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                        rechargeDetails?.let {
                            val currentDate = Calendar.getInstance()
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                            val expiry: String = addDaysToDate(rechargeDetails.date!!, validityDays)
                            val formattedCurrentDate = sdf.format(currentDate.time)
                            val check = compareDates(expiry, formattedCurrentDate)
                            if (check <= 0) {
                                userList.add(it)
                                userList.sortBy { rechargeDetails ->
                                    val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                                    val expiry: String = addDaysToDate(rechargeDetails.date!!, validityDays)
                                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val date: Date = format.parse(expiry)
                                    date
                                }
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
        esTv.setOnClickListener {
            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
            userList.clear()
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()

                    for (childSnapshot in snapshot.children) {
                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                        rechargeDetails?.let {

                            val currentDate = Calendar.getInstance()
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedCurrentDate = sdf.format(currentDate.time)
                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                            val expiry: String = addDaysToDate(rechargeDetails.date!!, validityDays)
                            val daysRemaining = differenceDate(expiry, formattedCurrentDate)
                            if (daysRemaining <= 5 && daysRemaining > 0) {
                                userList.add(it)
                                userList.sortBy { rechargeDetails ->
                                    val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                                    val expiry: String = addDaysToDate(rechargeDetails.date!!, validityDays)
                                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val date: Date = format.parse(expiry)
                                    date
                                }
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

        val searchEditText = rootView.findViewById<EditText>(R.id.search_bar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCustomers(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        handleBackPress()

        return rootView
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
    }

    private fun loadAllCustomers() {
        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (childSnapshot in snapshot.children) {
                    val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
                    rechargeDetails?.let {
                        userList.add(it)
                        userList.sortBy { rechargeDetails ->
                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
                            val expiry: String = addDaysToDate(rechargeDetails.date!!, validityDays)
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

    fun differenceDate(dateString1: String, dateString2: String): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = dateFormat.parse(dateString1)!!
        cal2.time = dateFormat.parse(dateString2)!!

        val diffInMillis = cal1.timeInMillis - cal2.timeInMillis

        val daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        return daysDifference.toInt()
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

    fun compareDates(dateString1: String, dateString2: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val date1 = sdf.parse(dateString1)
            val date2 = sdf.parse(dateString2)

            return date1.compareTo(date2)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
}

//package com.example.Home
//
//import android.content.Context
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.TextView
//import androidx.activity.OnBackPressedCallback
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.Recharge.RechargeDetails
//import com.example.rms_project_v2.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Date
//import java.util.Locale
//import java.util.concurrent.TimeUnit
//
//class HomeFragment : Fragment() {
//
//    private lateinit var userRecyclerView: RecyclerView
//    private lateinit var userList: ArrayList<RechargeDetails>
//    private lateinit var adapter: CustomerFragmentAdapter
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var mDbRef: DatabaseReference
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
//        firebaseAuth = FirebaseAuth.getInstance()
//        mDbRef = FirebaseDatabase.getInstance().getReference("Recharge")
//        userList = ArrayList()
//        adapter = CustomerFragmentAdapter(requireContext(), userList)
//        userRecyclerView = rootView.findViewById(R.id.home_rv)
//        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        userRecyclerView.adapter = adapter
//
//        loadAllCustomers()
//
//        val allTv:TextView=rootView.findViewById(R.id.allTv)
//        val expiredTv:TextView=rootView.findViewById(R.id.expiredTv)
//        val esTv:TextView=rootView.findViewById(R.id.esTv)
//        allTv.setOnClickListener {
//            userList.clear()
//            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
//            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//            loadAllCustomers()
//        }
//        expiredTv.setOnClickListener {
//            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
//            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//
//            userList.clear()
//            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    userList.clear()
//
//                    for (childSnapshot in snapshot.children) {
//                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
//                        rechargeDetails?.let {
//                            val currentDate = Calendar.getInstance()
//                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
//                            val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
//                            val formattedCurrentDate = sdf.format(currentDate.time)
//                            val check=compareDates(expiry,formattedCurrentDate)
//                            if(check<=0){
//                                userList.add(it)
//                                userList.sortBy { rechargeDetails ->
//                                    val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
//                                    val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
//                                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                                    val date: Date = format.parse(expiry)
//                                    date
//                                }
//                            }
//                        }
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("FirebaseError", "Database operation cancelled: $error")
//                }
//            })
//
//
//        }
//        esTv.setOnClickListener {
//            allTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//            expiredTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_light))
//            esTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.g_dark))
//            userList.clear()
//            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    userList.clear()
//
//                    for (childSnapshot in snapshot.children) {
//                        val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
//                        rechargeDetails?.let {
//
//                            val currentDate = Calendar.getInstance()
//                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                            val formattedCurrentDate = sdf.format(currentDate.time)
//                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
//                            val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
//                            val daysRemaining=differenceDate(expiry,formattedCurrentDate)
//                            if(daysRemaining<=5 && daysRemaining>0){
//                                userList.add(it)
//                                userList.sortBy { rechargeDetails ->
//                                    val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
//                                    val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
//                                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                                    val date: Date = format.parse(expiry)
//                                    date
//                                }
//                            }
//                        }
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("FirebaseError", "Database operation cancelled: $error")
//                }
//            })
//
//        }
//
//        val searchEditText = rootView.findViewById<EditText>(R.id.search_bar)
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                filterCustomers(s.toString())
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })
//
//        return rootView
//    }
//
//
//    private fun loadAllCustomers() {
//        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
//
//                for (childSnapshot in snapshot.children) {
//                    val rechargeDetails = childSnapshot.getValue(RechargeDetails::class.java)
//                    rechargeDetails?.let {
//                        userList.add(it)
//                        userList.sortBy { rechargeDetails ->
//                            val validityDays = rechargeDetails.validity?.split(" ")?.get(0)?.toIntOrNull() ?: 0
//                            val expiry:String=addDaysToDate(rechargeDetails.date!!, validityDays)
//                            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//                            val date: Date = format.parse(expiry)
//                            date
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseError", "Database operation cancelled: $error")
//            }
//        })
//
//    }
//    fun differenceDate(dateString1: String, dateString2: String): Int {
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
//
//        val cal1 = Calendar.getInstance()
//        val cal2 = Calendar.getInstance()
//        cal1.time = dateFormat.parse(dateString1)!!
//        cal2.time = dateFormat.parse(dateString2)!!
//
//        val diffInMillis = cal1.timeInMillis - cal2.timeInMillis
//
//        val daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis)
//        return daysDifference.toInt()
//    }
//    private fun filterCustomers(query: String) {
//        if (query.isEmpty()) {
//            loadAllCustomers()
//        } else {
//            val filteredList = userList.filter { user ->
//                user.name?.contains(query, ignoreCase = true) ?: false ||
//                        user.phone_no?.contains(query) ?: false ||
//                        user.telecom?.contains(query, ignoreCase = true) ?: false
//            }
//            adapter.setData(filteredList as ArrayList<RechargeDetails>)
//        }
//    }
//    fun addDaysToDate(dateString: String, daysToAdd: Int): String {
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        val calendar = Calendar.getInstance()
//        val date = sdf.parse(dateString)
//        if (date != null) {
//            calendar.time = date
//            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
//            return sdf.format(calendar.time)
//        } else {
//            return ""
//        }
//    }
//    fun compareDates(dateString1: String, dateString2: String): Int {
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//
//        try {
//            val date1 = sdf.parse(dateString1)
//            val date2 = sdf.parse(dateString2)
//
//            return date1.compareTo(date2)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return 0
//        }
//    }
//
//
//
//}
//
