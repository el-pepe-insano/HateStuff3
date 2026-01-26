package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.user.UserEntity

class UserRepository(private val userDao: UserDao) {

    // Login: Busca por email y verifica contraseña manualmente
    suspend fun login(email: String, pass: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByEmail(email) // Nombre correcto del DAO
            if (user != null && user.password == pass) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registro:
    suspend fun register(user: UserEntity): Result<UserEntity> {
        return try {
            val exists = userDao.getUserByEmail(user.email)
            if (exists != null) {
                Result.failure(Exception("El usuario ya existe"))
            } else {
                userDao.insertUser(user) // Nombre correcto del DAO
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar perfil
    suspend fun updateProfile(userId: Long, bio: String, photoUri: String?): Result<Boolean> {
        return try {
            userDao.updateUserProfile(userId, bio, photoUri) // Nombre correcto del DAO
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }
}