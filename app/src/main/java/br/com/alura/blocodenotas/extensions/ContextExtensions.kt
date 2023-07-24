package br.com.alura.blocodenotas.extensions

import android.content.Context
import android.content.Intent

fun Context.vaiPara(
    clazz: Class<*>,
    intent: Intent.() -> Unit = {}
) {
    Intent(this, clazz).apply {
        intent()
        startActivity(this)
    }
}