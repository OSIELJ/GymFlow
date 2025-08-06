package com.osiel.gymflow.domain.repository

import com.osiel.gymflow.domain.model.Treino

interface WorkoutRepository {
    suspend fun getUserWorkouts(): List<Treino>
    suspend fun getSuggestedWorkouts(): List<Treino>
    suspend fun createWorkout(workout: Treino)
}