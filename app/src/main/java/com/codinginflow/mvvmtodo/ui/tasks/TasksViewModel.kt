package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


/**
 * Данный класс определяет взаимодействие с данными, приходящими с UI, а также маршуртизует их в БД.
 * Суть класса в наличии LiveData, которая может обновляется динамически.
 *
 * Особое внимание обращаю на @ViewModelInject - внедрение зависимостей во ViewModel имеет свою аннотацию
 * отличную от @Inject.
 * */
class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao, // Data access object - Взаимодействие с БД
    private val preferencesManager: PreferencesManager, // Данный менеджер предоставляет нам настройки пользователя
   @Assisted private val state: SavedStateHandle
): ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "") // В данную строку мы кладём значение строки поиска
    val preferencesFlow = preferencesManager.preferencesFlow // Здесь мы инициализируем настройки пользователя

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    /**
     * Когда изменяется searchQuery или preferencesFlow, то мы передаём в getTask() новое значение
     * searchQuery
     * */
    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    /**
     * Мы не должны привязваться к Activity. Так как Activity уничтожается и собирается заново при
     * вращении экрана. Если мы привяжемся к определённому Activity, то есть вероятность получить
     * Memory leak при вращении экрана.
     *
     * Чтобы избежать этого наши таски обёрнуты во Flow, который будет следить за актуальностью данных.
     * */
    val tasks = tasksFlow.asLiveData()


    /**
     * Вызывает метод, который обновляет настройки сортировки для юзера
     * */
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    /**
     * Вызывает метод, который обновляет настройки отображения выполненных задач
     * */
    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }


    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    /**
     * Определяет поведение при нажатии на CheckBox около задачи
     * */
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    /**
     * Определяет поведение при свайпе.
     * */
    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    /**
     * Метод для возвращения удалённой задачи
     * */
    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Задача добавлена")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Задача обновлена")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }


    /**
     * Sealed-class похож на enum, но объекты sealed класса могут содержать данные (в отличие от
     * enum'овских объектов)
     * */
    sealed class TasksEvent {
        object NavigateToAddTaskScreen: TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task): TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task): TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String): TasksEvent()
    }
}
