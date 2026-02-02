package com.example.hatestuff3


import com.example.hatestuff3.data.local.database.repository.UserRepository
import com.example.hatestuff3.data.local.database.user.UserEntity
import com.example.hatestuff3.ui.viewmodel.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mock(UserRepository::class.java)
        viewModel = AuthViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_ok_con_credenciales_validas() = runTest {
        val user = UserEntity(1, "test", "a@a.cl", "1234", "USER", "", "")
        `when`(userRepository.login("a@a.cl", "1234")).thenReturn(Result.success(user))

        viewModel.onLoginEmailChange("a@a.cl")
        viewModel.onLoginPassChange("1234")
        viewModel.login()

        assertEquals(true, viewModel.state.value.isLoginSuccess)
        assertNull(viewModel.state.value.loginError)
    }

    @Test
    fun register_error_cuando_passwords_no_coinciden() = runTest {
        viewModel.onRegNameChange("nombre")
        viewModel.onRegEmailChange("b@b.cl")
        viewModel.onRegPassChange("123")
        viewModel.onRegConfirmChange("999")
        viewModel.register()

        assertEquals(false, viewModel.state.value.isRegisterSuccess)
        assertEquals("No coinciden", viewModel.state.value.regConfirmError)
    }
}