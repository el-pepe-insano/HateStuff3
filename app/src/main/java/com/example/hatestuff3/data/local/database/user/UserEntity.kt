package com.example.hatestuff3.data.local.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(
    //identifica la llave primaria
    @PrimaryKey(autoGenerate = true) //se crea de manera automática
    val id: Long = 0L,

    val name: String,
    val email: String,
    val password: String,
    val bio: String = "",
    val profilePictureUri: String? = null
    //investigar como identificar las llaves foráneas
)
