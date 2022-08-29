package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentTasksBinding
import com.codinginflow.mvvmtodo.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

/**
 * Класс, в котором мы определили нашу кастомную View
 * */

@AndroidEntryPoint // Для Hilt'а необходимо указать данную аннотацию
class TasksFragment: Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val tasksAdapter = TasksAdapter()

        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter // Подключение адаптера к recyclerView
                layoutManager = LinearLayoutManager(requireContext()) // Устанавливает расположение вьюх
                setHasFixedSize(true) // Оптимизация. Так как мы знаем, что наш Task не изменяет свои размеры
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        // Включает менюху
        setHasOptionsMenu(true)
    }

    /**
     * Данный метод позволяет нам имплементировать нашу реализацию option menu в наш фрагмент.
     * */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        /**
         * Описание логики поведения при написании текста в SearchView.
         * См. файл com.codinginflow.mvvmtodo.util.ViewExt.
         * */
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    /**
     * Определение поведения приложения при выборе того или иного пункта меню.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            // Если пользователь ткнул на "Сортировку по названию"
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }

            // Если пользователь ткнул на "Сортировку по дате"
            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }

            // Если пользователь ткнул на "Удалить все завершённые задания"
            R.id.action_delete_all_completed_tasks -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}