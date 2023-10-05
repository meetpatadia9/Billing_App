package com.codebyzebru.myapplication.dataclasses

data class PurchasedItemDataClass(
    val key: String = "",
    val pname: String = "",
    val pprice: Float = 0F,
    val pqty: Int = 0,
    val totalAmt: Float = 0F
)
