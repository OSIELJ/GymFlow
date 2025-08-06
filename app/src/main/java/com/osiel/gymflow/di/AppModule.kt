package com.osiel.gymflow.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.osiel.gymflow.data.repository.AuthRepositoryImpl
import com.osiel.gymflow.data.repository.WorkoutRepositoryImpl
import com.osiel.gymflow.domain.repository.AuthRepository
import com.osiel.gymflow.domain.repository.WorkoutRepository
import com.osiel.gymflow.presentation.viewmodel.AuthViewModel
import com.osiel.gymflow.presentation.viewmodel.CrudWorkoutViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<WorkoutRepository> { WorkoutRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { WorkoutViewModel(get()) }
    viewModel { CrudWorkoutViewModel(get()) }
}
