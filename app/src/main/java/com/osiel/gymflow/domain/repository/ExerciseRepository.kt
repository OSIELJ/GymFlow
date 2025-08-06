package com.osiel.gymflow.domain.repository

import android.net.Uri
import com.osiel.gymflow.domain.model.Exercicio

interface ExerciseRepository {
    suspend fun getExercisesForWorkout(workoutId: String): List<Exercicio>
    suspend fun createExercise(workoutId: String, name: String, obs: String, imageUri: Uri?): Result<Unit>
    suspend fun updateExercise(workoutId: String, exercise: Exercicio)
    suspend fun deleteExercise(workoutId: String, exerciseId: String)
    suspend fun getExercisesForSuggestedWorkout(workoutId: String): List<Exercicio>
}