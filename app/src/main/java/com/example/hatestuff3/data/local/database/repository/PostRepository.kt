package com.example.hatestuff3.data.local.database.repository

import com.example.hatestuff3.data.local.database.post.CommentEntity
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import kotlinx.coroutines.flow.Flow

class PostRepository(private val postDao: PostDao) {

    // Variable que contiene la lista de posts en tiempo real (Flow)
    // Si alguien agrega un post, esta variable avisa automáticamente a la pantalla.
    val allPosts: Flow<List<PostEntity>> = postDao.getAllPosts()

    // Función para crear un post
    suspend fun createPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    // Función para dar like
    suspend fun likePost(postId: Int) {
        postDao.incrementLikes(postId)
    }

    // Función para agregar un comentario
    suspend fun addComment(comment: CommentEntity) {
        postDao.insertComment(comment)
    }

    // Función para ver los comentarios de un post específico
    fun getCommentsForPost(postId: Int): Flow<List<CommentEntity>> {
        return postDao.getCommentsForPost(postId)
    }
}