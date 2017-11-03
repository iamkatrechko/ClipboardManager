package com.iamkatrechko.clipboardmanager.data.repository

import android.content.Context
import android.net.Uri
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription

/**
 * Репозиторий записей
 * @author iamkatrechko
 *         Date: 03.11.17
 */
class ClipboardRepository {

    /** Основной компаньон */
    companion object {

        /** Тег для логирования */
        private val TAG = ClipboardRepository::class.java.simpleName

        /** Экземпляр текущего класса */
        @Volatile
        private var INSTANCE: ClipboardRepository? = null

        /** Возвращает экземпляр текущего класса */
        fun getInstance(): ClipboardRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: ClipboardRepository().also { INSTANCE = it }
                }
    }

    /**
     * Добавление новой записи в базу данных
     * @param [context] контекст
     * @param [text]    текст записи
     * @return uri новой записи, если она успешно добавлена
     */
    fun addNewClip(context: Context, text: String): Uri? {
        var titleLength = 25

        val contentValues = DatabaseDescription.Clip.getDefaultContentValues()
        if (text.length < titleLength) {
            titleLength = text.length
        }
        contentValues.put(DatabaseDescription.Clip.COLUMN_TITLE, text.substring(0, titleLength))
        contentValues.put(DatabaseDescription.Clip.COLUMN_CONTENT, text)

        val newClipUri = context.contentResolver.insert(DatabaseDescription.Clip.CONTENT_URI, contentValues)

        return newClipUri
    }

    /**
     * Существует ли текущая запись в базе данных
     * @param [context] контекст
     * @param [text]    текст записи
     * @return true - существует, false - не существует
     */
    fun alreadyExists(context: Context, text: String): Boolean {
        val cursor = context.contentResolver.query(DatabaseDescription.Clip.CONTENT_URI,
                null,
                DatabaseDescription.Clip.COLUMN_CONTENT + " = ?",
                arrayOf(text),
                null)
        return cursor != null && cursor.count != 0
    }
}