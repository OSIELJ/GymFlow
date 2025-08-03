package com.osiel.gymflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osiel.gymflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> get() = _state

    fun login(email: String, password: String) {
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _state.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
        }
    }

    fun register(email: String, password: String) {
        _state.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.register(email, password)
            _state.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
        }
    }

    fun logout() = repository.logout()

    fun isUserLoggedIn(): Boolean = repository.isUserLoggedIn()
}