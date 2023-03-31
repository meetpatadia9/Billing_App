package com.codebyzebru.myapplication.dataclasses

data class ViewInventoryDataClass(
    var key: String = "",
    val productName: String = "",
    val purchasingPrice: Int = 0,
    val sellingPrice: Int = 0,
    val productQty: Int = 0
)
