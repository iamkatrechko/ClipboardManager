package com.iamkatrechko.clipboardmanager.data.mapper

import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Конвертер из CategoryCursor в CategoryTable
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
object CursorToCategoryMapper {

    /**
     * Переводит из курсора в категорию
     * @param [cursor] курсор с записью в нужной позиции
     * @return категория
     */
    fun toCategory(cursor: CategoryCursor): Category =
            Category(cursor.id, cursor.title)

    /**
     * Переводит из курсора в список категорий
     * @param [cursor] курсор с категориями
     * @return список категорий
     */
    fun toCategories(cursor: CategoryCursor?): List<Category> {
        cursor ?: return emptyList()
        return MutableList(cursor.count) {
            cursor.moveToPosition(it)
            toCategory(cursor)
        }
    }
}