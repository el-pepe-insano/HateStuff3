package com.example.hatestuff3.data.local.database.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // Insertar un post
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    // Obtener todos los posts (ordenados por fecha, más nuevos primero)
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    // Insertar un comentario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // Obtener comentarios de un post específico
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Int): Flow<List<CommentEntity>>

    // Dar Like (Incrementar contador)
    @Query("UPDATE posts SET likesCount = likesCount + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Int)
}