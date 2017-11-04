package com.iamkatrechko.clipboardmanager.data.model

/**
 * Сущность записи базы данных
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
data class Clip(
        /** Идентификатор */
        val id: Long,
        /** Заголовок */
        val title: String,
        /** Текст записи */
        val text: String,
        /** флаг избранности */
        val isFavorite: Boolean,
        /** Время создания (мс) */
        val dateTime: Long,
        /** Идентификатор категории */
        val categoryId: Long,
        /** Флаг удалённой записи */
        val isDeleted: Boolean
)