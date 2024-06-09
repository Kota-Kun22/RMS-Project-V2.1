package com.example.entities

data class Transaction(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val telecom: String = "",
    val amount: Double = 0.0,
    val paid: Double = 0.0,
    val pending: Double = 0.0
)
