package com.example.Customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.NewCustomer
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

class CustomersFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<NewCustomer>
    private lateinit var adapter: CustomerAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var count: Int = 0
    private lateinit var hofTypeSelector: TextView
    private lateinit var individualTypeSelector: TextView
    private lateinit var listSize: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_customers, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        userList = ArrayList()
        adapter = CustomerAdapter(requireContext(), userList)
        userRecyclerView = rootView.findViewById(R.id.customer_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter
        individualTypeSelector = rootView.findViewById(R.id.individualTypeSelector)
        hofTypeSelector = rootView.findViewById(R.id.hofTypeSelector)
        listSize = rootView.findViewById(R.id.listSize)
        progressBar = rootView.findViewById(R.id.progressBar)

        val blueColor = ContextCompat.getColor(requireContext(), R.color.typeSelectorColor)
        val blackColor = ContextCompat.getColor(requireContext(), R.color.black)

        // Set default selection to HOF and load HOF users
        hofTypeSelector.setTextColor(blueColor)
        individualTypeSelector.setTextColor(blackColor)
        GlobalScope.launch {
            loadHofUsers()
        }


        hofTypeSelector.setOnClickListener {
            individualTypeSelector.setTextColor(blackColor)
            hofTypeSelector.setTextColor(blueColor)
            loadHofUsers()
        }

        individualTypeSelector.setOnClickListener {
            individualTypeSelector.setTextColor(blueColor)
            hofTypeSelector.setTextColor(blackColor)
            loadIndividualUsers()
        }

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

    private fun loadHofUsers() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                startProgressBar()
            }
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        userList.clear()
                        count = 0
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(NewCustomer::class.java)
                            currentUser?.let {
                                if (it.role == "Head of Family") {
                                    userList.add(it)
                                    count++
                                }
                            }
                        }
                        listSize.text = "All (${count})"
                        adapter.setData(userList)
                        adapter.notifyDataSetChanged()
                        stopProgressBar()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("FirebaseError", "Database operation cancelled: $error")
                        stopProgressBar()
                    }
                }
            })
        }
    }


    private fun loadIndividualUsers() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                startProgressBar()
            }
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        userList.clear()
                        count = 0
                        for (postSnapshot in snapshot.children) {
                            val currentUser = postSnapshot.getValue(NewCustomer::class.java)
                            currentUser?.let {
                                if (it.role == "Individual") {
                                    userList.add(it)
                                    count++
                                }
                            }
                        }
                        listSize.text = "All (${count})"
                        adapter.setData(userList)
                        adapter.notifyDataSetChanged()
                        stopProgressBar()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("FirebaseError", "Database operation cancelled: $error")
                        stopProgressBar()
                    }
                }
            })
        }
    }


    private fun filterCustomers(query: String) {
        if (query.isEmpty()) {
            adapter.setData(userList)
        } else {
            val filteredList = userList.filter { user ->
                user.name?.contains(query, ignoreCase = true) ?: false ||
                        user.phone_no?.contains(query) ?: false ||
                        user.telecom?.contains(query, ignoreCase = true) ?: false
            }
            adapter.setData(filteredList as ArrayList<NewCustomer>)
        }
        adapter.notifyDataSetChanged()
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



//package com.example.Customer
//
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.entities.NewCustomer
//import com.example.rms_project_v2.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//
//class CustomersFragment : Fragment() {
//
//    //changes has to be done in this C1 hof list will be shown here
//
//    private lateinit var userRecyclerView: RecyclerView
//    private lateinit var userList: ArrayList<NewCustomer>
//    private lateinit var adapter: CustomerAdapter
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var mDbRef: DatabaseReference
//    var count: Int=0
//    private  lateinit var hofTypeSelector : TextView
//    private  lateinit var individualTypeSelector : TextView
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_customers, container, false)
//
//        firebaseAuth = FirebaseAuth.getInstance()
//        mDbRef = FirebaseDatabase.getInstance().getReference("Users")
//        userList = ArrayList()
//        adapter = CustomerAdapter(requireContext(),userList)
//        userRecyclerView = rootView.findViewById(R.id.customer_rv)
//        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        userRecyclerView.adapter = adapter
//        individualTypeSelector = rootView.findViewById(R.id.individualTypeSelector)
//        hofTypeSelector = rootView.findViewById(R.id.hofTypeSelector)
//
//        val listSize:TextView=rootView.findViewById(R.id.listSize)
//        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
//
//                for (postSnapshot in snapshot.children) {
//
//                    val currentUser = postSnapshot.getValue(NewCustomer::class.java)
//                    currentUser?.let {
//                        userList.add(it)
////                        Log.d("Testpreet",it.toString())
//                        count+=1
//                        listSize.setText("All (${count})")
//                    }
//                }
//                adapter.notifyDataSetChanged()
//                Toast.makeText(context, "${userList.size}", Toast.LENGTH_LONG).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseError", "Database operation cancelled: $error")
//            }
//        })
//        val blueColor = ContextCompat.getColor(requireContext(), R.color.typeSelectorColor)
//        val blackColor = ContextCompat.getColor(requireContext(), R.color.black)
//        hofTypeSelector.setOnClickListener{
//            individualTypeSelector.setTextColor(blackColor)
//            hofTypeSelector.setTextColor(blueColor)
//            var hofUserList  = userList.filter { it.role=="Head of Family"  }
//            adapter.setData(hofUserList as ArrayList<NewCustomer>)
//            adapter.notifyDataSetChanged()
//
//        }
//        individualTypeSelector.setOnClickListener{
//            individualTypeSelector.setTextColor(blueColor)
//            hofTypeSelector.setTextColor(blackColor)
//            var individualUserList  = userList.filter { it.role=="Individual"  }
//            adapter.setData(individualUserList as ArrayList<NewCustomer>)
//            adapter.notifyDataSetChanged()
//
//        }
////        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
////            override fun onDataChange(snapshot: DataSnapshot) {
////                userList.clear()
////
////                for (postSnapshot in snapshot.children) {
////
////                    val currentUser = postSnapshot.getValue(NewCustomer::class.java)
////                    currentUser?.let {
////                        userList.add(it)
//////                        Log.d("Testpreet",it.toString())
////                        count+=1
////                        listSize.setText("All (${count})")
////                    }
////                }
////                adapter.notifyDataSetChanged()
////                Toast.makeText(context, "${userList.size}", Toast.LENGTH_LONG).show()
////            }
////
////            override fun onCancelled(error: DatabaseError) {
////                Log.e("FirebaseError", "Database operation cancelled: $error")
////            }
////        })
////    fun fetchAllCustomersFromDatabase(): List<NewCustomer> {
////        val db = FirebaseDatabase.getInstance().getReference("Users")
////        return try {
////            val snapshot = db.get()
////            val customers = mutableListOf<NewCustomer>()
////            for (childSnapshot in snapshot.children) {
////                val customer = childSnapshot.getValue(NewCustomer::class.java)
////                customer?.let { customers.add(it) }
////            }
////            customers
////        } catch (exception: Exception) {
////            // Handle the error
////            println("Error getting data: $exception")
////            emptyList()
////        }
////    }
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
//    private fun filterCustomers(query: String) {
//        val listSize:TextView= requireView().findViewById(R.id.listSize)
//        if (query.isEmpty()) {
//            mDbRef.child(firebaseAuth.currentUser?.uid ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    userList.clear()
//                    for (postSnapshot in snapshot.children) {
//                        val currentUser = postSnapshot.getValue(NewCustomer::class.java)
//                        currentUser?.let {
//                            userList.add(it)
//                            count+=1
//                            listSize.setText("All (${count})")
//                        }
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("FirebaseError", "Database operation cancelled: $error")
//                }
//            })
//        } else {
//            val filteredList = userList.filter { user ->
//                user.name?.contains(query, ignoreCase = true) ?: false ||
//                        user.phone_no?.contains(query) ?: false ||
//                        user.telecom?.contains(query, ignoreCase = true) ?: false
//            }
//            adapter.setData(filteredList as ArrayList<NewCustomer>)
//        }
//    }
//
//}