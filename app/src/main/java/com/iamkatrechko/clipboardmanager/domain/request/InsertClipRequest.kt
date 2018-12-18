package com.iamkatrechko.clipboardmanager.domain.request

import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Запрос добавления заметки в БД
 * @author iamkatrechko
 *         Date: 14.12.2018
 *
 * @param title      заголовок
 * @param content    контент
 * @param categoryId идентификатор категории
 * @param date       дата
 * @param position   позиция в списке
 */
class InsertClipRequest(
        val title: String,
        val content: String,
        val categoryId: Int,
        // ToDo: удалить поле
        val date: Long = System.currentTimeMillis(),
        val position: Int = 0 // ToDo: POSITION - last
) {

    companion object {

        /** Максимальная длина заголовка */
        private const val TITLE_MAX_LENGTH = 25

        /** Создает заметку с [content] и полями по-умолчанию */
        fun withContent(content: String): InsertClipRequest {
            val titleLength = Math.min(content.length, TITLE_MAX_LENGTH)
            return InsertClipRequest(
                    content.take(titleLength),
                    content,
                    Category.DEFAULT_CATEGORY_ID,
                    System.currentTimeMillis(),
                    0
            )
        }
    }
}