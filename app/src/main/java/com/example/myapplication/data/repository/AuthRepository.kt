package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.UserDao
import com.example.myapplication.data.local.entity.UserEntity
import com.example.myapplication.data.mapper.toEntity
import com.example.myapplication.data.model.*
import com.example.myapplication.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем существующих пользователей в БД
                val existingUser = userDao.getUserByUsername(username)
                
                if (existingUser != null) {
                    // Проверяем пароль
                    if (existingUser.password == password) {
                        val user = User(
                            id = existingUser.id,
                            username = existingUser.username,
                            email = existingUser.email,
                            token = existingUser.token
                        )
                        val loginResponse = LoginResponse(
                            token = existingUser.token,
                            user = user
                        )
                        return@withContext Result.success(loginResponse)
                    } else {
                        return@withContext Result.failure(Exception("Неверный логин или пароль"))
                    }
                }
                
                // Если пользователя нет в БД, проверяем тестовые логины (только 1/1 и 2/2)
                if (username == password && username.matches(Regex("^[1-2]$"))) {
                    val userId = username.toInt()
                    val token = "test_token_$username"
                    val testUser = User(
                        id = userId,
                        username = username,
                        email = "user$username@test.com",
                        token = token
                    )
                    val loginResponse = LoginResponse(
                        token = token,
                        user = testUser
                    )
                    // Сохраняем пользователя в БД с паролем
                    userDao.insertUser(UserEntity(
                        id = userId,
                        username = username,
                        email = "user$username@test.com",
                        token = token,
                        password = password
                    ))
                    return@withContext Result.success(loginResponse)
                }
                
                // Иначе пробуем через API
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    userDao.insertUser(loginResponse.user.toEntity(loginResponse.token))
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Неверный логин или пароль"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Неверный логин или пароль"))
            }
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем, не существует ли уже такой пользователь
                val existingUser = userDao.getUserByUsername(username)
                if (existingUser != null) {
                    return@withContext Result.failure(Exception("Пользователь с таким именем уже существует"))
                }
                
                // Проверяем email
                val existingEmail = userDao.getUserByEmail(email)
                if (existingEmail != null) {
                    return@withContext Result.failure(Exception("Email уже используется"))
                }
                
                // Создаем нового пользователя локально
                val maxId = userDao.getAllUsers().maxOfOrNull { it.id } ?: 0
                val newUserId = maxId + 1
                val token = "token_${System.currentTimeMillis()}"
                
                val newUser = User(
                    id = newUserId,
                    username = username,
                    email = email,
                    token = token
                )
                
                // Сохраняем пользователя с паролем
                userDao.insertUser(UserEntity(
                    id = newUserId,
                    username = username,
                    email = email,
                    token = token,
                    password = password
                ))
                
                val loginResponse = LoginResponse(
                    token = token,
                    user = newUser
                )
                
                Result.success(loginResponse)
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка регистрации: ${e.message}"))
            }
        }
    }
    
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            userDao.deleteAllUsers()
        }
    }
}
