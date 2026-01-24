package com.example.hatestuff3.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hatestuff3.data.local.database.repository.UserRepository

class AuthViewModelFactory (
    private val repository: UserRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //cree el viewmodel con el parametro
        if(modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(repository) as T
        }
        //si es otro viewmodel que no usa parametros
        throw IllegalArgumentException("Uknow ViewModel class: ${modelClass.name}")
    }
}
