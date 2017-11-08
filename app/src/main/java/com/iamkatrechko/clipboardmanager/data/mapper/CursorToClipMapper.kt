package com.iamkatrechko.clipboardmanager.data.mapper

import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.model.Clip

/**
 * Конвертер из ClipCursor в ClipsTable
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
        if (clipCursor.position < 0) {
            clipCursor.moveToFirst()
        }
        return Clip(clipCursor.id,
                clipCursor.title,
                clipCursor.content,
                clipCursor.isFavorite,
                clipCursor.date,
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