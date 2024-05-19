package com.example


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CustomersFragment : Fragment() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<NewUser>
    private lateinit var adapter: UserAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    var count: Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_customers, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        userList = ArrayList()
        adapter = UserAdapter(requireContext(),userList)
        userRecyclerView = rootView.findViewById(R.id.customer_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter
        val listSize:TextView=rootView.findViewById(R.id.listSize)

        mDbRef.child(firebaseAuth.currentUser?.uid ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(NewUser::class.java)
                    currentUser?.let {
                        userList.add(it)
                        count+=1
                        listSize.setText("All (${count})")
                    }
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "${userList.size}", Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Database operation cancelled: $error")
            }
        })
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
    private fun filterCustomers(query: String) {
        val listSize:TextView= requireView().findViewById(R.id.listSize)
        if (query.isEmpty()) {
            mDbRef.child(firebaseAuth.currentUser?.uid ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue(NewUser::class.java)
                        currentUser?.let {
                            userList.add(it)
                            count+=1
                            listSize.setText("All (${count})")
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Database operation cancelled: $error")
                }
            })
        } else {
            val filteredList = userList.filter { user ->
                user.name?.contains(query, ignoreCase = true) ?: false ||
                        user.phone_no?.contains(query) ?: false ||
                        user.telecom?.contains(query, ignoreCase = true) ?: false
            }
            adapter.setData(filteredList as ArrayList<NewUser>)
        }
    }

}