package com.example.hatestuff3.data.local.database.post

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.hatestuff3.data.local.database.user.UserEntity

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorId: Int,
    val authorName: String, // Asegúrate de que esta línea exista
    val content: String,
    val imageUri: String? = null,
    val likesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)