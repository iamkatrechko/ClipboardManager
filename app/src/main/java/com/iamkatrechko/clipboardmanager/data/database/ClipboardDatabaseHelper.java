package com.iamkatrechko.clipboardmanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Класс по работе с базой данных с заметками
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipboardDatabaseHelper extends SQLiteOpenHelper {

    /** Имя базы данных */
    private static final String DATABASE_NAME = "Clipboard.db";
    /** Версия базы данных */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор
     * @param context контекст
     */
    public ClipboardDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_CATEGORIES_TABLE =
                "CREATE TABLE " + DatabaseDescription.Category.TABLE_NAME + "(" +
                        DatabaseDescription.Category._ID + " INTEGER PRIMARY KEY, " +
                        DatabaseDescription.Category.COLUMN_TITLE + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CATEGORIES_TABLE);

        final String CREATE_CLIPS_TABLE =
                "CREATE TABLE " + DatabaseDescription.Clip.TABLE_NAME + "(" +
                        DatabaseDescription.Clip._ID + " INTEGER PRIMARY KEY, " +
                        DatabaseDescription.Clip.COLUMN_TITLE + " TEXT, " +
                        DatabaseDescription.Clip.COLUMN_CONTENT + " TEXT, " +
                        DatabaseDescription.Clip.COLUMN_DATE + " TEXT, " +
                        DatabaseDescription.Clip.COLUMN_IS_FAVORITE + " INTEGER, " +
                        DatabaseDescription.Clip.COLUMN_CATEGORY_ID + " INTEGER, " +
                        DatabaseDescription.Clip.COLUMN_IS_DELETED + " INTEGER);";
        sqLiteDatabase.execSQL(CREATE_CLIPS_TABLE);

        generateTestData(sqLiteDatabase);
        Log.d("DataBase", "База создана");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Генерирует тестовые данные
     * @param sqLiteDatabase база данных
     */
    private void generateTestData(SQLiteDatabase sqLiteDatabase) {
        for (int i = 1; i < 10; i++) {
            String query = "INSERT INTO " + DatabaseDescription.Clip.TABLE_NAME + " (title, content, date, is_favorite, category_id, is_deleted) values(" +
                    "'Название " + i + "', " +
                    "'Содержимое " + i + "', " +
                    "'" + i * 86400000 + "', " +
                    "" + i % 2 + ", " +
                    "2, " +
                    "" + i % 2 + ")";
            sqLiteDatabase.execSQL(query);
        }

        String[] categories = new String[]{"Основная категория", "Категория №2", "Категория №3", "Категория №4"};
        for (String categoryName : categories) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDescription.Category.COLUMN_TITLE, categoryName);
            sqLiteDatabase.insert(DatabaseDescription.Category.TABLE_NAME, null, contentValues);
        }

        /*for (int i = 1; i < 5; i++){
            String query = "INSERT INTO " + Category.TABLE_NAME + " (title) values('Категория " + i + "')";
            sqLiteDatabase.execSQL(query);
        }*/
    }

    /** Оболочка для курсора со списком записей */
    public static class ClipCursor extends CursorWrapper {

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
        public int getCategoryId() {
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

    /** Оболочка для курсора со списком категорий */
    public static class CategoryCursor extends CursorWrapper {

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
            return getWrappedCursor().getLong(getColumnIndex(DatabaseDescription.Category._ID));
        }

        /**
         * Возвращает заголовок категории
         * @return заголовок категории
         */
        public String getTitle() {
            return getWrappedCursor().getString(getColumnIndex(DatabaseDescription.Category.COLUMN_TITLE));
        }
    }
}
