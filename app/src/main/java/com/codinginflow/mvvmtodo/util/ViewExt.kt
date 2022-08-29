package com.codinginflow.mvvmtodo.util

import androidx.appcompat.widget.SearchView
/**
 * Данная функция расширяет функционал SearchView. Фишка Kotlin'а - расширение чужих классов, написанием
 * static методов.
 * */
inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }


        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}