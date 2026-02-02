package com.example.hatestuff3.data.local.database.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val email: String,
    val password: String,
    val bio: String = "",
    val profilePictureUri: String? = null,
    val role: String? = "USER"
)
