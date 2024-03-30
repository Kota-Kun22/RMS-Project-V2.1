package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R

class AddCustomerRecyclerViewAdapter(
    private val userList: MutableList<NewUser>,
) :
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
        private val telecomEditText: Spinner = itemView.findViewById(R.id.telecom_type)
        private val emailEditText: EditText = itemView.findViewById(R.id.emailAddress)
        private val roleSpinner = itemView.findViewById<Spinner>(R.id.assign_Role1)

        val rolePlans = arrayOf("Assign Role","Head of Family","Child","Wife","Husband","Father","Mother")
        val arrayAdapter = ArrayAdapter(itemView.context ,android.R.layout.simple_spinner_dropdown_item,rolePlans)

        val telecomPlans = arrayOf("Select","Airtel","Jio","Vi","Bsnl")
        val arrayAdapter1 = ArrayAdapter(itemView.context,android.R.layout.simple_spinner_dropdown_item,telecomPlans)



        fun bind(user: NewUser) {
            nameEditText.setText(user.name)
            dobEditText.setText(user.dob)
            numberEditText.setText(user.phone_no)
            emailEditText.setText(user.email)
            roleSpinner.adapter= arrayAdapter
            telecomEditText.adapter= arrayAdapter1
        }
        fun getMember(): Member {
            return Member(
                nameEditText.text.toString(),
                dobEditText.text.toString(),
                numberEditText.text.toString(),
                telecomEditText.selectedItem.toString(),
                emailEditText.text.toString(),
                roleSpinner.selectedItem.toString()
            )
        }
    }
}
