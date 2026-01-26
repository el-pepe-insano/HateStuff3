package com.example.hatestuff3.ui.viewmodel

import android.content.Context // Importante para manejar archivos
import android.net.Uri        // Importante para leer la imagen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.CommentEntity
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import com.example.hatestuff3.copyImageToInternalStorage // Asegúrate de importar tu utilidad
import kotlinx.coroutines.Dispatchers // Para mover el trabajo pesado fuera del hilo principal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel(private val postDao: PostDao, private val commentDao: CommentDao) : ViewModel() {

    // ESTADO DE BÚSQUEDA
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // LISTA FILTRADA
    val filteredPosts: StateFlow<List<PostEntity>> = combine(
        postDao.getAllPosts(),
        _searchQuery
    ) { posts, query ->
        if (query.isBlank()) {
            posts
        } else {
            posts.filter { it.userName.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Lista completa
    val allPosts: StateFlow<List<PostEntity>> = postDao.getAllPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. CAMBIAR BÚSQUEDA
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // --- FUNCIÓN MODIFICADA PARA GUARDAR IMÁGENES PERMANENTES ---
    fun submitPost(
        context: Context,
        content: String,
        imageUri: String?,
        userName: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Paso 1: Procesar la imagen en segundo plano (IO)
                val finalImagePath = withContext(Dispatchers.IO) {
                    if (imageUri != null) {
                        val originalUri = Uri.parse(imageUri)
                        copyImageToInternalStorage(context, originalUri)
                    } else {
                        null
                    }
                }


                val newPost = PostEntity(
                    userName = userName,
                    content = content,
                    imageUri = finalImagePath,
                    creationTime = System.currentTimeMillis(),
                    likes = 0
                )
                postDao.insertPost(newPost)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError()
            }
        }
    }

    fun likePost(post: PostEntity) {
        viewModelScope.launch {
            val updatedPost = post.copy(likes = post.likes + 1)
            postDao.updatePost(updatedPost)
        }
    }

    fun getComments(postId: Long): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForPost(postId)
    }

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

    fun deletePost(post: PostEntity) {
        viewModelScope.launch {
            postDao.deletePost(post)
        }
    }

    fun deleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            commentDao.deleteComment(comment)
        }
    }
}