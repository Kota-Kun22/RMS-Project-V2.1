package com.example

class RechargeDetails {
    var uid:String?=null
    var name:String?=null
    var phone_no:String?=null
    var telecom:String?=null
    var amount:String?=null
    var payment_status:String?=null
    var validity:String?=null
    constructor(){}
    constructor(uid:String?,name:String?,phone_no:String?,telecom:String?,amount:String?,payment_status:String?,validity:String?){
        this.uid=uid
        this.name=name
        this.phone_no=phone_no
        this.telecom=telecom
        this.amount=amount
        this.payment_status=payment_status
        this.validity=validity
    }
}