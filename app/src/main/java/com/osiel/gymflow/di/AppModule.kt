package com.osiel.gymflow.di

import com.google.firebase.auth.FirebaseAuth
import com.osiel.gymflow.data.repository.AuthRepositoryImpl
import com.osiel.gymflow.data.repository.WorkoutRepositoryImpl
import com.osiel.gymflow.domain.repository.AuthRepository
import com.osiel.gymflow.domain.repository.WorkoutRepository
import com.osiel.gymflow.presentation.viewmodel.AuthViewModel
import com.osiel.gymflow.presentation.viewmodel.WorkoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { AuthViewModel(get()) }

    single<WorkoutRepository> { WorkoutRepositoryImpl(get(), get()) }
    viewModel { WorkoutViewModel(get()) }
}