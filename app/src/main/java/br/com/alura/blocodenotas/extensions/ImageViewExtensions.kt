package br.com.alura.blocodenotas.extensions

import android.widget.ImageView
import br.com.alura.blocodenotas.R
import coil.load

fun ImageView.tentaCarregarImagem(
    url: String? = null,
    fallback: Int = R.drawable.imagem_padrao
) {
    load(url) {
        placeholder(R.drawable.placeholder)
        error(R.drawable.erro)
        fallback(fallback)
    }
}