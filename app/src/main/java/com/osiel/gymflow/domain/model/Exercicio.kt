package com.osiel.gymflow.domain.model

data class Exercicio(
    val id: String = "",
    val nome: String = "",
    val imagemUrl: String? = null,
    val observacoes: String = ""
)