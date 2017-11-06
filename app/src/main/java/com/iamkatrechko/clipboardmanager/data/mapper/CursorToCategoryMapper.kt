package com.iamkatrechko.clipboardmanager.data.mapper

import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Конвертер из CategoryCursor в CategoryTable
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class CursorToCategoryMapper {

    /**
     * Переводит из курсора в категорию
     * @param [catCursor] курсор с записью в нужной позиции
     * @return категория
     */
    fun toCategory(catCursor: CategoryCursor): Category {
        return Category(catCursor.id,
                catCursor.title)
    }

    /**
     * Переводит из курсора в список категорий
     * @param [catCursor] курсор с категориями
     * @return список категорий
     */
    fun toCategories(catCursor: CategoryCursor): List<Category> {
        return ArrayList<Category>().apply {
            for (i in 0 until catCursor.count) {
                catCursor.moveToPosition(i)
                add(toCategory(catCursor))
            }
        }
    }
}