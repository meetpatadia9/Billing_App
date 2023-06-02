package com.codebyzebru.myapplication.dataclasses

data class ViewInventoryDataClass(
    var key: String = "",
    val productName: String = "",
    val purchasingPrice: String? = "",
    val sellingPrice: Int = 0,
    var productQty: Int = 0
)
