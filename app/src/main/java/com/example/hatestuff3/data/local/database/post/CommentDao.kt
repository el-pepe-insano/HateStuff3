package com.example.hatestuff3.data.local.database.post


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert
    suspend fun insertComment(comment: CommentEntity)

    // Obtener comentarios de un post específico, ordenados del más reciente al más antiguo
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp DESC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>
}