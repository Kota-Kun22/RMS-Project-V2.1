package com.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R

class TransactionAdapter(
    val c: Context,
    val transactionList: ArrayList<RechargeDetails>
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(c).inflate(R.layout.transaction_cardview, parent, false))
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val currentUser = transactionList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom
        holder.date.text = currentUser.date
        holder.amount.text = "₹"+currentUser.amount
        holder.status.text = currentUser.payment_status
        if (currentUser.payment_status == "Credit") {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_red))
        } else {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_green))
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name2)
        val number = itemView.findViewById<TextView>(R.id.Number2)
        val telecom = itemView.findViewById<TextView>(R.id.telecom2)
        val date = itemView.findViewById<TextView>(R.id.date2)
        val amount = itemView.findViewById<TextView>(R.id.Amount2)
        val status = itemView.findViewById<TextView>(R.id.Status2)
    }
}