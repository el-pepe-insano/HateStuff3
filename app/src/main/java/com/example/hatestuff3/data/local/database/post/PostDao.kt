package com.example.hatestuff3.data.local.database.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    //  1. POSTS
    @Query("SELECT * FROM posts ORDER BY creationTime DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    // --- 2. LIKES
    @Query("UPDATE posts SET likes = likes + 1 WHERE id = :postId")
    suspend fun incrementLikes(postId: Long)

    // --- 3. COMENTARIOS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: Long): Flow<List<CommentEntity>>

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)
}