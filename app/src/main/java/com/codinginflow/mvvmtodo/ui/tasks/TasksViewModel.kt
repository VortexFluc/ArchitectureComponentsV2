package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)
    /**
     * Когда изменяется searchQuery, то мы передаём в getTask() новое значение searchQuery
     * */
    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ) { query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest { (query, sortOrder, hideCompleted) ->
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }

    /**
     * Мы не должны привязваться к Activity. Так как Activity уничтожается и собирается заново при
     * вращении экрана. Если мы привяжемся к определённому Activity, то есть вероятность получить
     * Memory leak при вращении экрана.
     *
     * Чтобы избежать этого наши таски обёрнуты во Flow, который будет следить за актуальностью данных.
     * */
    val tasks = tasksFlow.asLiveData()
}

enum class SortOrder {
    BY_NAME, BY_DATE
}