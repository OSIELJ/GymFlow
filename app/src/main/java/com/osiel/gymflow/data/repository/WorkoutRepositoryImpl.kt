package com.osiel.gymflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.osiel.gymflow.domain.model.Treino
import com.osiel.gymflow.domain.repository.WorkoutRepository
import kotlinx.coroutines.tasks.await

class WorkoutRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : WorkoutRepository {

    override suspend fun getUserWorkouts(): List<Treino> {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        return firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .get()
            .await()
            .map { it.toObject(Treino::class.java).copy(id = it.id) }
    }

    override suspend fun getSuggestedWorkouts(): List<Treino> {
        return firestore.collection("suggested_workouts")
            .get()
            .await()
            .map { it.toObject(Treino::class.java).copy(id = it.id) }
    }

    override suspend fun createWorkout(workout: Treino) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .add(workout)
            .await()
    }

    override suspend fun updateWorkout(workout: Treino) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .document(workout.id)
            .set(workout)
            .await()
    }

    override suspend fun deleteWorkout(workoutId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .document(workoutId)
            .delete()
            .await()
    }

}
