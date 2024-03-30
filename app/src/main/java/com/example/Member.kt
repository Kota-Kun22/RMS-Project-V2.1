package com.example

import android.os.Parcel
import android.os.Parcelable

class Member(
    var name: String? = null,
    var dob: String? = null,
    var phone_no: String? = null,
    var telecom: String? = null,
    var email: String? = null,
    var role:String?=null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dob)
        parcel.writeString(phone_no)
        parcel.writeString(telecom)
        parcel.writeString(email)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Member> {
        override fun createFromParcel(parcel: Parcel): Member {
            return Member(parcel)
        }

        override fun newArray(size: Int): Array<Member?> {
            return arrayOfNulls(size)
        }
    }
}