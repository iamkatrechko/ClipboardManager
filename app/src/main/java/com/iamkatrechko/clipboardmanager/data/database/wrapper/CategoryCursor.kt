package com.iamkatrechko.clipboardmanager.data.database.wrapper

import android.database.Cursor
import android.database.CursorWrapper
import androidx.core.database.getLong
import androidx.core.database.getString
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable.COLUMN_TITLE
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable._ID

/**
 * Оболочка для курсора со списком категорий
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class CategoryCursor(
        cursor: Cursor?
) : CursorWrapper(cursor) {

    /** Идентификатор категории */
    val id: Long
        get() = wrappedCursor.getLong(_ID)

    /** Заголовок категории */
    val title: String
        get() = wrappedCursor.getString(COLUMN_TITLE)
}
