package com.codebyzebru.myapplication.dataclasses

data class BillDataClass(
    var key: String = "",
    val date: String = "",
    val billNo: String = "",
    val buyerUID: String = "",
    val buyer: String = "",
    /*val email: String? = "",
    val organization: String? = "",
    val address: String? = "",
    val contact: String? = "",*/
    val purchasedItems: List<PurchasedItemDataClass>? = emptyList(),
    val subTotal: Float = 0F,
    val tax: Float = 0F,
    val billTotal: Float = 0F
)
