package com.codebyzebru.myapplication.dataclasses

data class NewUserEntryDataClass(
    val profileImg: String?,
    val fullName: String,
    val companyName: String,
    val email: String,
    val contact: String,
    val gender: String?,
    val password: String
)