package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.data.remote.UserApi
import com.example.hatestuff3.data.remote.dto.LoginRequest
import com.example.hatestuff3.data.remote.dto.UserDto
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApi: UserApi) {

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
                id = null,
                name = userEntity.name,
                email = userEntity.email,
                password = userEntity.password,
                role = userEntity.role,
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

    // 3. ACTUALIZAR PERFIL (BIO, FOTO, ETC)
    suspend fun updateProfile(userId: Long, bio: String, photoUri: String?): Result<Boolean> {
        return try {
            val userUpdate = UserDto(
                id = userId,
                name = "",
                email = "",
                password = "",
                bio = bio,
                profilePictureUri = photoUri,
                role = null
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

    // 4. OBTENER TODOS LOS USUARIOS (PARA ADMIN)
    suspend fun getAllUsers(): Result<List<UserEntity>> {
        return try {
            val response = userApi.getAllUsers()

            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                // Mapeamos los DTOs a Entidades locales
                val users = dtos.map { dto -> mapDtoToEntity(dto, "") }
                Result.success(users)
            } else {
                Result.failure(Exception("Error al obtener usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. BORRAR USUARIO (Necesita tu rol para validar)
    suspend fun deleteUser(userId: Long, requesterRole: String): Result<Boolean> {
        return try {
            val response = userApi.deleteUser(userId, requesterRole)
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Error al eliminar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 6. CAMBIAR ROL (CORREGIDO)
    // ==========================================
    // Ahora pide 'myRequestRole' porque el backend exige saber quién hace el cambio
    suspend fun updateUserRole(userId: Long, newRole: String, myRequestRole: String): Result<Boolean> {
        return try {
            // Creamos el Mapa simple {"role": "ADMIN"}
            val body = mapOf("role" to newRole)

            // Llamamos a la función nueva de UserApi
            val response = userApi.updateUserRole(userId, body, myRequestRole)

            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Error al cambiar rol: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- MAPPER AUXILIAR ---
    private fun mapDtoToEntity(dto: UserDto, pass: String): UserEntity {
        return UserEntity(
            id = dto.id ?: 0L,
            name = dto.name ?: "Usuario",
            email = dto.email ?: "",
            password = pass,
            bio = dto.bio ?: "",
            profilePictureUri = dto.profilePictureUri,
            role = dto.role ?: "USER"
        )
    }
}