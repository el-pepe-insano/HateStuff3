package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import kotlinx.coroutines.flow.Flow

class PostRepository(private val postDao: PostDao) {

    val allPosts: Flow<List<PostEntity>> = postDao.getAllPosts()

    suspend fun createPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    suspend fun incrementLikes(postId: Long) {
        postDao.incrementLikes(postId) // Ahora sí existe en PostDao
    }

}