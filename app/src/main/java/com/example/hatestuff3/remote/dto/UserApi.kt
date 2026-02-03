package com.example.hatestuff3.data.remote

import com.example.hatestuff3.data.remote.dto.LoginRequest
import com.example.hatestuff3.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UserDto>

    @POST("api/auth/register")
    suspend fun register(@Body user: UserDto): Response<UserDto>

    // Actualizar perfil normal (nombre, foto)
    @PUT("api/auth/update/{id}") // Ojo: Verifica si en tu backend es "update/{id}" o solo "{id}"
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserDto): Response<UserDto>

    // --- AGREGADOS PARA EL ADMIN ---

    // 1. Obtener todos los usuarios
    @GET("api/auth/all")
    suspend fun getAllUsers(): Response<List<UserDto>>

    // 2. Eliminar usuario por ID
    @DELETE("api/auth/{id}")
    suspend fun deleteUser(
        @Path("id") id: Long,
        @Header("role") userRole: String // Enviamos TU rol para validar permiso
    ): Response<Void>

    // 3. CAMBIAR ROL (NUEVO)
    @PUT("api/auth/role/{id}")
    suspend fun updateUserRole(
        @Path("id") id: Long,
        @Body roleData: Map<String, String>, // Enviaremos: {"role": "ADMIN"}
        @Header("role") requesterRole: String // TU rol de Admin
    ): Response<Void>
}