package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.AddUser.AddCustomerEntry
import com.example.Customer.CustomersFragment
import com.example.Home.HomeFragment
import com.example.MenuSetting.MenuFragment
import com.example.Transcation.TransactionsFragment
import com.example.rms_project_v2.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addCustomer: ImageView = findViewById(R.id.add_customer)
        addCustomer.setOnClickListener {
            startActivity(Intent(this, AddCustomerEntry::class.java))
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_ic -> {
                    replaceFragment(HomeFragment())
                    title = "Home"
                }
                R.id.transfer_ic -> {
                    replaceFragment(TransactionsFragment())
                    title = "Transactions"
                }
                R.id.menu_ic -> {
                    replaceFragment(MenuFragment())
                    title = "More"
                }
                R.id.person_ic -> {
                    replaceFragment(CustomersFragment())
                    title = "Customer List"
                }
            }
            return@setOnItemSelectedListener true
        }
        //By default selected
        replaceFragment(HomeFragment())
        title = "Home"
        bottomNavigationView.selectedItemId=R.id.home_ic

    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
