package com.example

import com.example.rms_project_v2.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class AddCustomerRecyclerViewAdapter(private val userList: MutableList<NewUser>) :
    RecyclerView.Adapter<AddCustomerRecyclerViewAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_customer_cardview, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameEditText: EditText = itemView.findViewById(R.id.user_name)
        private val dobEditText: EditText = itemView.findViewById(R.id.date_of_birth)
        private val numberEditText: EditText = itemView.findViewById(R.id.mobile_number)
        private val telecomEditText: EditText = itemView.findViewById(R.id.telecom_type)
        private val emailEditText: EditText = itemView.findViewById(R.id.emailAddress)

        fun bind(user: NewUser) {
            nameEditText.setText(user.name)
            dobEditText.setText(user.dob)
            numberEditText.setText(user.phone_no)
            telecomEditText.setText(user.telecom)
            emailEditText.setText(user.email)
        }
    }
}
