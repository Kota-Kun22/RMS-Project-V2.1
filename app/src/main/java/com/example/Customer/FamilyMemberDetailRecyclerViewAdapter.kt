
package com.example.Customer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.Member
import com.example.Recharge.RechargeActivity
import com.example.rms_project_v2.R

class FamilyMemberDetailRecyclerViewAdapter(
    val context: Context,
    var memberList: List<Member>,
    private val onDelete: (Member) -> Unit
) : RecyclerView.Adapter<FamilyMemberDetailRecyclerViewAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.customers_profile_cardview, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val currentUser = memberList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom
        holder.memberCountImage.visibility = View.GONE
        holder.count.visibility = View.GONE

        holder.recharge.setOnClickListener {
            val intent = Intent(context, RechargeActivity::class.java).apply {
                putExtra("name", currentUser.name)
                putExtra("number", currentUser.phone_no)
                putExtra("telecom", currentUser.telecom)
                putExtra("hofName", currentUser.hofName)
                putExtra("hofNumber", currentUser.hofNumber)
            }
            context.startActivity(intent)
        }

        holder.message.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val number = "+91${currentUser.phone_no}"
                val url = "https://api.whatsapp.com/send?phone=$number"
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        }
        holder.itemView.setOnClickListener {
            (context as FamilyMemberDetails).showEditMemberDialog(currentUser)
            true
        }

        holder.itemView.setOnLongClickListener {

            AlertDialog.Builder(context)
                .setTitle("Delete Member")
                .setMessage("Are you sure you want to delete this member?")
                .setPositiveButton("Yes") { _, _ ->
                    onDelete(currentUser)
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name: TextView = itemView.findViewById(R.id.name)
        val number: TextView = itemView.findViewById(R.id.Phone_number_temp)
        val telecom: TextView = itemView.findViewById(R.id.telecom_temp)
        val recharge: ImageView = itemView.findViewById(R.id.recharge)
        val message: ImageView = itemView.findViewById(R.id.message)
        val count: TextView = itemView.findViewById(R.id.count)
        val memberCountImage: ImageView = itemView.findViewById(R.id.memberCountImage)
    }
}

//package com.example.Customer
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.entities.Member
//import com.example.Recharge.RechargeActivity
//import com.example.rms_project_v2.R
//
//
//class FamilyMemberDetailRecyclerViewAdapter(val context: Context, private val memberList:List<Member>):
//    RecyclerView.Adapter<FamilyMemberDetailRecyclerViewAdapter.MemberViewHolder>() {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.customers_profile_cardview, parent, false)
//        return MemberViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return memberList.size
//    }
//
//    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
//        val currentUser = memberList[position]
//        holder.user_name.text = currentUser.name
//        holder.number.text = currentUser.phone_no
//        holder.telecom.text = currentUser.telecom
//        holder.memberCountImage.visibility=View.GONE
//        holder.count.visibility=View.GONE
//        holder.recharge.setOnClickListener {
//            val intent= Intent(context, RechargeActivity::class.java)
//            intent.putExtra("name",currentUser.name)
//            intent.putExtra("number",currentUser.phone_no)
//            intent.putExtra("telecom",currentUser.telecom)
//            intent.putExtra("hofName",currentUser.hofName)
//            intent.putExtra("hofNumber",currentUser.hofNumber)
//            context.startActivity(intent)
//        }
//        holder.message.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            val number="+91"+currentUser.phone_no
//            val url = "https://api.whatsapp.com/send?phone=$number"
//            intent.data = Uri.parse(url)
//            context.startActivity(intent)
//        }
//    }
//    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val user_name = itemView.findViewById<TextView>(R.id.name)
//        val number = itemView.findViewById<TextView>(R.id.Phone_number_temp)
//        val telecom = itemView.findViewById<TextView>(R.id.telecom_temp)
//        val recharge=itemView.findViewById<ImageView>(R.id.recharge)
//        val message=itemView.findViewById<ImageView>(R.id.message)
//        val count=itemView.findViewById<TextView>(R.id.count)
//        val memberCountImage=itemView.findViewById<ImageView>(R.id.memberCountImage)
//    }
//
//}


