package com.osiel.gymflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osiel.gymflow.domain.repository.WorkoutRepository
import com.osiel.gymflow.domain.model.Treino
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _userWorkouts = MutableStateFlow<List<Treino>>(emptyList())
    val userWorkouts: StateFlow<List<Treino>> = _userWorkouts

    private val _suggestedWorkouts = MutableStateFlow<List<Treino>>(emptyList())
    val suggestedWorkouts: StateFlow<List<Treino>> = _suggestedWorkouts


    private val _selectedWorkout = MutableStateFlow<Treino?>(null)
    val selectedWorkout: StateFlow<Treino?> = _selectedWorkout.asStateFlow()

    fun selectWorkout(workout: Treino) {
        _selectedWorkout.value = workout
    }

    fun clearSelectedWorkout() {
        _selectedWorkout.value = null
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