package com.iamkatrechko.clipboardmanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

/**
 * Создает базу данных и дает возможность ClipboardContentProvider обращаться к ней.
 */

public class ClipboardDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Clipboard.db";
    private static final int DATABASE_VERSION = 1;

    public ClipboardDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Команда SQL для создания таблицы categories
        final String CREATE_CATEGORIES_TABLE =
                "CREATE TABLE " + Category.TABLE_NAME + "(" +
                        Category._ID + " INTEGER PRIMARY KEY, " +
                        Category.COLUMN_TITLE + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CATEGORIES_TABLE);

        // Команда SQL для создания таблицы clips
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

    private void generateTestData(SQLiteDatabase sqLiteDatabase){
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

    /** Оболочка для курсора со списком записей*/
    public static class ClipCursor extends CursorWrapper {
        public ClipCursor(Cursor cursor) {
            super(cursor);
        }

        public long getID(){
            return getWrappedCursor().getLong(getColumnIndex(Clip._ID));
        }

        public String getTitle(){
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_TITLE));
        }

        public String getContent(){
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_CONTENT));
        }

        public String getDate(){
            return getWrappedCursor().getString(getColumnIndex(Clip.COLUMN_DATE));
        }

        public boolean isFavorite(){
            int buf = getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_IS_FAVORITE));
            return (buf == 1);
        }

        public int getCategoryId(){
            return getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_CATEGORY_ID));
        }

        public boolean isDeleted(){
            int buf = getWrappedCursor().getInt(getColumnIndex(Clip.COLUMN_IS_DELETED));
            return (buf == 1);
        }
    }

    /** Оболочка для курсора со списком категорий */
    public static class CategoryCursor extends CursorWrapper {
        public CategoryCursor(Cursor cursor) {
            super(cursor);
        }

        public long getID(){
            return getWrappedCursor().getLong(getColumnIndex(Category._ID));
        }

        public String getTitle(){
            return getWrappedCursor().getString(getColumnIndex(Category.COLUMN_TITLE));
        }
    }
}
