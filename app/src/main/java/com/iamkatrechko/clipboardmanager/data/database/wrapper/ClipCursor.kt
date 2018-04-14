package com.iamkatrechko.clipboardmanager.data.database.wrapper

import android.database.Cursor
import android.database.CursorWrapper
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable.*

/**
 * Оболочка для курсора со списком записей
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class ClipCursor(
        cursor: Cursor?
) : CursorWrapper(cursor) {

    /** Идентификатор заметки */
    val id: Long
        get() = wrappedCursor.getLong(_ID)

    /** Заголовок заметки */
    val title: String
        get() = wrappedCursor.getString(COLUMN_TITLE)

    /** Содержимое заметки */
    val content: String
        get() = wrappedCursor.getString(COLUMN_CONTENT)

    /** Дата заметки */
    val date: Long
        get() = wrappedCursor.getLong(COLUMN_DATE)

    /** Является ли заметка избранной */
    val isFavorite: Boolean
        get() = wrappedCursor.getInt(COLUMN_IS_FAVORITE) == 1

    /** Идентификатор категории заметки */
    val categoryId: Long
        get() = wrappedCursor.getLong(COLUMN_CATEGORY_ID)

    /** Была ли заметка удалена */
    val isDeleted: Boolean
        get() = wrappedCursor.getInt(COLUMN_IS_DELETED) == 1

    /** Позиция записи в списке */
    val positn: Int
        get() = wrappedCursor.getInt(COLUMN_POSITION)
}