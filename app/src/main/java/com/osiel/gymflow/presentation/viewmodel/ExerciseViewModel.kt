package com.osiel.gymflow.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osiel.gymflow.domain.model.Exercicio
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ExerciseState {
    object Idle : ExerciseState()
    object Loading : ExerciseState()
    data class Success(val message: String = "") : ExerciseState()
    data class Error(val message: String) : ExerciseState()
}


class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercicio>>(emptyList())
    val exercises: StateFlow<List<Exercicio>> = _exercises.asStateFlow()

    private val _state = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val state: StateFlow<ExerciseState> = _state.asStateFlow()


    fun loadExercises(workoutId: String, isSuggested: Boolean) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                val exercisesList = if (isSuggested) {
                    repository.getExercisesForSuggestedWorkout(workoutId)
                } else {
                    repository.getExercisesForWorkout(workoutId)
                }
                _exercises.value = exercisesList
                _state.value = ExerciseState.Idle
            } catch (e: Exception) {
                _state.value = ExerciseState.Error("Erro ao carregar exercícios")
            }
        }
    }


    fun createExercise(workoutId: String, name: String, obs: String, imageUri: Uri?) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            if (name.isEmpty()) {
                _state.value = ExerciseState.Error("O nome do exercício é obrigatório.")
                return@launch
            }
            val result = repository.createExercise(workoutId, name, obs, imageUri)
            result.onSuccess {
                loadExercises(workoutId, isSuggested = false)
                _state.value = ExerciseState.Success("Exercício adicionado com sucesso")
            }.onFailure { exception ->
                _state.value = ExerciseState.Error(exception.message ?: "Erro ao adicionar exercício")
            }
        }
    }

    fun deleteExercise(workoutId: String, exerciseId: String) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                repository.deleteExercise(workoutId, exerciseId)
                loadExercises(workoutId, isSuggested = false)
                _state.value = ExerciseState.Success("Exercício removido com sucesso")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error("Erro ao excluir exercício")
            }
        }
    }

    fun updateExercise(workoutId: String, exercise: Exercicio) {
        viewModelScope.launch {
            _state.value = ExerciseState.Loading
            try {
                repository.updateExercise(workoutId, exercise)
                loadExercises(workoutId, isSuggested = false)
                _state.value = ExerciseState.Success("Exercício atualizado")
            } catch (e: Exception) {
                _state.value = ExerciseState.Error("Erro ao atualizar exercício")
            }
        }
    }

    fun resetState() {
        _state.value = ExerciseState.Idle
    }
}