package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.repository.PostRepository
import com.example.hatestuff3.data.remote.dto.CommentDto
import com.example.hatestuff3.data.remote.dto.PostDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<PostDto>>(emptyList())
    val posts: StateFlow<List<PostDto>> = _posts.asStateFlow()

    private val _activePostComments = MutableStateFlow<List<CommentDto>>(emptyList())
    val activePostComments: StateFlow<List<CommentDto>> = _activePostComments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = postRepository.getAllPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadComments(postId: Long) {
        viewModelScope.launch {
            try {
                _activePostComments.value = emptyList()
                val comments = postRepository.getComments(postId)
                _activePostComments.value = comments
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createPost(content: String, userName: String, imageUri: android.net.Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                postRepository.createPost(content, userName, imageUri)
                fetchPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(postId: Long, userName: String) {
        viewModelScope.launch {
            try {
                // 1. Actualización optimista de la UI
                _posts.update {
                    it.map {
                        if (it.id == postId) {
                            val isLiked = it.likedBy.contains(userName)
                            val newLikes = if (isLiked) it.likes - 1 else it.likes + 1
                            val newLikedBy = if (isLiked) {
                                it.likedBy - userName
                            } else {
                                it.likedBy + userName
                            }
                            it.copy(likes = newLikes, likedBy = newLikedBy)
                        } else {
                            it
                        }
                    }
                }

                // 2. Llamada a la API
                postRepository.likePost(postId, userName)

            } catch (e: Exception) {
                e.printStackTrace()
                // 3. Revertir si hay error (opcional pero recomendado)
                fetchPosts()
            }
        }
    }

    fun sendComment(postId: Long, content: String, userName: String) {
        viewModelScope.launch {
            try {
                val comment = CommentDto(
                    postId = postId,
                    content = content,
                    userName = userName
                )
                postRepository.createComment(comment)
                loadComments(postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteComment(commentId: Long, postId: Long) {
        viewModelScope.launch {
            try {
                postRepository.deleteComment(commentId)
                loadComments(postId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            try {
                postRepository.deletePost(postId)
                fetchPosts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updatePost(postId: Long, content: String) {
        viewModelScope.launch {
            try {
                val postToUpdate = _posts.value.find { it.id == postId }
                if (postToUpdate != null) {
                    val updatedPost = postToUpdate.copy(content = content)
                    postRepository.updatePost(postId, updatedPost)
                    fetchPosts()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}