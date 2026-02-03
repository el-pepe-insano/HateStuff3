package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.local.database.user.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val allUsers: StateFlow<List<UserEntity>> = _users.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // CORRECCIÓN: Renombramos la función y quitamos el init
    fun fetchAllUsers(adminRole: String) {
        viewModelScope.launch {
            // Por ahora, 'adminRole' no se usa en la llamada, pero se podría añadir
            // si la API requiriera un Header de autorización, por ejemplo.
            val result = repository.getAllUsers()
            result.onSuccess { userList ->
                _users.value = userList
            }.onFailure {
                _statusMessage.value = "Error al cargar usuarios: ${it.message}"
            }
        }
    }

    fun deleteUser(user: UserEntity, adminRole: String) {
        viewModelScope.launch {
            val result = repository.deleteUser(user.id, adminRole)
            if (result.isSuccess) {
                fetchAllUsers(adminRole) // Recargamos la lista
                _statusMessage.value = "Usuario eliminado"
            } else {
                _statusMessage.value = "Error al eliminar (Verifica permisos)"
            }
        }
    }

    fun updateRole(user: UserEntity, newRole: String, adminRole: String) {
        viewModelScope.launch {
            val result = repository.updateUserRole(user.id, newRole, adminRole)
            if (result.isSuccess) {
                fetchAllUsers(adminRole) // Recargamos la lista
                _statusMessage.value = "Rol actualizado a $newRole"
            } else {
                _statusMessage.value = "Error al actualizar rol (Verifica permisos)"
            }
        }
    }

    fun clearMessage() { _statusMessage.value = null }
}