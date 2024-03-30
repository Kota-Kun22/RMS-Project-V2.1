package com.example

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R

class HomeFragmentAdapter(private val context: Context, private var userList: ArrayList<NewUser>) :
RecyclerView.Adapter<HomeFragmentAdapter.UserViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name: TextView = itemView.findViewById(R.id.name)
    }
}