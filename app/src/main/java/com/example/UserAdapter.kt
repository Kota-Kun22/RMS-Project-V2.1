package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R
import java.io.Serializable

class UserAdapter(val c: Context, val userList: ArrayList<NewUser>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(c).inflate(R.layout.customers_profile_cardview, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom

        holder.itemView.setOnClickListener {
            val intent= Intent(c,FamilyMemberDetails::class.java)
            intent.putExtra("count",currentUser.count)
            intent.putExtra("members",currentUser.members as Serializable)
            c.startActivity(intent)
        }
        holder.recharge.setOnClickListener {
            val intent=Intent(c,RechargeActivity::class.java)
            intent.putExtra("name",currentUser.name)
            intent.putExtra("number",currentUser.phone_no)
            intent.putExtra("telecom",currentUser.telecom)
            c.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name)
        val number = itemView.findViewById<TextView>(R.id.Phone_number_temp)
        val telecom = itemView.findViewById<TextView>(R.id.telecom_temp)
        val recharge=itemView.findViewById<ImageView>(R.id.recharge)
    }
}
