package com.osiel.gymflow.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.osiel.gymflow.domain.model.Exercicio
import com.osiel.gymflow.domain.repository.ExerciseRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ExerciseRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ExerciseRepository {

    override suspend fun getExercisesForWorkout(workoutId: String): List<Exercicio> {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        return firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .get()
            .await()
            .map { it.toObject(Exercicio::class.java).copy(id = it.id) }
    }

    override suspend fun createExercise(workoutId: String, name: String, obs: String, imageUri: Uri?): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
            var downloadUrl: String? = null
            if (imageUri != null) {
                val imageRef = storage.reference.child("exercise_images/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                downloadUrl = imageRef.downloadUrl.await().toString()
            }
            val newExercise = Exercicio(
                nome = name,
                observacoes = obs,
                imagemUrl = downloadUrl
            )
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .document(workoutId)
                .collection("exercises")
                .add(newExercise)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExercise(workoutId: String, exercise: Exercicio) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .document(exercise.id)
            .set(exercise)
            .await()
    }

    override suspend fun deleteExercise(workoutId: String, exerciseId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val exerciseDocRef = firestore.collection("users")
            .document(userId)
            .collection("workouts")
            .document(workoutId)
            .collection("exercises")
            .document(exerciseId)

        try {
            val document = exerciseDocRef.get().await()
            val imageUrl = document.getString("imagemUrl")
            if (!imageUrl.isNullOrEmpty()) {
                try {
                    val storageRef = storage.getReferenceFromUrl(imageUrl)
                    storageRef.delete().await()
                } catch (e: Exception) {
                    Log.e("ExerciseRepository", "Erro ao deletar imagem do Storage: ${e.message}")
                }
            }
            exerciseDocRef.delete().await()
        } catch (e: Exception) {
            throw e
        }
    }
}
