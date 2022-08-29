package com.codinginflow.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.createDataStore
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Тэг для логов
 * */
private const val TAG = "PreferencesManager"


/**
 * Класс, создающий абстракцию для Jetpack Data Store над ViewModel.
 * */
@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
            /**
             * Данный обработчик инициализирует пользовательские настройки дефолтными и сообщает об
             * ошибке.
             *
             * Полезные сокращения команд:
             * logt + TAB - пишет строку private const val TAG = "бла-бла-бла"
             * loge + TAB - пишет строку Log.e(...)
             * */
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                // Если preferences[PreferencesKeys.SORT_ORDER] == null, то вернётся SortOrder.BY_DATE.name
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    /**
     * Функция обновления состояния для характера сортировки. (Асинхронка)
     * */
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    /**
     * Функция обновления состояния для сокрытия выполненных Task. (Асинхронка)
     * */
    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    /**
     * Объект необходим для более "красивого" обращения в инициализации preferencesFlow.
     * */
    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}

/**
 * Enum, содержащий различные виды сортировки.
 * */
enum class SortOrder {
    BY_NAME, BY_DATE
}

/**
 * Данный класс содержит наши поля, которые можно настраивать.
 * */
data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)