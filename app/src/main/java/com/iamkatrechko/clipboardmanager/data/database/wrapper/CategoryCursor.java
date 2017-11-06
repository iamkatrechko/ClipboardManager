package com.iamkatrechko.clipboardmanager.data.database.wrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;

/**
 * Оболочка для курсора со списком категорий
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
public class CategoryCursor extends CursorWrapper {

    /**
     * Конструктор
     * @param cursor курсор
     */
    public CategoryCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Возвращает идентификатор категории
     * @return идентификатор категории
     */
    public long getID() {
        return getWrappedCursor().getLong(getColumnIndex(DatabaseDescription.CategoryTable._ID));
    }

    /**
     * Возвращает заголовок категории
     * @return заголовок категории
     */
    public String getTitle() {
        return getWrappedCursor().getString(getColumnIndex(DatabaseDescription.CategoryTable.COLUMN_TITLE));
    }
}
