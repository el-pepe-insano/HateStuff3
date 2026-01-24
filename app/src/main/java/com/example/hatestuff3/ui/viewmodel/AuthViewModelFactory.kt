package com.example.hatestuff3.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hatestuff3.data.local.database.repository.PostRepository
import com.example.hatestuff3.data.local.database.repository.UserRepository

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Caso 1: Si piden el AuthViewModel (Login/Registro)
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userRepository) as T
        }

        // Caso 2: Si piden el PostViewModel
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postRepository) as T
        }

        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
