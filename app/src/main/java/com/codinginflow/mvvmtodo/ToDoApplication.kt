package com.codinginflow.mvvmtodo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Данный класс необходим для инициализации Hilt'а.
 * */
@HiltAndroidApp
class ToDoApplication: Application() {
}