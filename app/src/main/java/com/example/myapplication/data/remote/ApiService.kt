package com.example.myapplication.data.remote

import com.example.myapplication.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
    
    @GET("images")
    suspend fun getImages(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ImagesResponse>
}
