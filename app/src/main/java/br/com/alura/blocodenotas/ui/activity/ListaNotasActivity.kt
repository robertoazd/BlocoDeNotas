package br.com.alura.blocodenotas.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.alura.blocodenotas.database.AppDatabase
import br.com.alura.blocodenotas.databinding.ActivityListaNotasBinding
import br.com.alura.blocodenotas.extensions.vaiPara
import br.com.alura.blocodenotas.repository.NotaRepository
import br.com.alura.blocodenotas.ui.recyclerview.adapter.ListaNotasAdapter
import br.com.alura.blocodenotas.webclient.NotaWebClient
import kotlinx.coroutines.launch

class ListaNotasActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaNotasBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        ListaNotasAdapter(this)
    }
    private val repository by lazy {
        NotaRepository(
            AppDatabase.instancia(this).notaDao(),
            NotaWebClient()
        )
    }

//    private val dao by lazy {
//        AppDatabase.instancia(this).notaDao()
//    }
//    private val webClient by lazy {
//        NotaWebClient()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraFab()
        configuraRecyclerView()
        configuraSwipeRefresh()
        lifecycleScope.launch {
            launch {
                sincroniza()
            }
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                buscaNotas()
            }
        }
//        retrofitSemCoroutines()
    }

    private fun configuraSwipeRefresh() {
        binding.activityListaNotasSwipe.setOnRefreshListener {
            lifecycleScope.launch {
                sincroniza()
                binding.activityListaNotasSwipe.isRefreshing = false
            }
        }
    }

    private suspend fun sincroniza() {
        repository.sincroniza()
    }

    private fun retrofitSemCoroutines() {
        //        val call: Call<List<NotaResposta>> = RetrofitInicializador().notaService.buscaTodas()

        // Sincrona (execute)
        //        lifecycleScope.launch(IO) {
        //            val resposta: Response<List<NotaResposta>> = call.execute()
        //            resposta.body()?.let { notasResposta ->
        //                val notas: List<Nota> = notasResposta.map {
        //                    it.nota
        //                }
        //                Log.i("ListaNotas", "onCreate: $notas")
        //            }
        //        }

        // Assincrona (enqueue)
        //        call.enqueue(object : Callback<List<NotaResposta>?> {
        //            override fun onResponse(
        //                call: Call<List<NotaResposta>?> ,
        //                resposta: Response<List<NotaResposta>?>
        //            ) {
        //                resposta.body()?.let { notasResposta ->
        //                    val notas: List<Nota> = notasResposta.map {
        //                        it.nota
        //                    }
        //                    Log.i("ListaNotas", "onCreate: $notas")
        //                }
        //            }
        //
        //            override fun onFailure(call: Call<List<NotaResposta>?> , t: Throwable) {
        //                Log.e("ListaNotas", "onFailure: ", t)
        //            }
        //        })
    }

    private fun configuraFab() {
        binding.activityListaNotasFab.setOnClickListener {
            Intent(this, FormNotaActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun configuraRecyclerView() {
        binding.activityListaNotasRecyclerview.adapter = adapter
        adapter.quandoClicaNoItem = { nota ->
            vaiPara(FormNotaActivity::class.java) {
                putExtra(NOTA_ID, nota.id)
            }
        }
    }

    private suspend fun buscaNotas() {
        repository.buscaTodos()
            .collect { notasEncontradas ->
                binding.activityListaNotasMensagemSemNotas.visibility =
                    if (notasEncontradas.isEmpty()) {
                        binding.activityListaNotasRecyclerview.visibility = GONE
                        VISIBLE
                    } else {
                        binding.activityListaNotasRecyclerview.visibility = VISIBLE
                        adapter.atualiza(notasEncontradas)
                        GONE
                    }
            }
    }
}