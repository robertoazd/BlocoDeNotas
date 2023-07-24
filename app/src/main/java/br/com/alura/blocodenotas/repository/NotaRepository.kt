package br.com.alura.blocodenotas.repository

import br.com.alura.blocodenotas.database.dao.NotaDao
import br.com.alura.blocodenotas.model.Nota
import br.com.alura.blocodenotas.webclient.NotaWebClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NotaRepository(
    private val dao: NotaDao,
    private val webClient: NotaWebClient
    ){

    fun buscaTodos(): Flow<List<Nota>> {
        return dao.buscaTodas()
    }

    private suspend fun atualizaTodas() {
        webClient.buscatodos()?.let { notas ->
            val notasSincronizadas = notas.map { nota ->
                nota.copy(sincronizada = true)
            }
            dao.salva(notasSincronizadas)
        }
    }

    fun buscaPorId(id: String): Flow<Nota> {
        return dao.buscaPorId(id)
    }

    suspend fun remove(id: String) { 
        dao.desativa(id)
        if (webClient.remove(id)) {
            dao.remove(id)
        }
    }

    suspend fun salva(nota: Nota) {
        dao.salva(nota)
        if (webClient.salvar(nota)) {
            val notaSincronizada = nota.copy(sincronizada = true)
            dao.salva(notaSincronizada)
        }
    }

    suspend fun sincroniza() {
        val notasDesativadas = dao.buscaDesativadas().first()
        notasDesativadas.forEach { notasDesativada ->
            remove(notasDesativada.id)
        }
        val notasNaoSincronizadas = dao.buscaNãoSincronizadas().first()
        notasNaoSincronizadas.forEach { notaNaoSincronizada ->
            salva(notaNaoSincronizada)
        }
        atualizaTodas()
    }
}