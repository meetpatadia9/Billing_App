package com.codebyzebru.myapplication.dataclasses

data class NewUserEntryDataClass(
    val fullName: String,
    val email: String,
    val contact: String,
    val gender: String?,
    val password: String
)
