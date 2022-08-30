package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle // Сохранённое состояние между процессами
): ViewModel() {
    val task = state.get<Task>("task") // Здесь в качестве ключа задаём название аргумента в nav_graph.xml

    /**
     * Если в сохранённом состоянии (state.get<String>("taskName")) нет данных по названию таски, то
     * тогда сначала ищи название в найденной в сохранённом состоянии таске (task?.name), а если и
     * там нет, то пиши "".
     * */
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }
}