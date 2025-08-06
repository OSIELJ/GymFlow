package com.osiel.gymflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class SelectedWorkout {
    object None : SelectedWorkout()
    data class UserWorkout(val treino: Treino) : SelectedWorkout()
    data class SuggestedWorkout(val treino: Treino) : SelectedWorkout()
}

class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _userWorkouts = MutableStateFlow<List<Treino>>(emptyList())
    val userWorkouts: StateFlow<List<Treino>> = _userWorkouts.asStateFlow()

    private val _suggestedWorkouts = MutableStateFlow<List<Treino>>(emptyList())
    val suggestedWorkouts: StateFlow<List<Treino>> = _suggestedWorkouts.asStateFlow()

    // --- MUDANÇA 2: O StateFlow agora guarda um objeto 'SelectedWorkout' ---
    private val _selectedWorkout = MutableStateFlow<SelectedWorkout>(SelectedWorkout.None)
    val selectedWorkout: StateFlow<SelectedWorkout> = _selectedWorkout.asStateFlow()

    // --- MUDANÇA 3: A função de selecionar agora diferencia o tipo de treino ---
    fun selectWorkout(workout: Treino, isSuggested: Boolean) {
        if (isSuggested) {
            _selectedWorkout.value = SelectedWorkout.SuggestedWorkout(workout)
        } else {
            _selectedWorkout.value = SelectedWorkout.UserWorkout(workout)
        }
    }

    // --- MUDANÇA 4: A função de limpar agora reseta para o estado 'None' ---
    fun clearSelectedWorkout() {
        _selectedWorkout.value = SelectedWorkout.None
    }

    init {
        loadWorkouts()
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            try {
                _userWorkouts.value = repository.getUserWorkouts()
                _suggestedWorkouts.value = repository.getSuggestedWorkouts()
            } catch (e: Exception) {
                _userWorkouts.value = emptyList()
                _suggestedWorkouts.value = emptyList()
            }
        }
    }
}