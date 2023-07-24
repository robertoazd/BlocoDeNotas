package br.com.alura.blocodenotas.webclient.model

data class NotaRequisicao(
    val tituto: String,
    val descricao: String,
    val imagem: String? = null
)
