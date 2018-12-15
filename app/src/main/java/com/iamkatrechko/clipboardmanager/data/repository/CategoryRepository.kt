package com.iamkatrechko.clipboardmanager.data.repository

import android.content.ContentValues
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToCategoryMapper
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import com.iamkatrechko.clipboardmanager.domain.repository.ICategoryRepository
import com.iamkatrechko.clipboardmanager.domain.util.isNullOrEmpty

/**
 * Репозиторий категорий записей
 * ToDo: перевести все на Rx
 * @author iamkatrechko
 *         Date: 08.11.17
 *
 * @param ctx контекст приложения
 */
class CategoryRepository private constructor(
        private val ctx: Context
) : ICategoryRepository {

    override fun getCategory(id: Long): Category? {
        val cursor = ctx.contentResolver.query(DatabaseDescription.CategoryTable.buildClipUri(id),
                null,
                null,
                null,
                null)
        return if (cursor.isNullOrEmpty()) {
            null
        } else {
            cursor.moveToFirst()
            CursorToCategoryMapper.toCategory(CategoryCursor(cursor))
        }
    }

    override fun getCategories(): List<Category> {
        val cursor = ctx.contentResolver.query(DatabaseDescription.CategoryTable.CONTENT_URI,
                null,
                null,
                null,
                null)
        return CursorToCategoryMapper.toCategories(CategoryCursor(cursor))
    }

    override fun addCategory(title: String): Long? {
        val uri = DatabaseDescription.CategoryTable.CONTENT_URI
        val contentValues = ContentValues()
        contentValues.put(DatabaseDescription.CategoryTable.COLUMN_TITLE, title)
        return ctx.contentResolver.insert(uri, contentValues)?.lastPathSegment?.toLong()
    }

    override fun removeCategory(id: Int) {
        if (id == Category.DEFAULT_CATEGORY_ID) error("Невозможно удалить основную категорию")
        val uriDelete = DatabaseDescription.CategoryTable.buildClipUri(id.toLong())
        ctx.contentResolver.delete(uriDelete, null, null)
    }

    companion object : Provider<CategoryRepository>() {

        /** Приватный экземпляр класса */
        private var INSTANCE: CategoryRepository? = null

        /** Инициализирует компонент */
        fun init(context: Context) {
            INSTANCE = CategoryRepository(context)
        }

        override fun createInstance() = INSTANCE ?: error("Компонент не инициализирован")
    }
}