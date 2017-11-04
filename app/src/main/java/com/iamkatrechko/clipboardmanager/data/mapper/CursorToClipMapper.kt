package com.iamkatrechko.clipboardmanager.data.mapper

import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.model.Clip

/**
 * Конвертер из ClipCursor в Clip
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class CursorToClipMapper {

    /**
     * Переводит из курсора в запись
     * @param [clipCursor] курсор с записью в нужной позиции
     * @return запись
     */
    fun toClip(clipCursor: ClipCursor): Clip {
        return Clip(clipCursor.id,
                clipCursor.title,
                clipCursor.content,
                clipCursor.isFavorite,
                clipCursor.date.toLong(),
                clipCursor.categoryId,
                clipCursor.isDeleted)
    }

    /**
     * Переводит из курсора в список записей
     * @param [clipCursor] курсор с записями
     * @return список записей
     */
    fun toClips(clipCursor: ClipCursor): List<Clip> {
        return ArrayList<Clip>().apply {
            for (i in 0 until clipCursor.count) {
                clipCursor.moveToPosition(i)
                add(toClip(clipCursor))
            }
        }
    }
}