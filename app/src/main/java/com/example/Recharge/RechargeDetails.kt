package com.example.Recharge

class RechargeDetails {
    var uid: String? = null
    var name: String? = null
    var phone_no: String? = null
    var telecom: String? = null
    var amount: String? = null
    var payment_status: String? = null
    var validity: String? = null
    var date: String? = null
    var hofName: String? = null // Added hof field
    var hofNumber: String? = null // Added hofNumber field

    constructor() {}

    constructor(
        uid: String?,
        name: String?,
        phone_no: String?,
        telecom: String?,
        amount: String?,
        payment_status: String?,
        validity: String?,
        date: String?,
        hof: String?, // Include hof in the constructor parameters
        hofNumber: String? // Include hofNumber in the constructor parameters
    ) {
        this.uid = uid
        this.name = name
        this.phone_no = phone_no
        this.telecom = telecom
        this.amount = amount
        this.payment_status = payment_status
        this.validity = validity
        this.date = date
        this.hofName = hof // Assign hof to the property
        this.hofNumber = hofNumber // Assign hofNumber to the property
    }

    fun matches(other: RechargeDetails): Boolean {
        return (uid == other.uid
                && name == other.name
                && phone_no == other.phone_no
                && telecom == other.telecom
                && amount == other.amount
                && payment_status == other.payment_status
                && validity == other.validity
                && date == other.date
                && hofName == other.hofName // Check hof equality
                && hofNumber == other.hofNumber) // Check hofNumber equality
    }
}
