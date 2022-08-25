package com.codinginflow.mvvmtodo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/**
 * Наш Entity, который содержит бизнесовую информацию и хранится в БД.
 *
 * Keyword "data" говорит компилятору Kotlin переопределить для нас equals, так, чтобы можно было
 * сравнить объекты так, как мы делали бы это в реально жизни. Т.е. два объекта data класса равны, если
 * все их поля равны.
 * */

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}