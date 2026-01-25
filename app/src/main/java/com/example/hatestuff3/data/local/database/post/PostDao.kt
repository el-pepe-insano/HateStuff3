package com.example.hatestuff3.data.local.database.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // --- 1. POSTS (Lo que ya tenías) ---
    @Query("SELECT * FROM posts ORDER BY creationTime DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    // --- 2. LIKES (Lo que se ve al final de tu foto) ---
    @Query("UPDATE posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Long)

    // --- 3. COMENTARIOS (LO QUE FALTA y causa los errores) ---
    // Debes agregar esto para que el error del Repositorio desaparezca
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Update
    suspend fun updatePost(post: PostEntity)
}