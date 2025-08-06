package com.osiel.gymflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CrudWorkoutState {
    object Idle : CrudWorkoutState()
    object Loading : CrudWorkoutState()
    object Success : CrudWorkoutState()
    data class Error(val message: String) : CrudWorkoutState()
}

class CrudWorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CrudWorkoutState>(CrudWorkoutState.Idle)
    val state: StateFlow<CrudWorkoutState> = _state

    fun createWorkout(name: String, description: String) {
        if (name.isBlank()) {
            _state.value = CrudWorkoutState.Error("Nome não pode estar vazio")
            return
        }

        val workout = Treino(
            id = "",
            nome = name,
            descricao = description,
            data = Timestamp.now()
        )

        _state.value = CrudWorkoutState.Loading

        viewModelScope.launch {
            try {
                repository.createWorkout(workout)
                _state.value = CrudWorkoutState.Success
            } catch (e: Exception) {
                _state.value = CrudWorkoutState.Error(e.message ?: "Erro ao salvar")
            }
        }
    }

    fun resetState() {
        _state.value = CrudWorkoutState.Idle
    }
}

