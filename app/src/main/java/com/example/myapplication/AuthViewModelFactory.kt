package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.ui.auth.AuthViewModel

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
