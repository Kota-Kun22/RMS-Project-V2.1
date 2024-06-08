package com.example.Customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.Member
import com.example.rms_project_v2.R

class FamilyMemberDetails:AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FamilyMemberDetailRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_member_details)
        val count = intent.getIntExtra("count", 0)
        val members = intent.getSerializableExtra("members") as? List<Member>

        recyclerView = findViewById(R.id.familyMember_rv)
        adapter = FamilyMemberDetailRecyclerViewAdapter(this,members!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}