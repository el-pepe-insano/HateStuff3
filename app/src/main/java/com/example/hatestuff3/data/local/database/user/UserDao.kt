package com.example.hatestuff3.data.local.database.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 1. Insertar usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 2. Obtener por Email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // 3. Obtener por ID
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    // 4.  Perfil (Bio y Foto)
    @Query("UPDATE users SET bio = :bio, profilePictureUri = :photoUri WHERE id = :userId")
    suspend fun updateUserProfile(userId: Long, bio: String, photoUri: String?)

    // 5. Contar usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // 2. Borrar un usuario (Baneo)
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // 3. Actualizar datos completos del usuario
    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE name = :userName LIMIT 1")
    fun getUserByNameFlow(userName: String): kotlinx.coroutines.flow.Flow<UserEntity?>
}
