package com.example

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R


class FamilyMemberDetailRecyclerViewAdapter(val context: Context, val memberList:List<Member>):
    RecyclerView.Adapter<FamilyMemberDetailRecyclerViewAdapter.MemberViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.customers_profile_cardview, parent, false)
        return FamilyMemberDetailRecyclerViewAdapter.MemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val currentUser = memberList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom
        holder.recharge.setOnClickListener {
            val intent= Intent(context,RechargeActivity::class.java)
            intent.putExtra("name",currentUser.name)
            intent.putExtra("number",currentUser.phone_no)
            intent.putExtra("telecom",currentUser.telecom)
            context.startActivity(intent)
        }
    }
    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name)
        val number = itemView.findViewById<TextView>(R.id.Phone_number_temp)
        val telecom = itemView.findViewById<TextView>(R.id.telecom_temp)
        val recharge=itemView.findViewById<ImageView>(R.id.recharge)
    }

}