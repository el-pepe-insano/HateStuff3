package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.CommentEntity
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel(private val postDao: PostDao,private val commentDao: CommentDao) : ViewModel() {

    // ESTO ES LO QUE BUSCA TU HOMESCREEN: 'allPosts'
    val allPosts: StateFlow<List<PostEntity>> = postDao.getAllPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun submitPost(content: String, imageUri: String?, userName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val newPost = PostEntity(
                    // NO pongas userId (ya no existe)
                    // NO pongas id (se genera solo)
                    userName = userName,
                    content = content,
                    imageUri = imageUri,
                    creationTime = System.currentTimeMillis(), // Antes era timestamp
                    likes = 0
                )
                // Faltaba esto:
                postDao.insertPost(newPost)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }
    // Función para dar Like recibiendo solo el ID
    fun likePost(post: PostEntity) {
        viewModelScope.launch {
            // Creamos una copia con un like más
            val updatedPost = post.copy(likes = post.likes + 1)
            postDao.updatePost(updatedPost)
        }
    }
    // Obtener comentarios de un post específico
    fun getComments(postId: Long): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForPost(postId)
    }

    // Enviar comentario
    fun sendComment(postId: Long, text: String, userName: String) {
        viewModelScope.launch {
            val comment = CommentEntity(
                postId = postId,
                userName = userName,
                text = text
            )
            commentDao.insertComment(comment)
        }
    }
}
