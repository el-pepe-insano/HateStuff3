package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hatestuff3.data.local.database.post.CommentDao
import com.example.hatestuff3.data.local.database.post.PostDao
import com.example.hatestuff3.data.local.database.user.UserDao

class AuthViewModelFactory(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Caso 1: AuthViewModel necesita UserDao
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userDao) as T
        }

        // Caso 2: PostViewModel necesita PostDao
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postDao, commentDao) as T
        }

        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}