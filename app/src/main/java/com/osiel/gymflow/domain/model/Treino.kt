package com.osiel.gymflow.domain.model

import com.google.firebase.Timestamp

data class Treino(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val data: Timestamp = Timestamp.now()
)

