package com.iamkatrechko.clipboardmanager.domain

import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest

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
    fun joinToString(clipIds: List<Long>, separator: String): String {
        return clipRepository.getClips(clipIds).joinToString(separator) { it.text }
    }

    /**
     * Объединяет содержимое записей в одну строку и создает на ее основе новую запись
     * @param [clipIds]   идентификаторы записей
     * @param [separator] разделитель между записями
     * @param [deleteOld] требуется ли удалить исходные записи
     */
    fun joinAndDelete(clipIds: List<Long>, separator: String, deleteOld: Boolean) {
        val newClipText = ClipsHelper.joinToString(clipIds, separator)
        if (deleteOld) {
            clipIds.forEach(clipRepository::deleteClip)
        }
        clipRepository.insertClip(InsertClipRequest.withContent(newClipText))
    }

    /**
     * Изменяет категорию записей
     * @param [clipIds]    идентификаторы записей
     * @param [categoryId] идентификатор новой категории
     */
    fun changeCategory(clipIds: List<Long>, categoryId: Long) {
        for (id in clipIds) {
            clipRepository.changeCategory(id, categoryId)
        }
    }

    /**
     * Изменяет флага избранности записи в базе данных
     * @param [clipId]     идентификатор записи
     * @param [isFavorite] флаг избранности
     */
    fun setFavorite(clipId: Long, isFavorite: Boolean) {
        clipRepository.setFavorite(clipId, isFavorite)
    }
}