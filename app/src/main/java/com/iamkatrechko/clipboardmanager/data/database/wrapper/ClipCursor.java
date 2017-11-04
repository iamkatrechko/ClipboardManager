package com.iamkatrechko.clipboardmanager.data.database.wrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;

/**
 * Оболочка для курсора со списком записей
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
public class ClipCursor extends CursorWrapper {

    /**
     * Конструктор
     * @param cursor курсор с данными
     */
    public ClipCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Возвращает идентификатор заметки
     * @return идентификатор заметки
     */
    public long getID() {
        return getWrappedCursor().getLong(getColumnIndex(DatabaseDescription.Clip._ID));
    }

    /**
     * Возвращает заголовок заметки
     * @return заголовок заметки
     */
    public String getTitle() {
        return getWrappedCursor().getString(getColumnIndex(DatabaseDescription.Clip.COLUMN_TITLE));
    }

    /**
     * Возвращает содержимое заметки
     * @return содержимое заметки
     */
    public String getContent() {
        return getWrappedCursor().getString(getColumnIndex(DatabaseDescription.Clip.COLUMN_CONTENT));
    }

    /**
     * Возвращает дату заметки
     * @return дата заметки
     */
    public String getDate() {
        return getWrappedCursor().getString(getColumnIndex(DatabaseDescription.Clip.COLUMN_DATE));
    }

    /**
     * Является ли заметка избранной
     * @return принадлежность к избранным
     */
    public boolean isFavorite() {
        int buf = getWrappedCursor().getInt(getColumnIndex(DatabaseDescription.Clip.COLUMN_IS_FAVORITE));
        return (buf == 1);
    }

    /**
     * Возвращает идентификатор категории заметки
     * @return идентификатор категории заметки
     */
    public long getCategoryId() {
        return getWrappedCursor().getInt(getColumnIndex(DatabaseDescription.Clip.COLUMN_CATEGORY_ID));
    }

    /**
     * Была ли заметка удалена
     * @return была ли заметка удалена
     */
    public boolean isDeleted() {
        int buf = getWrappedCursor().getInt(getColumnIndex(DatabaseDescription.Clip.COLUMN_IS_DELETED));
        return (buf == 1);
    }
}