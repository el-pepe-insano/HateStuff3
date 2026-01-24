package com.example.hatestuff3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.domain.validation.validateConfirm
import com.example.hatestuff3.domain.validation.validateEmail
import com.example.hatestuff3.domain.validation.validateNameLettersOnly
import com.example.hatestuff3.domain.validation.validateStringPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



data class LoginUiState(
    //campos del formulario
    val email: String = "",
    val pass: String = "",
    //mostrar errores de cada campo del formulario
    val emailError: String? = null,
    val passError: String? = null,
    //variable para error global del formulario. Ejemplo: credenciales incorrectas
    val errorMsg: String? = null,
    //variables para manejar los estados del formulario
    val isSubmitting: Boolean = false, //saber si el formulario se esta ejecutando
    val canSubmit: Boolean = false, //saber si el botón está activo
    val success: Boolean= false //saber si el formulario se ejcuto de manera correcta
)

data class RegisterUiState(
    //campos del formulario
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",
    //mostrar errores de cada campo del formulario
    val nameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    //variable para error global del formulario. Ejemplo: correo ya registrado
    val errorMsg: String? = null,
    //variables para manejar los estados del formulario
    val isSubmitting: Boolean = false, //saber si el formulario se esta ejecutando
    val canSubmit: Boolean = false, //saber si el botón está activo
    val success: Boolean= false //saber si el formulario se ejcuto de manera correcta
)



class AuthViewModel(
    //repositorio que va a usar
    private val repository: UserRepository
): ViewModel(){
    //persistir la coleccion de usuarios para todas las diferentes instancias de este archivo

    //variables para manejar los flujos de estado desde la UI
    private val _login = MutableStateFlow(LoginUiState()) //estado interno para el login
    val login: StateFlow<LoginUiState> = _login //copia de la anterior para que se pueda ver sus datos

    private val _register = MutableStateFlow(RegisterUiState()) //estado interno para el registro
    val register: StateFlow<RegisterUiState> = _register //copia de la anterior para que se pueda ver sus datos

    //funciones de manejo de los formularios
    //Login

    //para habilitar/deshabilitar boton iniciar sesion
    private fun recomputeLoginCanSubmit(){
        val s = _login.value
        // Verifica que NO haya error de email y que ambos campos tengan texto
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    //unir las validaciones a sus respectivos campos
    fun onLoginEmailChange(value: String){
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }
    fun onLoginPassChange(value: String){
        // Cambia it.copy(email = value) por it.copy(pass = value)
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    fun submitLogin(){
        val s = _login.value //guardo os datos actuales del formulario
        //verifico si ya se esta ejecutando para no ejecutar denuevo
        if(!s.canSubmit || s.isSubmitting) return
        //ejecuto una corutina
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(2000)
            //buscar en memoria si los datos son correctos
            val result = repository.login(s.email.trim(),s.pass)

            //actualizamos los datos del state del formulario
            _login.update {
                if (result.isSuccess){
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                }else{
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "error de autenticacion")
                }
            }

        }
    }

    //limpiar campos al finalizar formulario
    fun clearLoginResult(){
        _login.update { it.copy(success = false, errorMsg = "") }
    }


    //REGISTRO
    //para habilitar/deshabilitar boton registrar
    private fun recomputeRegisterCanSubmit(){
        val s = _register.value //obtenemos todos los datos actuales del formulario
        val noErrors = listOf(s.nameError,s.emailError,
            s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank()
                && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }
    fun onNameChange(value: String){
        val x = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(name = x, nameError = validateNameLettersOnly(x))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterEmailChange(value:String){
        _register.update {
            it.copy(email = value, emailError = validateEmail(value))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(
            pass = value,
            passError = validateStringPassword(value)
        )}
        // Después de actualizar la pass, validamos si la confirmación sigue coincidiendo
        _register.update {
            it.copy(confirmError = validateConfirm(it.pass, it.confirm))
        }
        recomputeRegisterCanSubmit()
    }
    fun onConfirmChange(value: String){
        _register.update {
            it.copy(confirm = value, confirmError = validateConfirm(it.pass, value))
        }
        recomputeRegisterCanSubmit()
    }
    fun submitRegister(){
        val s = _register.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(2000)
            //existe el usuario????
            val result = repository.register(
                name = s.name,
                email = s.email,
                password = s.pass
            )
            _register.update {
                if (result.isSuccess){
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                }else{
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "no se pudo registrar")
                }

            }
        }
    }



}
