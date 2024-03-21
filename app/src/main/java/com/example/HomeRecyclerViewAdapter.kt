package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R

class NewCustomerRecyclerViewAdapter(val c: Context,val userList:ArrayList<NewUser>):RecyclerView.Adapter<NewCustomerRecyclerViewAdapter.userViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.add_customer_cardview,parent,false)
        return userViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val newUser = userList[position]
    }
    inner class userViewHolder(val v: View):RecyclerView.ViewHolder(v){

    }
}