package com.example.entities

class NewCustomer {
    var uid: String? = null
    var name: String? = null
    var dob: String? = null
    var phone_no: String? = null
    var telecom: String? = null
    var email: String? = null
    var count: Int = 0
    var members = mutableListOf<Member>()
    var role: String? = null

    /* removing the
    var hof: String? = null
    var hofNumber: String? = null
    * */

    constructor() {}

//    test


    constructor(
        uid: String?,
        name: String?,
        dob: String?,
        phone_no: String?,
        telecom: String?,
        email: String?,
        count: Int,
        members: List<Member>,
        role: String?,
    ) {
        this.uid = uid
        this.name = name
        this.dob = dob
        this.phone_no = phone_no
        this.telecom = telecom
        this.email = email
        this.count = count
        this.members = members.toMutableList()
        this.role = role

        /**
         *  this.hof = hof
         *  this.hofNumber = hofNumber
         * removed */
    }
}
