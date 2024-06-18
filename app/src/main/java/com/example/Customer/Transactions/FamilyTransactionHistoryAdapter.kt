
package com.example.Customer.Transactions

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.Transcation.CreditManagement.PendingActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.gson.Gson

class FamilyTransactionHistoryAdapter(
    val c: Context,
    val transactionList: ArrayList<Transaction>
) :
    RecyclerView.Adapter<FamilyTransactionHistoryAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(c).inflate(R.layout.transaction_cardview, parent, false))
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        Log.d("adapterTest1",transactionList.toString())
        val currentTransaction = transactionList[position]
        holder.user_name.text = currentTransaction.name
        holder.number2.text = currentTransaction.number
        holder.telecom.text = currentTransaction.telecom
        holder.date.text = currentTransaction.date
        holder.amount.text = "₹" + currentTransaction.amount
        holder.status.text = if (currentTransaction.pending > 0) "Pending" else "Paid"
        if (currentTransaction.pending > 0) {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_red))

        } else {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_green))
        }

        holder.itemView.setOnClickListener {
            if(currentTransaction.pending>0.0) {
                val intent = Intent(c, PendingActivity::class.java)
                val gson = Gson()
                val jsonString = gson.toJson(currentTransaction)
                intent.putExtra("currentTransaction", jsonString)
                c.startActivity(intent)
            }
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name2)
        val number2 = itemView.findViewById<TextView>(R.id.Number2)
        val telecom = itemView.findViewById<TextView>(R.id.telecom2)
        val date = itemView.findViewById<TextView>(R.id.date2)
        val amount = itemView.findViewById<TextView>(R.id.Amount2)
        val status = itemView.findViewById<TextView>(R.id.Status2)
    }
}
