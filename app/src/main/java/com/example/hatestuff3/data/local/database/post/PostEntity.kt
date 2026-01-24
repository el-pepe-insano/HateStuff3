package com.example.hatestuff3.data.local.database.post

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.hatestuff3.data.local.database.user.UserEntity

@Entity(
    tableName = "posts",
    foreignKeys = [
        // Esto asegura que si borras un usuario, se borren sus posts (Opcional, pero recomendado para limpieza)
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
    val authorId: Int,       // ID del usuario que lo publicó
    val content: String,     // Texto del post
    val title: String,       // Título (opcional si quieres)
    val likesCount: Int = 0, // Contador simple de likes
    val timestamp: Long = System.currentTimeMillis() // Fecha de creación
)