package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    /**
     * Данный Callback вызывается каждый раз при создании БД. Не путать создание БД с
     * закрытием-открытием приложения.
     *
     * @Inject позволяет в метод onCreate засунуть нашу БД, которую Dagger нам создаст через AppModule.
     * */
    class Callback @Inject constructor(
       private val database: Provider<TaskDatabase>, // При помощи Provider мы можем получить database позднее
       @ApplicationScope private val applicationScope: CoroutineScope // Просим у Dagger'а дать контект для корутины
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Помыть посуду"))
                dao.insert(Task("Постирать вещи"))
                dao.insert(Task("Купить продукты", important = true))
                dao.insert(Task("Приготовить еду", completed = true))
                dao.insert(Task("Позвонить маме"))
                dao.insert(Task("Навестить бабушку", completed = true))
                dao.insert(Task("Починить велосипед"))
                dao.insert(Task("Позвонить Илону Маску"))
            }
        }
    }
}