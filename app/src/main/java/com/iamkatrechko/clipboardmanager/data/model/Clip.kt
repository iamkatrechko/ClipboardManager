package com.iamkatrechko.clipboardmanager.data.model

/**
 * Сущность записи базы данных
 * @author iamkatrechko
 *         Date: 04.11.2017
 *
 * @param id         идентификатор
 * @param title      заголовок
 * @param text       содержание
 * @param isFavorite признак избранности
 * @param dateTime   дата
 * @param categoryId идентификатор категории
 * @param isDeleted  флаг удаленного статуса
 * @param position   позиция в списке
 */
data class Clip(
        val id: Long,
        val title: String,
        val text: String,
        val isFavorite: Boolean,
        val dateTime: Long = System.currentTimeMillis(),
        val categoryId: Long,
        val isDeleted: Boolean = false,
        val position: Int = 0
)