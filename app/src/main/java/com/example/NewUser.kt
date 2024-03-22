package com.example


class NewUser {
    var uid:String?=null
    var name:String?=null
    var dob:String?=null
    var phone_no:String?=null
    var telecom:String?=null
    var email:String?=null
    constructor(){}
    constructor(uid:String?,name:String?,dob:String?,phone_no:String?,telecom:String?,email:String?){
        this.uid=uid
        this.name=name
        this.dob=dob
        this.phone_no=phone_no
        this.telecom=telecom
        this.email=email

    }
}