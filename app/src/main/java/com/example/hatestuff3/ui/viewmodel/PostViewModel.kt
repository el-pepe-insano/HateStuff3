package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.CommentEntity
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.post.PostEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostViewModel(private val postDao: PostDao, private val commentDao: CommentDao) : ViewModel() {

    // 1. ESTADO DE BÚSQUEDA
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. LISTA FILTRADA (Esta es la que usa ahora tu HomeScreen)
    // Combina la base de datos con el texto de búsqueda
    val filteredPosts: StateFlow<List<PostEntity>> = combine(
        postDao.getAllPosts(),
        _searchQuery
    ) { posts, query ->
        if (query.isBlank()) {
            posts
        } else {
            // Filtra si el nombre del usuario contiene el texto (mayúsculas o minúsculas)
            posts.filter { it.userName.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Mantenemos esto por si acaso, pero la UI principal ahora usa filteredPosts
    val allPosts: StateFlow<List<PostEntity>> = postDao.getAllPosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. FUNCIÓN PARA CAMBIAR EL TEXTO DE BÚSQUEDA
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun submitPost(content: String, imageUri: String?, userName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val newPost = PostEntity(
                    userName = userName,
                    content = content,
                    imageUri = imageUri,
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