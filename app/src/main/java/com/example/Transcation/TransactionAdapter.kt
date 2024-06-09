
package com.example.Transcation

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.Transcation.CreditManagement.PendingActivity
import com.example.entities.Transaction
import com.example.rms_project_v2.R

class TransactionAdapter(
    val c: Context,
    val transactionList: ArrayList<Transaction>
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(LayoutInflater.from(c).inflate(R.layout.transaction_cardview, parent, false))
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val currentTransaction = transactionList[position]
        holder.user_name.text = currentTransaction.name
        holder.number2.text = currentTransaction.number
        holder.telecom.text = currentTransaction.telecom
        holder.date.text = currentTransaction.date
        holder.amount.text = "₹" + currentTransaction.amount
        holder.status.text = if (currentTransaction.pending > 0) "Pending" else "Paid"
        if (currentTransaction.pending > 0) {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_red))
            holder.itemView.setOnClickListener {
                val intent = Intent(c, PendingActivity::class.java)
                intent.putExtra("name", currentTransaction.name)
                intent.putExtra("id", currentTransaction.id)
                intent.putExtra("number", currentTransaction.number)
                intent.putExtra("pendingAmount", currentTransaction.pending.toString())
                intent.putExtra("telecom", currentTransaction.telecom)
                intent.putExtra("date", currentTransaction.date)
                intent.putExtra("paid", currentTransaction.paid)
                intent.putExtra("totalAmount", currentTransaction.amount)
                c.startActivity(intent)
            }
        } else {
            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_green))
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

////
//package com.example.Transcationpackage
//
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//import com.example.Transcation.CreditManagement.PendingActivity
//import com.example.entities.Transaction
//import com.example.rms_project_v2.R
//
//class TransactionAdapter(
//    val c: Context,
//    val transactionList: ArrayList<Transaction>
//) :
//    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        return TransactionViewHolder(LayoutInflater.from(c).inflate(R.layout.transaction_cardview, parent, false))
//    }
//
//    override fun getItemCount(): Int {
//        return transactionList.size
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val currentUser = transactionList[position]
//        holder.user_name.text = currentUser.name
//        holder.number.text = currentUser.id
//        holder.telecom.text = currentUser.telecom
//        holder.date.text = currentUser.date
//        holder.amount.text = "₹" + currentUser.amount
//        holder.status.text = if (currentUser.pending > 0) "Pending" else "Paid"
//        if (currentUser.pending > 0) {
//            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_red))
//            holder.itemView.setOnClickListener {
//                val intent = Intent(c, PendingActivity::class.java)
//                intent.putExtra("name", currentUser.name)
//                intent.putExtra("number", currentUser.id)
//                intent.putExtra("amount", currentUser.pending.toString())
//                intent.putExtra("telecom", currentUser.telecom)
//                intent.putExtra("validity", "N/A") // Assuming validity is not part of Transaction data class
//                intent.putExtra("date", currentUser.date)
//                intent.putExtra("hof", "N/A") // Assuming hof is not part of Transaction data class
//                intent.putExtra("hofNumber", "N/A") // Assuming hofNumber is not part of Transaction data class
//                c.startActivity(intent)
//            }
//        } else {
//            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_green))
//        }
//    }
//
//    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val user_name = itemView.findViewById<TextView>(R.id.name2)
//        val number = itemView.findViewById<TextView>(R.id.Number2)
//        val telecom = itemView.findViewById<TextView>(R.id.telecom2)
//        val date = itemView.findViewById<TextView>(R.id.date2)
//        val amount = itemView.findViewById<TextView>(R.id.Amount2)
//        val status = itemView.findViewById<TextView>(R.id.Status2)
//    }
//}
//
//
//
////
////
////import android.content.Context
////import android.content.Intent
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.TextView
////import androidx.core.content.ContextCompat
////import androidx.recyclerview.widget.RecyclerView
////import com.example.Transcation.CreditManagement.PendingActivity
////import com.example.Recharge.RechargeDetails
////import com.example.rms_project_v2.R
////
////class TransactionAdapter(
////    val c: Context,
////    val transactionList: ArrayList<RechargeDetails>
////) :
////    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
////        return TransactionViewHolder(LayoutInflater.from(c).inflate(R.layout.transaction_cardview, parent, false))
////    }
////
////    override fun getItemCount(): Int {
////        return transactionList.size
////    }
////
////    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
////        val currentUser = transactionList[position]
////        holder.user_name.text = currentUser.name
////        holder.number.text = currentUser.phone_no
////        holder.telecom.text = currentUser.telecom
////        holder.date.text = currentUser.date
////        holder.amount.text = "₹"+currentUser.amount
////        holder.status.text = currentUser.payment_status
////        if (currentUser.payment_status == "Pending") {
////            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_red))
////            holder.itemView.setOnClickListener {
////                val intent=Intent(c, PendingActivity::class.java)
////                intent.putExtra("name",currentUser.name)
////                intent.putExtra("number",currentUser.phone_no)
////                intent.putExtra("amount",currentUser.amount)
////                intent.putExtra("telecom",currentUser.telecom)
////                intent.putExtra("validity",currentUser.validity)
////                intent.putExtra("date",currentUser.date)
////                intent.putExtra("hof",currentUser.hof)
////                intent.putExtra("hofNumber",currentUser.hofNumber)
////                c.startActivity(intent)
////            }
////        } else {
////            holder.status.setTextColor(ContextCompat.getColor(c, R.color.g_green))
////        }
////    }
////
////    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        val user_name = itemView.findViewById<TextView>(R.id.name2)
////        val number = itemView.findViewById<TextView>(R.id.Number2)
////        val telecom = itemView.findViewById<TextView>(R.id.telecom2)
////        val date = itemView.findViewById<TextView>(R.id.date2)
////        val amount = itemView.findViewById<TextView>(R.id.Amount2)
////        val status = itemView.findViewById<TextView>(R.id.Status2)
////    }
////}
