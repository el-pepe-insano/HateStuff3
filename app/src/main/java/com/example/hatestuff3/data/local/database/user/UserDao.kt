package com.example.hatestuff3.data.local.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // 1. Insertar usuario (Registro)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // 2. Obtener por Email (Login)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // 3. Obtener por ID (Para refrescar perfil tras cambios)
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    // 4. Actualizar Perfil (Bio y Foto)
    @Query("UPDATE users SET bio = :bio, profilePictureUri = :photoUri WHERE id = :userId")
    suspend fun updateUserProfile(userId: Long, bio: String, photoUri: String?)

    // 5. Contar usuarios (Para inicializar DB)
    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}