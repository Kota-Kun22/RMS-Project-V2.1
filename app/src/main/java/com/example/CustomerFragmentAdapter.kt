package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rms_project_v2.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomerFragmentAdapter(private val c: Context, private var userList: ArrayList<RechargeDetails>) :
RecyclerView.Adapter<CustomerFragmentAdapter.UserViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return CustomerFragmentAdapter.UserViewHolder(LayoutInflater.from(c).inflate(R.layout.expired_cardview, parent, false))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.user_name.text = currentUser.name
        holder.number.text = currentUser.phone_no
        holder.telecom.text = currentUser.telecom
        holder.validity.text = currentUser.validity


        val validityString = currentUser.validity
        val validityValue = validityString?.split(" ")?.get(0)?.toIntOrNull() ?: 0



        val currentDate = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedCurrentDate = sdf.format(currentDate.time)

        val expiry=addDaysToDate(currentUser.date.toString(),validityValue)

        val check=compareDates(expiry,formattedCurrentDate)
        if(check<0){
            holder.expired.text="Expired"
        }
        else{
            holder.expired.setTextColor(ContextCompat.getColor(c, R.color.g_black))
            holder.expired.text="Expiry: $expiry"
        }


        holder.recharge.setOnClickListener {
            val intent= Intent(c, RechargeActivity::class.java)
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


    fun addDaysToDate(dateString: String, daysToAdd: Int): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val date = sdf.parse(dateString)
        if (date != null) {
            calendar.time = date
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
            return sdf.format(calendar.time)
        } else {
            return ""
        }
    }
    fun compareDates(dateString1: String, dateString2: String): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val date1 = sdf.parse(dateString1)
            val date2 = sdf.parse(dateString2)

            return date1.compareTo(date2)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
    fun setData(data: ArrayList<RechargeDetails>) {
        userList = data
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById<TextView>(R.id.name3)
        val number = itemView.findViewById<TextView>(R.id.Phone_number3)
        val telecom = itemView.findViewById<TextView>(R.id.telecom3)
        val validity = itemView.findViewById<TextView>(R.id.validity3)
        val expired = itemView.findViewById<TextView>(R.id.expired3)
        val recharge=itemView.findViewById<ImageView>(R.id.recharge3)
        val message=itemView.findViewById<ImageView>(R.id.message3)
    }
}