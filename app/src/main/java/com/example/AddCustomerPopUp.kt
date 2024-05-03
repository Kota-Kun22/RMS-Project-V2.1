package com.example

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddCustomerPopUp : AppCompatActivity() {

    private lateinit var Fdatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddCustomerRecyclerViewAdapter
    private val familyMembers = mutableListOf<NewUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_customer_recyclerciew)

        var count = 0


        Fdatabase = FirebaseDatabase.getInstance().getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()


        recyclerView = findViewById(R.id.addCustomerRecyclerview)
        adapter = AddCustomerRecyclerViewAdapter(familyMembers, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val roleSpinner = findViewById<Spinner>(R.id.assign_Role)
        val rolePlans = arrayOf("Assign Role", "Head of Family", "Member")
        val arrayAdapter = ArrayAdapter(
            this@AddCustomerPopUp,
            android.R.layout.simple_spinner_dropdown_item,
            rolePlans
        )
        roleSpinner.adapter = arrayAdapter

        val telecomSpinner = findViewById<Spinner>(R.id.telecom_type1)
        val telecomPlans = arrayOf("Select", "Airtel", "Jio", "Vi", "Bsnl")
        val arrayAdapter1 = ArrayAdapter(
            this@AddCustomerPopUp,
            android.R.layout.simple_spinner_dropdown_item,
            telecomPlans
        )
        telecomSpinner.adapter = arrayAdapter1

        val addMember: TextView = findViewById(R.id.addMember)
        addMember.setOnClickListener {
            familyMembers.add(NewUser("", "", "", "", "", "", 0, listOf(), "","",""))
            adapter.notifyItemInserted(familyMembers.size - 1)
            count += 1
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
                    dob.setText(selectedDate)
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
            val dob: TextView = findViewById(R.id.date_of_birth1)
            val number: EditText = findViewById(R.id.mobile_number1)
            val email: EditText = findViewById(R.id.emailAddress1)

            val userName = name.text.toString().trim()
            val userDob = dob.text.toString().trim()
            val userNumber = number.text.toString().trim()
            val userEmail = email.text.toString().trim()

            if (userName.isEmpty() || userDob.isEmpty() || userNumber.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                saveDetails.isEnabled = true
                saveDetails.text = "Save"
                return@setOnClickListener
            }

            if (userNumber.length != 10) {
                Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show()
                saveDetails.isEnabled = true
                saveDetails.text = "Save"
                return@setOnClickListener
            }

            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (!userEmail.matches(emailPattern.toRegex())) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                saveDetails.isEnabled = true
                saveDetails.text = "Save"
                return@setOnClickListener
            }

            // Continue with saving data
            val membersList = mutableListOf<Member>()
            for (i in 0 until count) {
                val viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(i) as? AddCustomerRecyclerViewAdapter.UserViewHolder
                viewHolder?.let {
                    val temp=it.getMember()
                    temp.hof=userName
                    temp.hofNumber=userNumber
                    membersList.add(temp)
                }
            }

            firebaseAuth.currentUser?.let { currentUser ->
                val user = NewUser(
                    currentUser.uid,
                    userName,
                    userDob,
                    userNumber,
                    userTelecom,
                    userEmail,
                    count,
                    membersList,
                    role,
                    "1",
                    "1"
                )
                Fdatabase.child(currentUser.uid).push().setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        // Enable the save button after completing the save process
                        saveDetails.isEnabled = true
                        saveDetails.text = "Save"
                    }
            }
            val back: ImageView = findViewById(R.id.back_icon)
            back.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }
}

