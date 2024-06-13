package com.example.AddUser

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.entities.Member
import com.example.rms_project_v2.R
import java.util.Calendar

class AddMemeberRecyclerViewAdapter(
    private val userList: MutableList<Member>,
    private val contextt: Context
) : RecyclerView.Adapter<AddMemeberRecyclerViewAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_customer_cardview, parent, false)
        return UserViewHolder(view)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.dobEditText.setOnClickListener {
            var selectedDate: String = ""
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                contextt,
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    holder.dobEditText.text = selectedDate
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameEditText: EditText = itemView.findViewById(R.id.user_name)
        val dobEditText: TextView = itemView.findViewById(R.id.date_of_birth)
        private val numberEditText: EditText = itemView.findViewById(R.id.mobile_number)
        private val telecomEditText: Spinner = itemView.findViewById(R.id.telecom_type)
        private val emailEditText: EditText = itemView.findViewById(R.id.emailAddress)
        private val roleSpinner = itemView.findViewById<Spinner>(R.id.assign_Role1)

        val rolePlans = arrayOf( "Member")
        val arrayAdapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_dropdown_item, rolePlans)

        val telecomPlans = arrayOf("Select", "Airtel", "Jio", "VI", "BSNL")
        val arrayAdapter1 = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_dropdown_item, telecomPlans)

        init {
            roleSpinner.adapter = arrayAdapter
            telecomEditText.adapter = arrayAdapter1
        }

        fun bind(member: Member) {
            nameEditText.setText(member.name)
            dobEditText.text = member.dob
            numberEditText.setText(member.phone_no)
            telecomEditText.setSelection(arrayAdapter1.getPosition(member.telecom))
            emailEditText.setText(member.email)
            roleSpinner.setSelection(arrayAdapter.getPosition(member.role))
        }

        fun getMember(): Member {
            return Member(
                name = nameEditText.text.toString().trim(),
                dob = dobEditText.text.toString().trim(),
                phone_no = numberEditText.text.toString().trim(),
                telecom = telecomEditText.selectedItem.toString(),
                email = emailEditText.text.toString().trim(),
                role = roleSpinner.selectedItem.toString()
//                name = "userddd",
//                dob = "10/2/2002",
//                phone_no = "9999999999",
//                telecom = "Airtel",
//                email = "fds@gmail.com",
//                role = roleSpinner.selectedItem.toString()
            )
        }
    }
}



//package com.example.AddUser
//
//import android.app.DatePickerDialog
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.entities.Member
//import com.example.entities.NewCustomer
//import com.example.rms_project_v2.R
//import java.util.Calendar
//
//class AddMemeberRecyclerViewAdapter(
//    private val userList: MutableList<NewCustomer>,
//    private val contextt: Context,
//    private val hof: String,
//    private val hofNumber: String,
//) :
//    RecyclerView.Adapter<AddMemeberRecyclerViewAdapter.UserViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.add_customer_cardview, parent, false)
//        return UserViewHolder(view)
//    }
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val currentUser = userList[position]
//        holder.dobEditText.setOnClickListener {
//            var selectedDate:String=""
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//            val datePickerDialog = DatePickerDialog(
//                contextt,
//                { _, selectedYear, selectedMonth, selectedDay ->
//                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                    holder.dobEditText.setText(selectedDate)
//                },
//                year,
//                month,
//                dayOfMonth
//            )
//            datePickerDialog.show()
//        }
//        holder.bind(currentUser)
//    }
//
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val nameEditText: EditText = itemView.findViewById(R.id.user_name)
//        val dobEditText: TextView = itemView.findViewById(R.id.date_of_birth)
//        private val numberEditText: EditText = itemView.findViewById(R.id.mobile_number)
//        private val telecomEditText: Spinner = itemView.findViewById(R.id.telecom_type)
//        private val emailEditText: EditText = itemView.findViewById(R.id.emailAddress)
//        private val roleSpinner = itemView.findViewById<Spinner>(R.id.assign_Role1)
//
//        val rolePlans = arrayOf("Individual","Member")
//        val arrayAdapter = ArrayAdapter(itemView.context ,android.R.layout.simple_spinner_dropdown_item,rolePlans)
//
//        val telecomPlans = arrayOf("Select","Airtel","Jio","VI","BSNL")
//        val arrayAdapter1 = ArrayAdapter(itemView.context,android.R.layout.simple_spinner_dropdown_item,telecomPlans)
//
//
//        fun bind(user: NewCustomer) {
//            nameEditText.setText(user.name)
//            dobEditText.setText(user.dob)
//            numberEditText.setText(user.phone_no)
//            emailEditText.setText(user.email)
//            roleSpinner.adapter= arrayAdapter
//            telecomEditText.adapter= arrayAdapter1
//        }
//        fun getMember(): Member {
//            return Member(
//                nameEditText.text.toString(),
//                dobEditText.text.toString(),
//                numberEditText.text.toString(),
//                telecomEditText.selectedItem.toString(),
//                emailEditText.text.toString(),
//                roleSpinner.selectedItem.toString(),
//                hof,
//                hofNumber
//            )
//        }
//    }
//}
