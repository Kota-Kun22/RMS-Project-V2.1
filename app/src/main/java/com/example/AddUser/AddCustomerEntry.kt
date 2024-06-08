package com.example.AddUser

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MainActivity
import com.example.entities.Member
import com.example.entities.NewCustomer
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddCustomerEntry : AppCompatActivity() {
    // Adding the Customer
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddMemeberRecyclerViewAdapter
    private val familyMembers = mutableListOf<Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_recyclerciew)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()

        val roleSpinner = findViewById<Spinner>(R.id.assign_Role)
        val rolePlans = arrayOf("Individual", "Head of Family")
        val arrayAdapter = ArrayAdapter(
            this@AddCustomerEntry,
            android.R.layout.simple_spinner_dropdown_item,
            rolePlans
        )
        roleSpinner.adapter = arrayAdapter

        val telecomSpinner = findViewById<Spinner>(R.id.telecom_type1)
        val telecomPlans = arrayOf("Select", "Airtel", "Jio", "Vi", "Bsnl")
        val arrayAdapter1 = ArrayAdapter(
            this@AddCustomerEntry,
            android.R.layout.simple_spinner_dropdown_item,
            telecomPlans
        )
        telecomSpinner.adapter = arrayAdapter1

        val addMember: TextView = findViewById(R.id.addMember)
        addMember.isEnabled = false
        addMember.visibility = View.GONE
        val saveHof: TextView = findViewById(R.id.saveHof)
        saveHof.setOnClickListener {
            val name: EditText = findViewById(R.id.user_name1)
            val number: EditText = findViewById(R.id.mobile_number1)
            val userName = name.text.toString().trim()
            val userNumber = number.text.toString().trim()
            if (userName.isEmpty() || userNumber.isEmpty() || userNumber.length != 10) {
                Toast.makeText(this, "Please enter name and number to add member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            recyclerView = findViewById(R.id.addCustomerRecyclerview)
            adapter = AddMemeberRecyclerViewAdapter(familyMembers, this)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            addMember.visibility = View.VISIBLE
            addMember.isEnabled = true
            saveHof.visibility = View.GONE
            saveHof.isEnabled = false
        }
        addMember.setOnClickListener {
            familyMembers.add(Member())
            adapter.notifyItemInserted(familyMembers.size - 1)
        }
        val dob: TextView = findViewById(R.id.date_of_birth1)
        dob.setOnClickListener {
            var selectedDate: String = ""
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dob.text = selectedDate
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
        val back: ImageView = findViewById(R.id.back_icon)
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val saveDetails: TextView = findViewById(R.id.saveDetails)

        saveDetails.setOnClickListener {

            saveDetails.isEnabled = false
            saveDetails.text = "Loading..."

            val role = roleSpinner.selectedItem.toString()
            val userTelecom = telecomSpinner.selectedItem.toString()

            val name: EditText = findViewById(R.id.user_name1)
            val number: EditText = findViewById(R.id.mobile_number1)
            val dob: TextView = findViewById(R.id.date_of_birth1)
            val email: EditText = findViewById(R.id.emailAddress1)

            val userName = name.text.toString().trim()
            val userDob = dob.text.toString().trim()
            val userNumber = number.text.toString().trim()
            val userEmail = email.text.toString().trim()

            // Collect family members' data
            for (i in 0 until adapter.itemCount) {

                val holder = recyclerView.findViewHolderForAdapterPosition(i) as AddMemeberRecyclerViewAdapter.UserViewHolder
                val member = holder.getMember()
                familyMembers[i] = member
            }

            firebaseAuth.currentUser?.let { currentUser ->
                val user = NewCustomer(
                    uid = currentUser.uid,
                    name = userName,
                    dob = userDob,
                    phone_no = userNumber,
                    telecom = userTelecom,
                    email = userEmail,
                    count = familyMembers.size,
                    members = familyMembers,
                    role = role
                )

                databaseReference.child(currentUser.uid).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        saveDetails.isEnabled = true
                        saveDetails.text = "Save"
                    }
            }
        }
    }
}





//package com.example.AddUser
//
//import android.app.DatePickerDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.MainActivity
//import com.example.entities.Member
//import com.example.entities.NewCustomer
//import com.example.rms_project_v2.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import java.util.Calendar
//
//class AddCustomerEntry : AppCompatActivity() {
//    //Adding the Customer
//    private lateinit var Fdatabase: DatabaseReference
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AddMemeberRecyclerViewAdapter
//    private val familyMembers = mutableListOf<NewCustomer>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.add_customer_recyclerciew)
//
//        Fdatabase = FirebaseDatabase.getInstance().getReference("Users")
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        val roleSpinner = findViewById<Spinner>(R.id.assign_Role)
//        val rolePlans = arrayOf("Individual", "Head of Family")
//        val arrayAdapter = ArrayAdapter(
//            this@AddCustomerEntry,
//            android.R.layout.simple_spinner_dropdown_item,
//            rolePlans
//        )
//        roleSpinner.adapter = arrayAdapter
//
//        val telecomSpinner = findViewById<Spinner>(R.id.telecom_type1)
//        val telecomPlans = arrayOf("Select", "Airtel", "Jio", "Vi", "Bsnl")
//        val arrayAdapter1 = ArrayAdapter(
//            this@AddCustomerEntry,
//            android.R.layout.simple_spinner_dropdown_item,
//            telecomPlans
//        )
//        telecomSpinner.adapter = arrayAdapter1
//
//        val addMember: TextView = findViewById(R.id.addMember)
//        addMember.isEnabled = false
//        addMember.visibility = View.GONE
//        val saveHof: TextView = findViewById(R.id.saveHof)
//        saveHof.setOnClickListener {
//            val name: EditText = findViewById(R.id.user_name1)
//            val number: EditText = findViewById(R.id.mobile_number1)
//            val userName = name.text.toString().trim()
//            val userNumber = number.text.toString().trim()
//            if (userName.isEmpty() || userNumber.isEmpty() || userNumber.length!= 10) {
//                Toast.makeText(this, "Please enter name and number to add member", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            recyclerView = findViewById(R.id.addCustomerRecyclerview)
//            adapter = AddMemeberRecyclerViewAdapter(familyMembers, this, userName, userNumber)
//            recyclerView.adapter = adapter
//            recyclerView.layoutManager = LinearLayoutManager(this)
//            addMember.visibility = View.VISIBLE
//            addMember.isEnabled = true
//            saveHof.visibility = View.GONE
//            saveHof.isEnabled = false
//        }
//        addMember.setOnClickListener {
//            familyMembers.add(NewCustomer("", "", "", "", "", "", 0, listOf(), "","",""))
//            adapter.notifyItemInserted(familyMembers.size - 1)
//        }
//        val dob: TextView = findViewById(R.id.date_of_birth1)
//        dob.setOnClickListener {
//            var selectedDate: String = ""
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
//            val datePickerDialog = DatePickerDialog(
//                this,
//                { _, selectedYear, selectedMonth, selectedDay ->
//                    selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                    dob.setText(selectedDate)
//                },
//                year,
//                month,
//                dayOfMonth
//            )
//            datePickerDialog.show()
//        }
//        val back: ImageView = findViewById(R.id.back_icon)
//        back.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//
//        val saveDetails: TextView = findViewById(R.id.saveDetails)
//        saveDetails.setOnClickListener {
//            saveDetails.isEnabled = false
//            saveDetails.text = "Loading..."
//
//            val role = roleSpinner.selectedItem.toString()
//            val userTelecom = telecomSpinner.selectedItem.toString()
//
//            val name: EditText = findViewById(R.id.user_name1)
//            val number: EditText = findViewById(R.id.mobile_number1)
//            val dob: TextView = findViewById(R.id.date_of_birth1)
//            val email: EditText = findViewById(R.id.emailAddress1)
//
//            val userName = name.text.toString().trim()
//            val userDob = dob.text.toString().trim()
//            val userNumber = number.text.toString().trim()
//            val userEmail = email.text.toString().trim()
//
//            val membersList = mutableListOf<Member>()
//            recyclerView.postDelayed({
//                Toast.makeText(this, "FM: ${familyMembers.size}", Toast.LENGTH_LONG).show()
//                for (newUser in familyMembers) {
//                    val member = Member(
//                        newUser.name,
//                        newUser.dob,
//                        newUser.phone_no,
//                        newUser.telecom,
//                        newUser.email,
//                        newUser.role,
//                        newUser.hof,
//                        newUser.hofNumber
//                    )
//                    membersList.add(member)
//                }
//                Toast.makeText(this, "ML: ${membersList.size}", Toast.LENGTH_LONG).show()
//            }, 100)
//
//            firebaseAuth.currentUser?.let { currentUser ->
//                val user = NewCustomer(
//                    currentUser.uid,
//                    userName,
//                    userDob,
//                    userNumber,
//                    userTelecom,
//                    userEmail,
//                    familyMembers.size,
//                    membersList,
//                    role,
//                    "1",
//                    "1"
//                )
//                Fdatabase.child(currentUser.uid).push().setValue(user)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this, MainActivity::class.java))
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnCompleteListener {
//                        saveDetails.isEnabled = true
//                        saveDetails.text = "Save"
//                    }
//            }
//        }
//    }
//}
