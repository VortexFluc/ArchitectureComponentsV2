package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data access object.
 * В нашем случае данный интерфейсом мы определили CRUD операции для
 * нашего entity.
 * */
@Dao
interface TaskDao {

    // Поиск задачи по названию. Шаблон поиска: '%название%'
    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY important DESC")
    fun getTasks(searchQuery: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}