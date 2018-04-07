package com.iamkatrechko.clipboardmanager.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToCategoryMapper
import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Репозиторий категорий записей
 * @author iamkatrechko
 *         Date: 08.11.17
 */
class CategoryRepository private constructor() {

    /** Основной компаньон */
    companion object {

        /** Экземпляр текущего класса */
        @Volatile
        private var INSTANCE: CategoryRepository? = null

        /** Возвращает экземпляр текущего класса */
        fun getInstance(): CategoryRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CategoryRepository().also { INSTANCE = it }
                }
    }

    /**
     * Возвращает категорию по его идентификатору
     * @param [context] контекст
     * @param [id]      идентификатор категории
     */
    fun getCategory(context: Context, id: Long): Category? {
        val cursor = context.contentResolver.query(DatabaseDescription.CategoryTable.buildClipUri(id),
                null,
                null,
                null,
                null)
        return if (cursor != null && cursor.moveToFirst()) {
            CursorToCategoryMapper().toCategory(CategoryCursor(cursor))
        } else {
            null
        }
    }

    /**
     * Возвращает список всех категорий
     * @param [context] контекст
     */
    fun getCategories(context: Context): List<Category> {
        val cursor = context.contentResolver.query(DatabaseDescription.CategoryTable.CONTENT_URI,
                null,
                null,
                null,
                null)
        val list = arrayListOf<Category>()
        if (cursor != null) {
            list.addAll(CursorToCategoryMapper().toCategories(CategoryCursor(cursor)))
        }
        return list
    }

    /** Добавляет новую категорию с именем [title] */
    fun addCategory(context: Context, title: String): Uri {
        val uri = DatabaseDescription.CategoryTable.CONTENT_URI
        val contentValues = ContentValues()
        contentValues.put(DatabaseDescription.CategoryTable.COLUMN_TITLE, title)
        return context.contentResolver.insert(uri, contentValues)
    }
}