package com.example.myapplication.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Состояния процесса авторизации.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel для управления процессом входа и регистрации пользователей.
 *
 * @property authRepository репозиторий для работы с авторизацией
 * @property userPreferences хранилище настроек пользователя
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Пожалуйста, заполните все поля")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(username, password)
            
            result.onSuccess { response ->
                userPreferences.saveAuthToken(
                    response.token,
                    response.user.username,
                    response.user.email
                )
                _authState.value = AuthState.Success("Вход выполнен успешно")
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Ошибка входа"
                )
            }
        }
    }
    
    fun register(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Пожалуйста, заполните все поля")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Неверный формат email")
            return
        }
        
        if (password.length < 6) {
            _authState.value = AuthState.Error("Пароль должен быть не менее 6 символов")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(username, email, password)
            
            result.onSuccess { response ->
                userPreferences.saveAuthToken(
                    response.token,
                    response.user.username,
                    response.user.email
                )
                _authState.value = AuthState.Success("Регистрация прошла успешно")
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Ошибка регистрации"
                )
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
    
    fun logout() {
        viewModelScope.launch {
            userPreferences.clearAuthData()
            _authState.value = AuthState.Idle
        }
    }
}
