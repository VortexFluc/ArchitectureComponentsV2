package com.codinginflow.mvvmtodo.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding

class TasksAdapter: ListAdapter<Task, TasksAdapter.TasksViewHolder>(DiffCallback()) {

    /**
     * Вызывается при создании нашего TasksViewHolder'а.
     * */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {

        /**
         * .inflate(<inflating_object>, <place_to_put>, <metadata>) - Инстанциирует наш item_task.xml,
         * "раздувая" его и превращая в объект, с которым мы можем взаимодействовать.
         *
         * parent - в нашем случае родителем является RecyclerView - родительский класс для ListAdapter'а.
         * */
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksViewHolder(binding)
    }

    /**
     * Данный метод вызвается при выборе соответствующего элемента в RecyclerView.
     * Здесь мы описываем логику поведения при выборе того или иного (исходя из position) элемента.
     * */
    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position) // Получения элемента по порядковому номеру
        holder.bind(currentItem) // Связка нашего ViewHolder'а c xml отображением
    }

    /**
     * Nested Класс, позволяющий связать наш Task с отображением на экране в файле item_task.xml
     *
     * ItemTaskBinding - автосгенерированный класс для item_task.xml.
     * В конструктор ViewHolder'а мы передаём binding.root, что позволит нам "достучаться" до "вьюх"
     * внутри item_task.xml через обращения типа binding.<idЭлемента>.
     * */
    class TasksViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                checkboxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }

    }

    /**
     * Nested Класс, являющийся обработчиком событий, определяющий логику определения являются ли два
     * объекта одним и тем же объектом (одинаковые id), или же являются равными друг другу
     * (содержимое двух объектов одинаковое).
     * */
    class DiffCallback: DiffUtil.ItemCallback<Task>() {

        /**
         * Проверка на идентичность объектов, путём равенства id'шников.
         * */
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        /**
         * Проверка на равенство объектов, путём вызова equals для них. (См. data class Task)
         * */
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

    }
}