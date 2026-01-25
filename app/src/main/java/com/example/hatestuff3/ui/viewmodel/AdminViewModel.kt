package com.example.hatestuff3.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.user.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminViewModel(private val userDao: UserDao) : ViewModel() {

    // Obtenemos todos los usuarios registrados
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    // Función para borrar un usuario (Baneo)
    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.deleteUser(user)
        }
    }

    // Función para cambiar el rol (Ascender/Degradar)
    fun updateRole(user: UserEntity, newRole: String) {
        viewModelScope.launch {
            userDao.updateUser(user.copy(role = newRole))
        }
    }
}