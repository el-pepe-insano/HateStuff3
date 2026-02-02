package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.local.database.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: UserRepository) : ViewModel() {

    // Estado interno (Lista mutable)
    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    // Estado público (Solo lectura para la UI)
    val allUsers: StateFlow<List<UserEntity>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    // Función para descargar la lista desde el servidor
    fun loadUsers() {
        viewModelScope.launch {
            val result = repository.getAllUsers()
            result.onSuccess { lista ->
                _users.value = lista
            }
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            val result = repository.deleteUser(user.id)
            if (result.isSuccess) {
                // Si se borró con éxito en el servidor, recargamos la lista
                loadUsers()
            }
        }
    }

    fun updateRole(user: UserEntity, newRole: String) {
        viewModelScope.launch {
            val result = repository.updateUserRole(user.id, newRole)
            if (result.isSuccess) {
                loadUsers()
            }
        }
    }
}