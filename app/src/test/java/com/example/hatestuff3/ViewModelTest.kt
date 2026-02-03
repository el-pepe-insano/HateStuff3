package com.example.hatestuff3

import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner

@org.robolectric.annotation.Config(manifest = org.robolectric.annotation.Config.NONE)
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Redirige el hilo principal a nuestro dispatcher de pruebas
        Dispatchers.setMain(testDispatcher)
        userRepository = mock(UserRepository::class.java)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        // Resetea el hilo principal al finalizar
        Dispatchers.resetMain()
    }

    @Test
    fun login_ok_con_credenciales_validas() = runTest {
        // Arrange
        val user = UserEntity(1, "test", "a@a.cl", "1234", "USER", "", "")
        `when`(userRepository.login("a@a.cl", "1234")).thenReturn(Result.success(user))

        // Act
        viewModel.onLoginEmailChange("a@a.cl")
        viewModel.onLoginPassChange("1234")
        viewModel.login()

        // Assert
        assertEquals(true, viewModel.state.value.isLoginSuccess)
        assertNull(viewModel.state.value.loginError)
        assertEquals(user, viewModel.currentUser.value)
    }

    @Test
    fun login_error_con_email_inexistente() = runTest {
        // Arrange
        `when`(userRepository.login("error@a.cl", "1234"))
            .thenReturn(Result.failure(Exception("No existe")))

        // Act
        viewModel.onLoginEmailChange("error@a.cl")
        viewModel.onLoginPassChange("1234")
        viewModel.login()

        // Assert
        assertEquals(false, viewModel.state.value.isLoginSuccess)
        assertNotNull(viewModel.state.value.loginError)
    }

    @Test
    fun login_error_con_campos_vacios() = runTest {
        // Act
        viewModel.onLoginEmailChange("")
        viewModel.onLoginPassChange("")
        viewModel.login()

        // Assert
        assertEquals(false, viewModel.state.value.isLoginSuccess)
        assertEquals("Por favor llena todos los campos", viewModel.state.value.loginError)
    }

    @Test
    fun register_ok_con_datos_validos() = runTest {
        // Arrange
        val user = UserEntity(1, "nombre", "b@b.cl", "1234", "USER", "", null)
        `when`(userRepository.register(any())).thenReturn(Result.success(user))

        // Act
        viewModel.onRegNameChange("nombre")
        viewModel.onRegEmailChange("b@b.cl")
        viewModel.onRegPassChange("1234")
        viewModel.onRegConfirmChange("1234")
        viewModel.register()

        // Assert
        assertEquals(true, viewModel.state.value.isRegisterSuccess)
        assertNull(viewModel.state.value.regEmailError)
        assertEquals(user, viewModel.currentUser.value)
    }

    @Test
    fun register_error_cuando_passwords_no_coinciden() = runTest {
        // Act
        viewModel.onRegNameChange("nombre")
        viewModel.onRegEmailChange("b@b.cl")
        viewModel.onRegPassChange("123")
        viewModel.onRegConfirmChange("999")
        viewModel.register()

        // Assert
        assertEquals(false, viewModel.state.value.isRegisterSuccess)
        assertEquals("No coinciden", viewModel.state.value.regConfirmError)
    }

    @Test
    fun onLoginEmailChange_ok_actualiza_email() {
        val testEmail = "profe@ejemplo.com"

        // Act
        viewModel.onLoginEmailChange(testEmail)

        // Assert
        assertEquals(testEmail, viewModel.state.value.loginEmail)
    }

    @Test
    fun logout_ok_limpia_sesion_y_estado() = runTest {
        // Arrange: Logueamos a un usuario primero
        val user = UserEntity(1, "test", "a@a.cl", "1234", "USER", "", "")
        `when`(userRepository.login("a@a.cl", "1234")).thenReturn(Result.success(user))

        viewModel.onLoginEmailChange("a@a.cl")
        viewModel.onLoginPassChange("1234")
        viewModel.login()

        // Act
        viewModel.logout()

        // Assert
        assertNull(viewModel.currentUser.value)
        assertEquals("", viewModel.state.value.loginEmail)
        assertEquals(false, viewModel.state.value.isLoginSuccess)
    }
}