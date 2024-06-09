package com.example.Customer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.Member
import com.example.rms_project_v2.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FamilyMemberDetails:AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FamilyMemberDetailRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_member_details)
        val count = intent.getIntExtra("count", 0)
//        val members = intent.getSerializableExtra("members") as? List<Member>
        val jsonString = intent.getStringExtra("memberListJson")

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
}