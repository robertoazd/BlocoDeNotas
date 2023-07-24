package br.com.alura.blocodenotas.webclient

import android.util.Log
import br.com.alura.blocodenotas.model.Nota
import br.com.alura.blocodenotas.webclient.model.NotaRequisicao
import br.com.alura.blocodenotas.webclient.services.NotaService

private const val TAG = "NotaWebClient"

class NotaWebClient {

    private val notaService: NotaService =
        RetrofitInicializador().notaService

    suspend fun buscatodos(): List<Nota>? {
        return try {
            val notasResposta = notaService
                .buscaTodas()
            notasResposta.map { notaResposta ->
                notaResposta.nota
            }
        } catch (e: Exception) {
            Log.e(TAG,"buscaTodos: ", e)
            null
        }
    }

    suspend fun salvar(nota: Nota): Boolean {
        try {
            val resposta = notaService.salva(nota.id, NotaRequisicao(
                tituto = nota.titulo,
                descricao = nota.descricao,
                imagem = nota.imagem
            ))
            return resposta.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "salva: Falha ao tentar salvar", e)
        }
        return false
    }

    suspend fun remove(id: String): Boolean {
        try {
            notaService.remove(id)
            return true
        } catch (e: Exception) {
            Log.e(TAG , "remove: falha ao tentar remover nota" , e)
        }
        return false
    }

}