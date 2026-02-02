package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.data.remote.UserApi
import com.example.hatestuff3.data.remote.dto.LoginRequest
import com.example.hatestuff3.data.remote.dto.UserDto

class UserRepository(private val userApi: UserApi) {

    // 1. LOGIN
    suspend fun login(email: String, pass: String): Result<UserEntity> {
        return try {
            val response = userApi.login(LoginRequest(email, pass))

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val entity = mapDtoToEntity(dto, pass)
                Result.success(entity)
            } else {
                Result.failure(Exception("Error de login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. REGISTRO
    suspend fun register(userEntity: UserEntity): Result<UserEntity> {
        return try {
            val dtoToSend = UserDto(
                name = userEntity.name,
                email = userEntity.email,
                password = userEntity.password,
                role = "USER",
                profilePictureUri = userEntity.profilePictureUri,
                bio = userEntity.bio
            )

            val response = userApi.register(dtoToSend)

            if (response.isSuccessful && response.body() != null) {
                val createdDto = response.body()!!
                val createdEntity = mapDtoToEntity(createdDto, userEntity.password)
                Result.success(createdEntity)
            } else {
                Result.failure(Exception("Error al registrar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. ACTUALIZAR PERFIL
    suspend fun updateProfile(userId: Long, bio: String, photoUri: String?): Result<Boolean> {
        return try {
            val userUpdate = UserDto(
                id = userId,
                name = "",
                email = "",
                bio = bio,
                profilePictureUri = photoUri
            )

            val response = userApi.updateUser(userId, userUpdate)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al actualizar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- TRADUCTOR ---
    private fun mapDtoToEntity(dto: UserDto, pass: String): UserEntity {
        return UserEntity(
            id = dto.id ?: 0L,
            name = dto.name,
            email = dto.email,
            password = pass,
            bio = dto.bio ?: "",
            profilePictureUri = dto.profilePictureUri,
            role = dto.role ?: "USER"
        )
    }
    suspend fun getAllUsers(): Result<List<UserEntity>> {
        return try {
            // Nota: Asumimos que tu UserApi tiene una función getAllUsers().
            // Si no la tiene, tendrás que agregarla en UserApi.kt: @GET("users") suspend fun getAllUsers(): List<UserDto>
            val response = userApi.getAllUsers() // Esto devolverá List<UserDto>

            // Convertimos la lista de DTO a Entity
            val users = response.map { dto -> mapDtoToEntity(dto, "") }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Borrar usuario
    suspend fun deleteUser(userId: Long): Result<Boolean> {
        return try {
            val response = userApi.deleteUser(userId) // Asumiendo @DELETE("users/{id}") en UserApi
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Error"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Cambiar rol
    suspend fun updateUserRole(userId: Long, newRole: String): Result<Boolean> {
        return try {
            val userUpdate = UserDto(role = newRole, name="", email="") // Solo mandamos el rol
            val response = userApi.updateUser(userId, userUpdate)
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Error"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}