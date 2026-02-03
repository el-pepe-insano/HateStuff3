package com.example.hatestuff3.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.local.database.user.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel // IMPORTANTE PARA HILT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject // IMPORTANTE PARA HILT

data class AuthState(
    val loginEmail: String = "",
    val loginPass: String = "",
    val loginError: String? = null,

    val regName: String = "",
    val regNameError: String? = null,
    val regEmail: String = "",
    val regEmailError: String? = null,
    val regPass: String = "",
    val regPassError: String? = null,
    val regConfirm: String = "",
    val regConfirmError: String? = null,

    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false
)

@HiltViewModel // 1. Etiqueta necesaria para que Android sepa crear este ViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    // 2. NUEVO: Estado específico para el Rol (para ocultar/mostrar botones)
    private val _currentUserRole = MutableStateFlow("USER")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    // --- MANEJO DE TEXTO EN UI ---
    fun onLoginEmailChange(text: String) { _state.update { it.copy(loginEmail = text, loginError = null) } }
    fun onLoginPassChange(text: String) { _state.update { it.copy(loginPass = text, loginError = null) } }
    fun onRegNameChange(text: String) { _state.update { it.copy(regName = text, regNameError = null) } }
    fun onRegEmailChange(text: String) { _state.update { it.copy(regEmail = text, regEmailError = null) } }
    fun onRegPassChange(text: String) { _state.update { it.copy(regPass = text, regPassError = null) } }
    fun onRegConfirmChange(text: String) { _state.update { it.copy(regConfirm = text, regConfirmError = null) } }

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

            val result = repository.login(email, pass)

            result.onSuccess { user ->
                _currentUser.value = user

                // 3. CAPTURA DEL ROL:
                // Si el backend manda null, asumimos "USER".
                val roleFromServer = user.role ?: "USER"
                _currentUserRole.value = roleFromServer

                _state.update { it.copy(isLoginSuccess = true, isLoading = false) }
            }.onFailure {
                _state.update { it.copy(loginError = "Credenciales incorrectas o error de red", isLoading = false) }
            }
        }
    }

    // --- REGISTRO ---
    fun register() {
        val s = _state.value
        if (!validateRegister(s)) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Lógica "hack" para crear Admins fácil (mantenemos tu lógica, ¡es útil!)
            val emailLower = s.regEmail.trim().lowercase()
            val assignedRole = when {
                emailLower.contains("admin") -> "ADMIN"
                emailLower.contains("mod") -> "MODERADOR" // Ajusté a "MODERADOR" para ser explícito
                else -> "USER"
            }

            val newUser = UserEntity(
                name = s.regName.trim(),
                email = s.regEmail.trim(),
                password = s.regPass,
                role = assignedRole, // Enviamos el rol calculado al backend
                bio = "Nuevo usuario",
                profilePictureUri = null
            )

            val result = repository.register(newUser)

            result.onSuccess { registeredUser ->
                _currentUser.value = registeredUser
                // También actualizamos el rol aquí por si entra directo
                _currentUserRole.value = registeredUser.role ?: "USER"

                _state.update { it.copy(isRegisterSuccess = true, isLoading = false) }
            }.onFailure {
                _state.update { it.copy(regEmailError = "Error al registrar (posible duplicado)", isLoading = false) }
            }
        }
    }

    private fun validateRegister(s: AuthState): Boolean {
        var isValid = true
        if (s.regName.isBlank()) { _state.update { it.copy(regNameError = "Nombre obligatorio") }; isValid = false }
        if (s.regEmail.isBlank()) { _state.update { it.copy(regEmailError = "Correo obligatorio") }; isValid = false }
        else if (!Patterns.EMAIL_ADDRESS.matcher(s.regEmail).matches()) { _state.update { it.copy(regEmailError = "Correo inválido") }; isValid = false }
        if (s.regPass.length < 4) { _state.update { it.copy(regPassError = "Mínimo 4 caracteres") }; isValid = false }
        if (s.regPass != s.regConfirm) { _state.update { it.copy(regConfirmError = "No coinciden") }; isValid = false }
        return isValid
    }

    fun logout() {
        _currentUser.value = null
        _currentUserRole.value = "USER" // Resetear rol al salir
        _state.value = AuthState()
    }

    fun clearStates() {
        _state.value = AuthState()
    }

    // --- ACTUALIZAR PERFIL ---
    fun updateUserProfile(userId: Long, newName: String, newBio: String, newAvatarUri: String?) {
        viewModelScope.launch {
            val result = repository.updateProfile(userId, newBio, newAvatarUri)

            result.onSuccess {
                val current = _currentUser.value
                if (current != null) {
                    _currentUser.value = current.copy(name = newName, bio = newBio, profilePictureUri = newAvatarUri)
                }
            }
        }
    }

    fun getUserPublicInfo(userName: String): Flow<UserEntity?> = flow { emit(null) }
}