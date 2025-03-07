package com.example.AddUser

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
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
import java.util.UUID

class AddCustomerEntry : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddMemeberRecyclerViewAdapter
    private val familyMembers = mutableListOf<Member>()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_recyclerciew)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        val roleSpinner = findViewById<Spinner>(R.id.assign_Role)
        val rolePlans = arrayOf("Individual", "Head of Family")
        val arrayAdapter = ArrayAdapter(
            this@AddCustomerEntry,
            android.R.layout.simple_spinner_dropdown_item,
            rolePlans
        )
        roleSpinner.adapter = arrayAdapter

        val telecomSpinner = findViewById<Spinner>(R.id.telecom_type1)
        val telecomPlans = arrayOf("Select", "Airtel", "Jio", "VI", "BSNL")
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

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = parent?.getItemAtPosition(position).toString()
                if (selectedRole == "Head of Family") {
                    saveHof.visibility = View.VISIBLE
                    saveHof.isEnabled = true
                } else {
                    saveHof.visibility = View.GONE
                    saveHof.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        recyclerView = findViewById(R.id.addCustomerRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddMemeberRecyclerViewAdapter(familyMembers, this)
        recyclerView.adapter = adapter

        addMember.setOnClickListener {
            familyMembers.add(Member())
            adapter.notifyItemInserted(familyMembers.size - 1)
            recyclerView.scrollToPosition(familyMembers.size - 1)  // Scroll to the newly added member
        }

        saveHof.setOnClickListener {
            val email: EditText = findViewById(R.id.emailAddress1)
            val name: EditText = findViewById(R.id.user_name1)
            val number: EditText = findViewById(R.id.mobile_number1)
            val userName = name.text.toString().trim()
            val userNumber = number.text.toString().trim()
            if (userName.isEmpty() || userNumber.isEmpty() || userNumber.length != 10 || telecomSpinner.selectedItem.toString() == "Select") {
                Toast.makeText(this, "Please enter name and number to add member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addMember.visibility = View.VISIBLE
            addMember.isEnabled = true
            saveHof.visibility = View.GONE
            saveHof.isEnabled = false
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

            // Disable the save button and show the ProgressBar

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
            if (userName.isEmpty() || userNumber.isEmpty() || userNumber.length != 10 || userDob.isEmpty()) {
                Toast.makeText(this, "Please enter name and number to add member", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveDetails.isEnabled = false
            saveDetails.text = "Loading..."
            progressBar.visibility = View.VISIBLE

            // Update the familyMembers list
            for (i in 0 until adapter.itemCount) {
                val holder = recyclerView.findViewHolderForAdapterPosition(i) as? AddMemeberRecyclerViewAdapter.UserViewHolder
                if (holder != null) {
                    familyMembers[i] = holder.getMember()
                    familyMembers[i].hofNumber = userNumber
                    familyMembers[i].hofName = userName
                }
            }

            if (familyMembers.isEmpty() && roleSpinner.selectedItem.toString() == "Head of Family") {
                Toast.makeText(this, "No family members added", Toast.LENGTH_SHORT).show()
                saveDetails.isEnabled = true
                saveDetails.text = "Save"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            } else if (roleSpinner.selectedItem.toString() == "Head of Family") {
                Log.d("memberCount", familyMembers.size.toString())
                for (member in familyMembers) {
                    Log.d("memberst", member.toString())
                    if (member.name.isNullOrEmpty() || member.dob.isNullOrEmpty() || member.phone_no.isNullOrEmpty() || member.telecom.isNullOrEmpty()) {
                        Toast.makeText(this, "Please fill all the details of each member", Toast.LENGTH_SHORT).show()
                        saveDetails.isEnabled = true
                        saveDetails.text = "Save"
                        progressBar.visibility = View.GONE
                        return@setOnClickListener
                    }
                }
            }
                for (member in familyMembers) {
                    member.uid = UUID.randomUUID().toString()
                }


            firebaseAuth.currentUser?.let { currentUser ->
                val user = NewCustomer(
                    uid = UUID.randomUUID().toString(),
                    name = userName,
                    dob = userDob,
                    phone_no = userNumber,
                    telecom = userTelecom,
                    email = userEmail,
                    count = familyMembers.size,
                    members = familyMembers,
                    role = role,
                    hofName = userName,
                    hofNumber = userNumber
                )

                val newUserRef = databaseReference.push()
                newUserRef.setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this@AddCustomerEntry, "Successfully Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AddCustomerEntry, "Save Failed", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        saveDetails.isEnabled = true
                        saveDetails.text = "Save"
                        progressBar.visibility = View.GONE
                    }
            }
        }
    }
}