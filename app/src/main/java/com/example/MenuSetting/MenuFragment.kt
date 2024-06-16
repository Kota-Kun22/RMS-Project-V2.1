package com.example.MenuSetting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.Authnic.SignIn_activity
import com.example.rms_project_v2.R
import com.google.firebase.auth.FirebaseAuth

class MenuFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val logout = rootView.findViewById<TextView>(R.id.logout)
        val password_change = rootView.findViewById<TextView>(R.id.password_change)
        logout.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                firebaseAuth.signOut()
                Log.d("MenuFragment", "User signed out, starting SignIn_activity")
                startActivity(Intent(requireContext(), SignIn_activity::class.java))
            } else {
                Toast.makeText(requireContext(), "No user is currently signed in", Toast.LENGTH_SHORT).show()
            }
        }

        password_change.setOnClickListener {
            Log.d("MenuFragment", "Password change button clicked")
            val intent = Intent(requireContext(), PasswordChange::class.java)
            startActivity(intent)
            Log.d("MenuFragment", "Intent to PasswordChange activity started")
        }

        return rootView
    }
}
