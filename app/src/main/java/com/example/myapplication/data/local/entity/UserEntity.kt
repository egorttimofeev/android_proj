package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val username: String,
    val email: String,
    val token: String,
    val password: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
