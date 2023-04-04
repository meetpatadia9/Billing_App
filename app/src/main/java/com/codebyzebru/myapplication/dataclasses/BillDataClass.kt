package com.codebyzebru.myapplication.dataclasses

data class BillDataClass(
    val date: String,
    val billNo: String,
    val buyer: String,
    val email: String?,
    val organization: String?,
    val address: String?,
    val contact: String?,
    val purchasedItems: List<PurchasedItemDataClass>?,
    val billTotal: Int
)
