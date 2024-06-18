package com.example.Customer

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Customer.Transactions.FamilyTrasactionHistory
import com.example.MainActivity
import com.example.entities.Member
import com.example.entities.NewCustomer
import com.example.entities.Transaction
import com.example.rms_project_v2.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

class FamilyMemberDetails : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FamilyMemberDetailRecyclerViewAdapter
    private lateinit var hofName: String
    private lateinit var hofNumber: String
    private lateinit var hofTelecom: String
    private lateinit var messageButton: ImageView
    private lateinit var editHOFButton: Button
    private lateinit var editTextHOFNameTelecom: Spinner
    private lateinit var editTextTelecom: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_member_details)
        val count = intent.getIntExtra("count", 0)
        val jsonString = intent.getStringExtra("memberListJson")
        hofName = intent.getStringExtra("HOF_Name")!!
        hofNumber = intent.getStringExtra("HOF_Number")!!
        hofTelecom = intent.getStringExtra("HOF_TELECOM")!!

        findViewById<TextView>(R.id.HOFName).text = hofName
        findViewById<TextView>(R.id.HOFNumber).text = hofNumber



        findViewById<ImageView>(R.id.messageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val number = "+91" + hofNumber
            val url = "https://api.whatsapp.com/send?phone=$number"
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        val back: ImageView = findViewById(R.id.backButton)
        back.setOnClickListener {
            finish()
        }
        editHOFButton = findViewById(R.id.editHOFButton)
        editHOFButton.setOnClickListener {
            showEditHOFDialog(hofTelecom)
        }
        findViewById<ImageView>(R.id.family_transaction_history).setOnClickListener {
            val intent = Intent(this@FamilyMemberDetails, FamilyTrasactionHistory::class.java)
            intent.putExtra("hofNumber", hofNumber.toString())
            startActivity(intent)
        }
        showTotalPending(hofNumber)
        findViewById<Button>(R.id.addMemberButton).setOnClickListener {
            showAddMemberDialog()
        }

        // Convert the JSON string back to a list of Member objects using Gson
        val listType = object : TypeToken<List<Member>>() {}.type
        val gson = Gson()
        val members = gson.fromJson<List<Member>>(jsonString, listType)

        Log.d("Testpreet", "In FamilyMemberDetails")
        Log.d("Testpreet", members.toString())
        recyclerView = findViewById(R.id.familyMember_rv)
        adapter = FamilyMemberDetailRecyclerViewAdapter(this, members) { member ->
            deleteMember(member)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun deleteMember(member: Member) {
        val mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (customerSnapshot in snapshot.children) {
                    val customer = customerSnapshot.getValue(NewCustomer::class.java)
                    customer?.let {
                        val memberToDelete = customer.members.find { it.uid == member.uid }
                        if (memberToDelete != null) {
                            // Remove member from the list
                            customer.members.remove(memberToDelete)

                            // Update count
                            customer.count = customer.members.size

                            // Update the customer in Firebase
                            mDbRef.child(customerSnapshot.key!!).setValue(customer)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@FamilyMemberDetails,
                                        "Member deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Update the UI if needed, for example, by removing the member from the list and notifying the adapter
                                    val updatedList = adapter.memberList.toMutableList()
                                    updatedList.remove(member)
                                    adapter.memberList = updatedList
                                    adapter.notifyDataSetChanged()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this@FamilyMemberDetails,
                                        "Failed to delete member: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@FamilyMemberDetails,
                    "Failed to delete member: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun showTotalPending(hofNumber: String?) {
        val transactionList = mutableListOf<Transaction>()
        val transactionListTemp = mutableListOf<Transaction>()
        val mDbRef = FirebaseDatabase.getInstance().getReference("Transactions")
        val totalPending = findViewById<TextView>(R.id.totalPending)
        totalPending.text = "Loading Total Pending..."
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
            }
            mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    GlobalScope.launch(Dispatchers.Main) {
                        transactionList.clear()

                        for (childSnapshot in snapshot.children) {
                            val transaction = childSnapshot.getValue(Transaction::class.java)
                            transaction?.let {
                                transactionList.add(it)
                            }
                        }

                        transactionListTemp.addAll(transactionList.filter {
                            Log.d("trTest", "${it.hofNumber} == $hofNumber")
                            it.hofNumber == hofNumber && it.pending > 0
                        })
                        val totalPendingAmount = transactionListTemp.sumOf { it.pending }
                        totalPending.text = "Total Pending: ₹$totalPendingAmount"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.e("TransactionsFragment", "Error fetching data", error.toException())
                        Toast.makeText(this@FamilyMemberDetails, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
    private fun showAddMemberDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_member, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Member")
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            editTextTelecom = dialogView.findViewById<Spinner>(R.id.editTextTelecom)
            val telecomPlans = arrayOf("Select", "Airtel", "Jio", "VI", "BSNL")
            val arrayAdapter2 = ArrayAdapter(
                this@FamilyMemberDetails,
                android.R.layout.simple_spinner_dropdown_item,
                telecomPlans
            )
            editTextTelecom.adapter = arrayAdapter2

            val editTextDob: TextView = dialogView.findViewById(R.id.editTextDob)
            editTextDob.setOnClickListener {
                var selectedDate: String = ""
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        editTextDob.text = selectedDate
                    },
                    year,
                    month,
                    dayOfMonth
                )
                datePickerDialog.show()
            }
            button.setOnClickListener {
                val name = dialogView.findViewById<EditText>(R.id.editTextName).text.toString()
                val dob = dialogView.findViewById<TextView>(R.id.editTextDob).text.toString()
                val phone = dialogView.findViewById<EditText>(R.id.editTextPhone).text.toString()


                if (name.isNotEmpty() && phone.isNotEmpty() && editTextTelecom.selectedItem.toString()!= "Select") {
                    val newMember = Member(
                        uid = UUID.randomUUID().toString(),
                        name = name,
                        dob = dob,
                        phone_no = phone,
                        telecom = editTextTelecom.selectedItem.toString(),
                        role = "Member",
                        hofName = hofName.toString(),
                        hofNumber = hofNumber.toString()
                    )
                    addNewMember(newMember)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
    private fun addNewMember(newMember: Member) {
        val mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (customerSnapshot in snapshot.children) {
                    val customer = customerSnapshot.getValue(NewCustomer::class.java)
                    customer?.let {
                        if (customer.hofNumber == newMember.hofNumber) {
                            customer.members.add(newMember)
                            customer.count = customer.members.size

                            mDbRef.child(customerSnapshot.key!!).setValue(customer)
                                .addOnSuccessListener {
                                    Toast.makeText(this@FamilyMemberDetails, "Member added successfully", Toast.LENGTH_SHORT).show()
                                    // Update the UI
                                    val updatedList = adapter.memberList.toMutableList()
                                    updatedList.add(newMember)
                                    adapter.memberList = updatedList
                                    adapter.notifyDataSetChanged()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@FamilyMemberDetails, "Failed to add member: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FamilyMemberDetails, "Failed to add member: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun showEditMemberDialog(member: Member) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_member, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Member")
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
            val dobEditText = dialogView.findViewById<TextView>(R.id.editTextDob)
            val phoneEditText = dialogView.findViewById<EditText>(R.id.editTextPhone)
            editTextTelecom = dialogView.findViewById<Spinner>(R.id.editTextTelecom)
            val telecomPlans = arrayOf("Select", "Airtel", "Jio", "VI", "BSNL")
            val arrayAdapter2 = ArrayAdapter(
                this@FamilyMemberDetails,
                android.R.layout.simple_spinner_dropdown_item,
                telecomPlans
            )
            editTextTelecom.adapter = arrayAdapter2

            // Populate the fields with current member details
            nameEditText.visibility = EditText.GONE
            dobEditText.visibility = EditText.GONE

            phoneEditText.setText(member.phone_no)

            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {

                val phone = phoneEditText.text.toString()
                val telecom = editTextTelecom.selectedItem.toString()

                if (phone.isNotEmpty() && telecom!="Select") {
                    member.name = member.name
                    member.dob = member.dob
                    member.phone_no = phone
                    member.telecom = telecom
                    member.role = member.role

                    updateMember(member)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
    private fun updateMember(member: Member) {
        val mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (customerSnapshot in snapshot.children) {
                    val customer = customerSnapshot.getValue(NewCustomer::class.java)
                    customer?.let {
                        if (customer.hofNumber == member.hofNumber) {
                            // Find the member to update
                            val memberIndex = customer.members.indexOfFirst { it.uid == member.uid }
                            if (memberIndex != -1) {
                                customer.members[memberIndex] = member
                                mDbRef.child(customerSnapshot.key!!).setValue(customer)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@FamilyMemberDetails, "Member updated successfully", Toast.LENGTH_SHORT).show()
                                        // Update the UI
                                        val updatedList = adapter.memberList.toMutableList()
                                        updatedList[memberIndex] = member
                                        adapter.memberList = updatedList
                                        adapter.notifyDataSetChanged()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this@FamilyMemberDetails, "Failed to update member: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FamilyMemberDetails, "Failed to update member: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditHOFDialog(telecom: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_hof, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit HOF")
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            editTextHOFNameTelecom = dialogView.findViewById<Spinner>(R.id.editTextHOFNameTelecom)
            val telecomPlans = arrayOf("Select", "Airtel", "Jio", "VI", "BSNL")
            val arrayAdapter1 = ArrayAdapter(
                this@FamilyMemberDetails,
                android.R.layout.simple_spinner_dropdown_item,
                telecomPlans
            )
            editTextHOFNameTelecom.adapter = arrayAdapter1



            // Populate the fields with current HOF details

            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val newTelecom = editTextHOFNameTelecom.selectedItem.toString()

                if ( newTelecom!= "Select") {
                    updateHOFDetails( newTelecom)
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }
    private fun updateHOFDetails(newTelecom: String) {
        val mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        mDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (customerSnapshot in snapshot.children) {
                    val customer = customerSnapshot.getValue(NewCustomer::class.java)
                    customer?.let {
                        if (customer.hofNumber == hofNumber) {  // Assuming hofNumber is the original HOF number
                            customer.hofName = hofName
                            customer.hofNumber = hofNumber
                            customer.telecom = newTelecom
                            customer.hofNumber = hofNumber
                            customer.hofName = hofName
                            mDbRef.child(customerSnapshot.key!!).setValue(customer)
                                .addOnSuccessListener {
                                    Toast.makeText(this@FamilyMemberDetails, "HOF updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@FamilyMemberDetails, "Failed to update HOF: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }

                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FamilyMemberDetails, "Failed to update HOF: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




}