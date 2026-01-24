package com.example.hatestuff3.data.local.database.post

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.hatestuff3.data.local.database.user.UserEntity
// Asegúrate de importar PostEntity si no está en el mismo paquete.
// Si te sale en rojo PostEntity, dale Alt+Enter para importarlo.

@Entity(
    tableName = "comments",
    foreignKeys = [
        // Un comentario pertenece a un Post
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        ),
        // Un comentario pertenece a un Usuario
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postId: Int,      // A qué post pertenece
    val authorId: Int,    // Quién lo escribió
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)