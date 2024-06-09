package com.example.Customer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavType
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.NewCustomer
import com.example.Recharge.RechargeActivity
import com.example.rms_project_v2.R
import com.google.gson.Gson
import java.io.Serializable

class CustomerAdapter(private val context: Context, private var userList: ArrayList<NewCustomer>) :
    RecyclerView.Adapter<CustomerAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.customers_profile_cardview, parent, false)
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
            if (currentUser.count != 0) {
                val intent = Intent(context, FamilyMemberDetails::class.java)
                intent.putExtra("count", currentUser.count)

                val gson = Gson()
                val jsonString = gson.toJson(currentUser.members)
                intent.putExtra("memberListJson", jsonString)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No members added", Toast.LENGTH_SHORT).show()
            }
        }

        holder.recharge.setOnClickListener {
            val intent = Intent(context, RechargeActivity::class.java)
            intent.putExtra("name", currentUser.name)
            intent.putExtra("number", currentUser.phone_no)
            intent.putExtra("telecom", currentUser.telecom)
            context.startActivity(intent)
        }

        holder.message.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val number = "+91" + currentUser.phone_no
            val url = "https://api.whatsapp.com/send?phone=$number"
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }

        holder.count.text = "+ ${currentUser.count}"
    }

    fun setData(data: ArrayList<NewCustomer>) {
        userList = data
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name: TextView = itemView.findViewById(R.id.name)
        val number: TextView = itemView.findViewById(R.id.Phone_number_temp)
        val telecom: TextView = itemView.findViewById(R.id.telecom_temp)
        val recharge: ImageView = itemView.findViewById(R.id.recharge)
        val message: ImageView = itemView.findViewById(R.id.message)
        val count: TextView = itemView.findViewById(R.id.count)
    }
}

//
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
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.example.entities.NewCustomer
//import com.example.Recharge.RechargeActivity
//import com.example.rms_project_v2.R
//import java.io.Serializable
//
//class CustomerAdapter(private val context: Context, private var userList: ArrayList<NewCustomer>) :
//    RecyclerView.Adapter<CustomerAdapter.UserViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view: View = LayoutInflater.from(context).inflate(R.layout.customers_profile_cardview, parent, false)
//        return UserViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val currentUser = userList[position]
//        holder.user_name.text = currentUser.name
//        holder.number.text = currentUser.phone_no
//        holder.telecom.text = currentUser.telecom
//
//        holder.itemView.setOnClickListener {
//
//            if(currentUser.count!=0){
//                    val intent= Intent(context, FamilyMemberDetails::class.java)//family memeber
//
//                Toast.makeText(context, "CCN: ${currentUser.count}", Toast.LENGTH_LONG).show()
//                Toast.makeText(context, "MN: ${currentUser.members.size}", Toast.LENGTH_LONG).show()
//                    intent.putExtra("count",currentUser.count)
//                    intent.putExtra("members",currentUser.members as Serializable)
//                    context.startActivity(intent)
//            }
//            else{
//                Toast.makeText(context,"No members added",Toast.LENGTH_SHORT).show()
//            }
//
//
//        }
//        holder.recharge.setOnClickListener {
//            val intent=Intent(context, RechargeActivity::class.java)
//            intent.putExtra("name",currentUser.name)
//            intent.putExtra("number",currentUser.phone_no)
//            intent.putExtra("telecom",currentUser.telecom)
//            intent.putExtra("hof",currentUser.hof)
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
//        holder.count.text="+ "+currentUser.count.toString()
//    }
//
//    fun setData(data: ArrayList<NewCustomer>) {
//        userList = data
//        notifyDataSetChanged()
//    }
//
//    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val user_name: TextView = itemView.findViewById(R.id.name)
//        val number: TextView = itemView.findViewById(R.id.Phone_number_temp)
//        val telecom: TextView = itemView.findViewById(R.id.telecom_temp)
//        val recharge=itemView.findViewById<ImageView>(R.id.recharge)
//        val message=itemView.findViewById<ImageView>(R.id.message)
//        val count=itemView.findViewById<TextView>(R.id.count)
//    }
//}
