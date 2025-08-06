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

    // Create workout
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

    // Edit workout
    fun updateWorkout(workout: Treino) {
        if (workout.nome.isBlank()) {
            _state.value = CrudWorkoutState.Error("Nome não pode estar vazio")
            return
        }

        _state.value = CrudWorkoutState.Loading

        viewModelScope.launch {
            try {
                repository.updateWorkout(workout)
                _state.value = CrudWorkoutState.Success
            } catch (e: Exception) {
                _state.value = CrudWorkoutState.Error(e.message ?: "Erro ao atualizar")
            }
        }
    }

    // Delete workout
    fun deleteWorkout(workoutId: String) {
        _state.value = CrudWorkoutState.Loading

        viewModelScope.launch {
            try {
                repository.deleteWorkout(workoutId)
                _state.value = CrudWorkoutState.Success
            } catch (e: Exception) {
                _state.value = CrudWorkoutState.Error(e.message ?: "Erro ao excluir")
            }
        }
    }

    fun resetState() {
        _state.value = CrudWorkoutState.Idle
    }
}
