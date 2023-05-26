package com.codebyzebru.myapplication.dataclasses

data class ProfileDataClass(
    var key:String = "",
    val fullName: String = "",
    val companyName: String? = "",
    val email: String = "",
    val contact: String = "",
    val gender: String? = "",
    val address: String? = "",
    val password: String? = ""
)
