package com.iamkatrechko.clipboardmanager.domain

import android.content.ContentValues
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository

/**
 * Помощник по работе с записями
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
object ClipsHelper {

    /**
     * Объединяет содержимое выделенных записей в одну строку
     * @param [context]   контекст
     * @param [clipIds]   идентификаторы записей
     * @param [separator] разделитель между записями
     * @return объединенная строка
     */
    fun joinToString(context: Context, clipIds: List<Long>, separator: String): String {
        return ClipboardRepository().getClips(context, clipIds).joinToString(separator)
    }

    /**
     * Объединяет содержимое записей в одну строку и создает на ее основе новую запись
     * @param [context]   контекст
     * @param [clipIds]   идентификаторы записей
     * @param [separator] разделитель между записями
     * @param [deleteOld] требуется ли удалить исходные записи
     */
    fun joinAndDelete(context: Context, clipIds: List<Long>, separator: String, deleteOld: Boolean) {
        val newClipText = ClipsHelper.joinToString(context, clipIds, separator)
        if (deleteOld) {
            ClipboardRepository().deleteClips(context, clipIds)
        }
        ClipboardRepository().addClip(context, newClipText)
    }

    /**
     * Изменяет категорию выделенных записей
     * @param [context]    контекст
     * @param [clipIds]    идентификаторы записей
     * @param [categoryId] идентификатор новой категории
     */
    fun changeCategory(context: Context, clipIds: List<Long>, categoryId: Long) {
        for (id in clipIds) {
            val uri = DatabaseDescription.ClipsTable.buildClipUri(id)

            val contentValues = ContentValues()
            contentValues.put(DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID, categoryId)
            context.contentResolver.update(uri, contentValues, null, null)
        }
    }

    /**
     * Изменяет флага избранности записи в базе данных
     * @param [context]    контекст
     * @param [clipId]     идентификатор записи
     * @param [isFavorite] флаг избранности
     */
    fun setFavorite(context: Context, clipId: Long, isFavorite: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseDescription.ClipsTable.COLUMN_IS_FAVORITE, isFavorite)
        context.contentResolver.update(DatabaseDescription.ClipsTable.buildClipUri(clipId), contentValues, null, null)
    }
}