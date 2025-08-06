package com.osiel.gymflow.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.osiel.gymflow.data.repository.AuthRepositoryImpl
import com.osiel.gymflow.data.repository.ExerciseRepositoryImpl
import com.osiel.gymflow.data.repository.WorkoutRepositoryImpl
import com.osiel.gymflow.domain.repository.AuthRepository
import com.osiel.gymflow.domain.repository.ExerciseRepository
import com.osiel.gymflow.domain.repository.WorkoutRepository
import com.osiel.gymflow.presentation.viewmodel.AuthViewModel
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutViewModel
import com.osiel.gymflow.presentation.viewmodel.ExerciseViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get(), get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get(), get(), get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { WorkoutViewModel(get()) }
    viewModel { CrudWorkoutViewModel(get()) }
    viewModel { ExerciseViewModel(get()) }
}
