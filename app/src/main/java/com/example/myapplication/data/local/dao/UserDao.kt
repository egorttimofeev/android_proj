package com.example.myapplication.data.local.dao

import androidx.room.*
import com.example.myapplication.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Int): Flow<UserEntity?>
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>
    
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE token = :token LIMIT 1")
    suspend fun getUserByToken(token: String): UserEntity?
    
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}
