package com.example.hatestuff3.data.local.database.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val imageUri: String?,
    // AGREGAMOS ESTOS CAMPOS QUE FALTABAN PARA QUE EL DAO FUNCIONE
    val creationTime: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val userName: String
)