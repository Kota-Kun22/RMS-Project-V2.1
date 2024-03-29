package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.FamilyMemberDetails
import com.example.NewUser
import com.example.RechargeActivity
import com.example.rms_project_v2.R
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter(val c: Context, private var userList: ArrayList<NewUser>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var filteredList: ArrayList<NewUser> = ArrayList(userList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(c).inflate(R.layout.customers_profile_cardview, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = filteredList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom

        holder.itemView.setOnClickListener {

            if(currentUser.members.isNullOrEmpty()){
                Toast.makeText(c,"No members added",Toast.LENGTH_SHORT).show()
            }
            else{
                val intent= Intent(c, FamilyMemberDetails::class.java)
                intent.putExtra("count",currentUser.count)
                intent.putExtra("members",currentUser.members as Serializable)
                c.startActivity(intent)
            }
        }
        holder.recharge.setOnClickListener {
            val intent=Intent(c, RechargeActivity::class.java)
            intent.putExtra("name",currentUser.name)
            intent.putExtra("number",currentUser.phone_no)
            intent.putExtra("telecom",currentUser.telecom)
            c.startActivity(intent)
        }
        holder.message.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val number="+91"+currentUser.phone_no
            val url = "https://api.whatsapp.com/send?phone=$number"
            intent.data = Uri.parse(url)
            c.startActivity(intent)
        }
    }

    fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val searchText = text.toLowerCase(Locale.getDefault())
            userList.forEach { user ->
                if ((user.name?.lowercase(Locale.getDefault())?.contains(searchText) == true) ||
                    (user.phone_no?.contains(searchText) == true)) {
                    filteredList.add(user)
                }
            }
        }
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name)
        val number = itemView.findViewById<TextView>(R.id.Phone_number_temp)
        val telecom = itemView.findViewById<TextView>(R.id.telecom_temp)
        val recharge=itemView.findViewById<ImageView>(R.id.recharge)
        val message=itemView.findViewById<ImageView>(R.id.message)
    }
}


    
