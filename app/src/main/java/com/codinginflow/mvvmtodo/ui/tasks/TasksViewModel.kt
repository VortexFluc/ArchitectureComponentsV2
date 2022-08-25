package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {

    /**
     * Мы не должны привязваться к Activity. Так как Activity уничтожается и собирается заново при
     * вращении экрана. Если мы привяжемся к определённому Activity, то есть вероятность получить
     * Memory leak при вращении экрана.
     *
     * Чтобы избежать этого наши таски обёрнуты во Flow, который будет следить за актуальностью данных.
     * */
    val tasks = taskDao.getTasks().asLiveData()
}