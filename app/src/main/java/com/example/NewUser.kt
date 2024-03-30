package com.example


class NewUser {
    var uid:String?=null
    var name:String?=null
    var dob:String?=null
    var phone_no:String?=null
    var telecom:String?=null
    var email:String?=null
    var count:Int=0
    lateinit var members:List<Member>
    var role:String?=null
    constructor(){}
    constructor(uid:String?,name:String?,dob:String?,phone_no:String?,telecom:String?,email:String?,count:Int,members:List<Member>,role:String?){
        this.uid=uid
        this.name=name
        this.dob=dob
        this.phone_no=phone_no
        this.telecom=telecom
        this.email=email
        this.count=count
        this.members=members
        this.role=role
    }
}