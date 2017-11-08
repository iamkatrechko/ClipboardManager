package com.iamkatrechko.clipboardmanager.data.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Описание таблиц с заметками
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DatabaseDescription {

    /** Имя ContentProvider */
    public static final String AUTHORITY = "com.iamkatrechko.clipboardmanager.data";
    /** Базовый URI для взаимодействия с ContentProvider */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /** Описание таблицы clips */
    public static final class ClipsTable implements BaseColumns {

        /** Имя таблицы */
        public static final String TABLE_NAME = "clips";
        /** Объект Uri для таблицы clips */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /** Имя столбца с названием */
        public static final String COLUMN_TITLE = "title";
        /** Имя столбца с содержимым */
        public static final String COLUMN_CONTENT = "content";
        /** Имя столбца датой */
        public static final String COLUMN_DATE = "date";
        /** Имя столбца с принадлежностью к избранным */
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        /** Имя столбца с категорией записи */
        public static final String COLUMN_CATEGORY_ID = "category_id";
        /** Имя столбца с принадлежностью к корзине */
        public static final String COLUMN_IS_DELETED = "is_deleted";

        /** Создание Uri для конкретной записи */
        public static Uri buildClipUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Возвращает значения для записи по умолчанию
         * @return значения по умолчанию
         */
        public static ContentValues getDefaultContentValues() {
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_TITLE, "");
            contentValues.put(COLUMN_CONTENT, "");
            contentValues.put(COLUMN_DATE, System.currentTimeMillis());
            contentValues.put(COLUMN_IS_FAVORITE, 0);
            contentValues.put(COLUMN_CATEGORY_ID, 1);
            contentValues.put(COLUMN_IS_DELETED, 0);

            return contentValues;
        }
    }

    /** Описание таблицы с категориями */
    public static final class CategoryTable implements BaseColumns {

        /** Имя таблицы */
        public static final String TABLE_NAME = "categories";
        /** Объект Uri для таблицы clips */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /** Имя столбца с названием */
        public static final String COLUMN_TITLE = "title";

        /** Создание Uri для конкретной записи */
        public static Uri buildClipUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

