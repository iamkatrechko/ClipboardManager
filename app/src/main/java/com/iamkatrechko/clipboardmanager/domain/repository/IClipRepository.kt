package com.iamkatrechko.clipboardmanager.domain.repository

import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest

/**
 * Интерфейс репозитория буферных заметок
 * @author iamkatrechko
 *         Date: 14.12.2018
 */
interface IClipRepository {

    /** Возвращает заметку по ее [id] */
    fun getClip(id: Long): Clip?

    /** Возвращает записи по их идентификаторам [ids] */
    fun getClips(ids: Collection<Long>): List<Clip>

    /** Вставляет заметку в БД */
    fun insertClip(request: InsertClipRequest): Int?

    /** Обновляет заметку в БД и возвращает статус обновления */
    fun updateClip(clipId: Long, clip: Clip): Boolean

    /** Существует ли заметка с текстом [content] */
    fun alreadyExists(content: String): Boolean

    /** Устанавливает статус избранности [isFavorite] для заметки по ее [clipId] */
    fun setFavorite(clipId: Long, isFavorite: Boolean)

    /** Изменяет категорию заметки с [clipId] на категорию с [categoryId] */
    fun changeCategory(clipId: Long, categoryId: Long)

    /** Удаляет заметку по ее [id] */
    fun deleteClip(id: Long)

    /** Удаляет записи по списку [ids] */
    fun deleteClips(ids: Collection<Long>)
}