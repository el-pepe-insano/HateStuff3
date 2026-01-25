package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.user.UserDao

// Esta clase ahora acepta los 3 DAOs para repartirlos a los 3 ViewModels
class AuthViewModelFactory(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. AuthViewModel: Para login y registro
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userDao) as T
            }
            // 2. PostViewModel: Para publicaciones y comentarios
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(postDao, commentDao) as T
            }
            // 3. AdminViewModel: Para gestionar usuarios (BORRAR/ROLES)
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(userDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}