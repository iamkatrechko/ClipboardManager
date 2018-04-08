package com.iamkatrechko.clipboardmanager.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.domain.util.TAG
import java.util.*

/**
 * Класс по работе с базой данных с заметками
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipDatabaseHelper(
        private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val randomDate: Long
        get() {
            var time = System.currentTimeMillis()
            time -= nextLong(Random(), 31536000000L)
            return time
        }

    override fun onCreate(database: SQLiteDatabase) {
        Log.d(TAG, "Начало создания базы данных")
        database.execSQL(CREATE_CATEGORIES_TABLE)
        database.execSQL(CREATE_CLIPS_TABLE)
        createDefalutCategories(database)
        createTestClips(database)
        Log.d(TAG, "База данных создана")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, old: Int, new: Int) {}

    /** Создает категории по умолчанию */
    private fun createDefalutCategories(sqLiteDatabase: SQLiteDatabase) {
        val categories = context.resources.getStringArray(R.array.default_categories)
        for (categoryName in categories) {
            val contentValues = contentValuesOf(CategoryTable.COLUMN_TITLE to categoryName)
            sqLiteDatabase.insert(CategoryTable.TABLE_NAME, null, contentValues)
        }
    }

    /** Генерирует тестовые записи */
    private fun createTestClips(sqLiteDatabase: SQLiteDatabase) {
        repeat(1000) { i ->
            val content = contentValuesOf(
                    ClipsTable.COLUMN_TITLE to "Заголовок записи $i",
                    ClipsTable.COLUMN_CONTENT to "Содержимое записи $i",
                    ClipsTable.COLUMN_DATE to randomDate,
                    ClipsTable.COLUMN_IS_FAVORITE to i % 2,
                    ClipsTable.COLUMN_CATEGORY_ID to 2,
                    ClipsTable.COLUMN_IS_DELETED to i % 2
            )
            sqLiteDatabase.insert(ClipsTable.TABLE_NAME, null, content)
        }
    }

    private fun nextLong(rng: Random, n: Long): Long {
        // error checking and 2^x checking removed for simplicity.
        var bits: Long
        var value: Long
        do {
            bits = (rng.nextLong() shl 1).ushr(1)
            value = bits % n
        } while (bits - value + (n - 1) < 0L)
        return value
    }

    companion object {

        /** Имя базы данных  */
        private const val DATABASE_NAME = "Clipboard.db"
        /** Версия базы данных  */
        private const val DATABASE_VERSION = 1

        /** Запрос создания таблицы категорий */
        private const val CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoryTable.TABLE_NAME + "(" +
                CategoryTable._ID + " INTEGER PRIMARY KEY, " +
                CategoryTable.COLUMN_TITLE + " TEXT);"

        /** Запрос создания таблицы записей */
        private const val CREATE_CLIPS_TABLE = "CREATE TABLE " + ClipsTable.TABLE_NAME + "(" +
                ClipsTable._ID + " INTEGER PRIMARY KEY, " +
                ClipsTable.COLUMN_TITLE + " TEXT, " +
                ClipsTable.COLUMN_CONTENT + " TEXT, " +
                ClipsTable.COLUMN_DATE + " INTEGER, " +
                ClipsTable.COLUMN_IS_FAVORITE + " INTEGER, " +
                ClipsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                ClipsTable.COLUMN_IS_DELETED + " INTEGER);"
    }
}
