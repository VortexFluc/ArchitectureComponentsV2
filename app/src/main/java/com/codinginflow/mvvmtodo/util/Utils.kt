package com.codinginflow.mvvmtodo.util

/**
 * Позволяет использовать when как выражение. Таким образом можно обезопасить наш код при использовании в
 * when sealed-класса.
 *
 * (См. TaskFragment.kt
 * */
val <T> T.exhaustive: T
    get() = this