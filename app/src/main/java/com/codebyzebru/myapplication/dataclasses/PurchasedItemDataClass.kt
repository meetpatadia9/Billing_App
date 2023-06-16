package com.codebyzebru.myapplication.dataclasses

data class PurchasedItemDataClass(
    val key: String = "",
    val pName: String = "",
    val pPrice: Float = 0F,
    val pQty: Int = 0,
    val totalAmt: Float = 0F
)
