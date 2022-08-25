package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Данный класс объясняет Dagger'у как создавать объекты по запросам.
 * Необходимо заметить, что это не класс, а ОБЪЕКТ.
 *
 * Особое внимание на @InstallIn - данной анотацией мы поясняем Dagger'у, что хотим использовать данный
 * компонент в ApplicationComponent контексте.
 *
 * ApplicationComponent - Автогенерируемый Dagger'ом класс в котором содержаться все зависимости
 * приложения.
 * */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    /**
     * Метод определяет, как Dagger'у получить БД. Аннотация Provides отвечает за это.
     * Также синтаксис может немного смущать, т.к. мы после определения параметров функции ставми знак
     * "=", но всё норм. Таким образом можно определить однострочную функцию.
     * */
    @Provides
    @Singleton // Нам необходим только один Instance нашей БД. Поэтому юзаем Singleton (это паттерн)
    fun provideDatabase(
        app: Application, // Здесь указывается наше приложение при вызове. См. AndroidManifest.
        callback: TaskDatabase.Callback // Callback, который заполнит нашу БД или ещё что-нибудь
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration() // DROP таблицы и создания новой при миграции
            .addCallback(callback) // Для того, чтобы БД изначально была не пустой мы заполняем её, используя Callback
            .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()


    /**
     * Данный провайдер позволяет нам получить Scope для корутины. Так как suspend функции в Dao могут
     * исполняться только асинхронно - для них нужны корутины.
     * */
    @ApplicationScope
    @Provides
    @Singleton
    /**
     * SuperviserJob говорит контексту, что если один из Child-процессов в корутинах умер, то не надо
     * останавливать все Child-процессы.
     */
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

/**
 * Данная аннотация позволяет менять DI-фреймворки.
 * */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope