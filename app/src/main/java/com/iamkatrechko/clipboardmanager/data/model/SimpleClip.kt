package com.iamkatrechko.clipboardmanager.data.model

/**
 * Сущность упрощенной записи буфера обмена
 * ToDo: перенести в domain
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
data class SimpleClip(
        /** Текст записи */
        val text: String,
        /** Метка записи */
        val label: String
)