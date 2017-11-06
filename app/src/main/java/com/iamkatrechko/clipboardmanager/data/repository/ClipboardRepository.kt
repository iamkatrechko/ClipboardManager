package com.iamkatrechko.clipboardmanager.data.repository

import android.content.Context
import android.net.Uri
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor

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
    fun addClip(context: Context, text: String): Uri? {
        var titleLength = 25

        val contentValues = DatabaseDescription.Clip.getDefaultContentValues()
        if (text.length < titleLength) {
            titleLength = text.length
        }
        contentValues.put(DatabaseDescription.Clip.COLUMN_TITLE, text.substring(0, titleLength))
        contentValues.put(DatabaseDescription.Clip.COLUMN_CONTENT, text)

        return context.contentResolver.insert(DatabaseDescription.Clip.CONTENT_URI, contentValues)
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

    /**
     * Возвращает запись из базы данных
     * @param [context] контекст
     * @param [id]      идентификатор записи
     * @return запись из базы данных
     */
    fun getClip(context: Context, id: Long): String? {
        val uri = DatabaseDescription.Clip.buildClipUri(id)
        val cursor = ClipCursor(
                context.contentResolver.query(uri, null, null, null, null)
        )
        if (cursor.moveToFirst()) {
            return cursor.content
        }
        return null
    }

    /**
     * Возвращает список записей по их идентификаторам
     * @param [context] контекст
     * @param [ids]     список идентификаторов записей
     * @return список записей по их идентификаторам
     */
    fun getClips(context: Context, ids: List<Long>): List<String> {
        return ids.mapTo(ArrayList()) { ClipboardRepository().getClip(context, it) }.filterNotNull()
    }

    /**
     * Удаляет запись из базы данных
     * @param [context] контект
     * @param [id]      идентификатор записи
     */
    fun deleteClip(context: Context, id: Long) {
        context.contentResolver.delete(DatabaseDescription.Clip.buildClipUri(id),
                null, null)
    }

    /**
     * Удаляет записи из базы данных
     * @param [context] контекст
     * @param [ids]     идентификаторы записей на удаление
     */
    fun deleteClips(context: Context, ids: List<Long>) {
        ids.forEach { deleteClip(context, it) }
    }
}