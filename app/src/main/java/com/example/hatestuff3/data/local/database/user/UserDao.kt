package com.example.hatestuff3.data.local.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
@JvmSuppressWildcards
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(user: UserEntity): Long

    //busca los datos del usuario con un correo en especifico
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    //buscar todos los usuarios
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

    //contar cuantos usuarios existen
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

}
