package com.iamkatrechko.clipboardmanager.domain.repository

import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Интерфейс репозитория списка категорий
 * @author iamkatrechko
 *         Date: 14.12.2018
 */
interface ICategoryRepository {

    /** Возвращает категорию по его [id] */
    fun getCategory(id: Long): Category?

    /** Возвращает список категорий */
    fun getCategories(): List<Category>

    /** Добавляет новую категорию с именем [title]. Возвращает id новой категории */
    fun addCategory(title: String): Long?

    /** Удаляет категорию по его [id] */
    fun removeCategory(id: Int)
}
