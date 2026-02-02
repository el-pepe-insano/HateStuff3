package com.example.hatestuff3.data.remote

import com.example.hatestuff3.data.remote.dto.LoginRequest
import com.example.hatestuff3.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE // <--- Nuevo import
import retrofit2.http.GET    // <--- Nuevo import
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @POST("api/auth/register")
    suspend fun register(@Body user: UserDto): Response<UserDto>

    @PUT("api/auth/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserDto): Response<UserDto>

    // --- AGREGADOS PARA EL ADMIN ---

    // 1. Obtener todos los usuarios
    // Nota: Si en tu backend Java la ruta es diferente (ej: "api/auth/all"), cámbialo aquí.
    @GET("api/auth/all")
    suspend fun getAllUsers(): List<UserDto>

    // 2. Eliminar usuario por ID
    @DELETE("api/auth/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Void>
}