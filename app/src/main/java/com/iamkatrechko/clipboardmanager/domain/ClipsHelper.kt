package com.iamkatrechko.clipboardmanager.domain

import android.content.Context
import androidx.core.content.contentValuesOf
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository

/**
 * Помощник по работе с записями
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
object ClipsHelper {

    /** Репозиторий списка записей */
    private val clipRepository = ClipboardRepository.getInstance()

    /**
     * Объединяет содержимое выделенных записей в одну строку
     * @param [context]   контекст
     * @param [clipIds]   идентификаторы записей
     * @param [separator] разделитель между записями
     * @return объединенная строка
     */
    fun joinToString(context: Context, clipIds: List<Long>, separator: String): String {
        return clipRepository.getClips(context, clipIds).joinToString(separator)
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
            clipRepository.deleteClips(context, clipIds)
        }
        clipRepository.insertClip(context, newClipText)
    }

    /**
     * Изменяет категорию записей
     * @param [context]    контекст
     * @param [clipIds]    идентификаторы записей
     * @param [categoryId] идентификатор новой категории
     */
    fun changeCategory(context: Context, clipIds: List<Long>, categoryId: Long) {
        for (id in clipIds) {
            val content = contentValuesOf(ClipsTable.COLUMN_CATEGORY_ID to categoryId)
            clipRepository.updateClip(context, id, content)
        }
    }

    /**
     * Изменяет флага избранности записи в базе данных
     * @param [context]    контекст
     * @param [clipId]     идентификатор записи
     * @param [isFavorite] флаг избранности
     */
    fun setFavorite(context: Context, clipId: Long, isFavorite: Boolean) {
        val content = contentValuesOf(ClipsTable.COLUMN_IS_FAVORITE to isFavorite)
        clipRepository.updateClip(context, clipId, content)
    }
}