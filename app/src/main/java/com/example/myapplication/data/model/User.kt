package com.example.myapplication.data.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val token: String? = null
)
