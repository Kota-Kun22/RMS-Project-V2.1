package com.example.entities

//import android.os.Parcel
//import android.os.Parcelable

class Member(
    var uid:String?=null,
    var name: String? = null,
    var dob: String? = null,
    var phone_no: String? = null,
    var telecom: String? = null,
    var email: String? = null,
    var role: String? = null,
    var hofName: String? = null,
    var hofNumber: String? = null
) {

    override fun toString(): String {
        return "Member(uid=$uid, name=$name, dob=$dob, phone_no=$phone_no, telecom=$telecom, email=$email, role=$role, hofName=$hofName, hofNumber=$hofNumber)"
    }
}


