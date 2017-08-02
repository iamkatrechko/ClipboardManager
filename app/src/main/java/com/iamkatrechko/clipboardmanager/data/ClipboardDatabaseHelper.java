package com.iamkatrechko.clipboardmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.Category;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.Clip;

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
                "CREATE TABLE " + Category.TABLE_NAME + "(" +
                        Category._ID + " INTEGER PRIMARY KEY, " +
                        Category.COLUMN_TITLE + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CATEGORIES_TABLE);

        final String CREATE_CLIPS_TABLE =
                "CREATE TABLE " + Clip.TABLE_NAME + "(" +
                        Clip._ID + " INTEGER PRIMARY KEY, " +
                        Clip.COLUMN_TITLE + " TEXT, " +
                        Clip.COLUMN_CONTENT + " TEXT, " +
                        Clip.COLUMN_DATE + " TEXT, " +
                        Clip.COLUMN_IS_FAVORITE + " INTEGER, " +
                        Clip.COLUMN_CATEGORY_ID + " INTEGER, " +
                        Clip.COLUMN_IS_DELETED + " INTEGER);";
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
            String query = "INSERT INTO " + Clip.TABLE_NAME + " (title, content, date, is_favorite, category_id, is_deleted) values(" +
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
            contentValues.put(Category.COLUMN_TITLE, categoryName);
            sqLiteDatabase.insert(Category.TABLE_NAME, null, contentValues);
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
            return getWrappedCursor().getLong(getColumnIndex(Clip._ID));
        }

        /**
         * Возвращает заголовок заметки
         * @return заголовок заметки
         */
        public String getTitle() {
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_TITLE));
        }

        /**
         * Возвращает содержимое заметки
         * @return содержимое заметки
         */
        public String getContent() {
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_CONTENT));
        }

        /**
         * Возвращает дату заметки
         * @return дата заметки
         */
        public String getDate() {
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_DATE));
        }

        /**
         * Является ли заметка избранной
         * @return принадлежность к избранным
         */
        public boolean isFavorite() {
            int buf = getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_IS_FAVORITE));
            return (buf == 1);
        }

        /**
         * Возвращает идентификатор категории заметки
         * @return идентификатор категории заметки
         */
        public int getCategoryId() {
            return getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_CATEGORY_ID));
        }

        /**
         * Была ли заметка удалена
         * @return была ли заметка удалена
         */
        public boolean isDeleted() {
            int buf = getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_IS_DELETED));
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
            return getWrappedCursor().getLong(getColumnIndex(Category._ID));
        }

        /**
         * Возвращает заголовок категории
         * @return заголовок категории
         */
        public String getTitle() {
            return getWrappedCursor().getString(getColumnIndex(Category.COLUMN_TITLE));
        }
    }
}
