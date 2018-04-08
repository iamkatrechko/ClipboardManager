package com.iamkatrechko.clipboardmanager.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider

/**
 * Репозиторий записей
 * @author iamkatrechko
 *         Date: 03.11.17
 */
class ClipboardRepository private constructor() {

    /**
     * Добавление новой записи в базу данных
     * @param [context] контекст
     * @param [text]    текст записи
     * @return uri новой записи, если она успешно добавлена
     */
    fun insertClip(context: Context, text: String): Uri? {
        var titleLength = 25

        val contentValues = ClipsTable.getDefaultContentValues()
        if (text.length < titleLength) {
            titleLength = text.length
        }
        contentValues.put(ClipsTable.COLUMN_TITLE, text.substring(0, titleLength))
        contentValues.put(ClipsTable.COLUMN_CONTENT, text)

        return insertClip(context, contentValues)
    }

    /**
     * Добавление новой записи в базу данных
     * @param [context]       контекст
     * @param [contentValues] свойства записи
     * @return uri новой записи, если она успешно добавлена
     */
    fun insertClip(context: Context, contentValues: ContentValues): Uri? {
        return context.contentResolver.insert(ClipsTable.CONTENT_URI, contentValues)
    }

    /**
     * Существует ли текущая запись в базе данных
     * @param [context] контекст
     * @param [text]    текст записи
     * @return true - существует, false - не существует
     */
    fun alreadyExists(context: Context, text: String): Boolean {
        val cursor = context.contentResolver.query(ClipsTable.CONTENT_URI,
                null,
                ClipsTable.COLUMN_CONTENT + " = ?",
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
    fun getClip(context: Context, id: Long): Clip? {
        val uri = ClipsTable.buildClipUri(id)
        val cursor = ClipCursor(
                context.contentResolver.query(uri, null, null, null, null)
        )
        if (cursor.moveToFirst()) {
            return CursorToClipMapper().toClip(ClipCursor(cursor))
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
        return ids.mapTo(ArrayList()) { ClipboardRepository().getClip(context, it) }.mapNotNull { it?.text }
    }

    /**
     * Удаляет запись из базы данных
     * @param [context] контект
     * @param [id]      идентификатор записи
     */
    fun deleteClip(context: Context, id: Long) {
        context.contentResolver.delete(ClipsTable.buildClipUri(id),
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

    /**
     * Обновляет содержимое записи
     * @param [context]       контекст
     * @param [clipId]        идентификатор записи
     * @param [contentValues] новые свойства записи
     * @return количество обновленных записей
     */
    fun updateClip(context: Context, clipId: Long, contentValues: ContentValues): Int {
        val clipUri = DatabaseDescription.ClipsTable.buildClipUri(clipId)
        return context.contentResolver.update(clipUri, contentValues, null, null)
    }

    companion object : Provider<ClipboardRepository>() {

        override fun createInstance() = ClipboardRepository()
    }
}