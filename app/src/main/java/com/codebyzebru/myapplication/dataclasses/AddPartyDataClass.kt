package com.codebyzebru.myapplication.dataclasses

class AddPartyDataClass(
    val partyName: String,
    val companyName: String,
    val address: String,
    val email: String,
    val contact: String,
    val type: String,
    val totalPurchase: Int = 0
)