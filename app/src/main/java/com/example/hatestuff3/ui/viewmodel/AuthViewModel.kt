package com.example.hatestuff3.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.user.UserDao
import com.example.hatestuff3.data.local.database.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado unificado para mantener la UI sincronizada
data class AuthState(
    // Campos de Login
    val loginEmail: String = "",
    val loginPass: String = "",
    val loginError: String? = null,

    // Campos de Registro
    val regName: String = "",
    val regNameError: String? = null,
    val regEmail: String = "",
    val regEmailError: String? = null,
    val regPass: String = "",
    val regPassError: String? = null,
    val regConfirm: String = "",
    val regConfirmError: String? = null,

    // Estados generales
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false
)

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    // --- EVENTOS DE CAMBIO DE TEXTO (Limpian errores al escribir) ---
    fun onLoginEmailChange(text: String) {
        _state.update { it.copy(loginEmail = text, loginError = null) }
    }
    fun onLoginPassChange(text: String) {
        _state.update { it.copy(loginPass = text, loginError = null) }
    }
    fun onRegNameChange(text: String) {
        _state.update { it.copy(regName = text, regNameError = null) }
    }
    fun onRegEmailChange(text: String) {
        _state.update { it.copy(regEmail = text, regEmailError = null) }
    }
    fun onRegPassChange(text: String) {
        _state.update { it.copy(regPass = text, regPassError = null) }
    }
    fun onRegConfirmChange(text: String) {
        _state.update { it.copy(regConfirm = text, regConfirmError = null) }
    }

    // --- LOGIN ---
    fun login() {
        val email = _state.value.loginEmail.trim()
        val pass = _state.value.loginPass.trim()

        if (email.isBlank() || pass.isBlank()) {
            _state.update { it.copy(loginError = "Por favor llena todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = userDao.getUserByEmail(email)
                if (user != null && user.password == pass) {
                    _currentUser.value = user
                    _state.update { it.copy(isLoginSuccess = true, isLoading = false) }
                } else {
                    _state.update { it.copy(loginError = "Credenciales incorrectas", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loginError = "Error al conectar", isLoading = false) }
            }
        }
    }

    // --- REGISTRO CON VALIDACIONES Y ROLES ---
    fun register() {
        val s = _state.value
        var hasError = false

        // Validaciones
        if (s.regName.isBlank()) {
            _state.update { it.copy(regNameError = "El nombre es obligatorio") }
            hasError = true
        }
        if (s.regEmail.isBlank()) {
            _state.update { it.copy(regEmailError = "El correo es obligatorio") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(s.regEmail).matches()) {
            _state.update { it.copy(regEmailError = "Formato de correo inválido") }
            hasError = true
        }
        if (s.regPass.length < 4) {
            _state.update { it.copy(regPassError = "Mínimo 4 caracteres") }
            hasError = true
        }
        if (s.regPass != s.regConfirm) {
            _state.update { it.copy(regConfirmError = "Las contraseñas no coinciden") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val existing = userDao.getUserByEmail(s.regEmail.trim())
                if (existing != null) {
                    _state.update { it.copy(regEmailError = "Este correo ya existe", isLoading = false) }
                } else {
                    // --- LÓGICA DE ROLES MEJORADA ---
                    val emailLower = s.regEmail.trim().lowercase()
                    val assignedRole = when {
                        emailLower.contains("admin") -> "ADMIN"
                        emailLower.contains("mod") -> "MOD" // <--- Detección de Moderadores
                        else -> "USER"
                    }

                    val newUser = UserEntity(
                        name = s.regName.trim(),
                        email = s.regEmail.trim(),
                        password = s.regPass,
                        role = assignedRole,
                        bio = "Nuevo usuario",
                        profilePictureUri = null
                    )

                    userDao.insertUser(newUser)

                    // Iniciamos sesión automáticamente tras el registro
                    _currentUser.value = userDao.getUserByEmail(s.regEmail.trim())
                    _state.update { it.copy(isRegisterSuccess = true, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _state.value = AuthState()
    }

    fun clearStates() {
        _state.value = AuthState()
    }

    fun updateProfile(userId: Long, newBio: String, newPhotoUri: String?) {
        viewModelScope.launch {
            try {
                userDao.updateUserProfile(userId, newBio, newPhotoUri)
                _currentUser.value?.email?.let { email ->
                    val updatedUser = userDao.getUserByEmail(email)
                    _currentUser.value = updatedUser
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}