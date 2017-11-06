package com.iamkatrechko.clipboardmanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Random;

/**
 * Класс по работе с базой данных с заметками
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipDatabaseHelper extends SQLiteOpenHelper {

    /** Тег для логирования */
    private static final String TAG = ClipDatabaseHelper.class.getSimpleName();
    /** Имя базы данных */
    private static final String DATABASE_NAME = "Clipboard.db";
    /** Версия базы данных */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор
     * @param context контекст
     */
    public ClipDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_CATEGORIES_TABLE =
                "CREATE TABLE " + DatabaseDescription.CategoryTable.TABLE_NAME + "(" +
                        DatabaseDescription.CategoryTable._ID + " INTEGER PRIMARY KEY, " +
                        DatabaseDescription.CategoryTable.COLUMN_TITLE + " TEXT);";
        sqLiteDatabase.execSQL(CREATE_CATEGORIES_TABLE);

        final String CREATE_CLIPS_TABLE =
                "CREATE TABLE " + DatabaseDescription.ClipsTable.TABLE_NAME + "(" +
                        DatabaseDescription.ClipsTable._ID + " INTEGER PRIMARY KEY, " +
                        DatabaseDescription.ClipsTable.COLUMN_TITLE + " TEXT, " +
                        DatabaseDescription.ClipsTable.COLUMN_CONTENT + " TEXT, " +
                        DatabaseDescription.ClipsTable.COLUMN_DATE + " INTEGER, " +
                        DatabaseDescription.ClipsTable.COLUMN_IS_FAVORITE + " INTEGER, " +
                        DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                        DatabaseDescription.ClipsTable.COLUMN_IS_DELETED + " INTEGER);";
        sqLiteDatabase.execSQL(CREATE_CLIPS_TABLE);

        generateTestData(sqLiteDatabase);
        Log.d(TAG, "База создана");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Генерирует тестовые данные
     * @param sqLiteDatabase база данных
     */
    private void generateTestData(SQLiteDatabase sqLiteDatabase) {
        for (int i = 1; i < 1000; i++) {
            String query = "INSERT INTO " + DatabaseDescription.ClipsTable.TABLE_NAME + " (title, content, date, is_favorite, category_id, is_deleted) values(" +
                    "'Заголовок записи " + i + "', " +
                    "'Содержимое записи " + i + "', " +
                    "" + getDate() + ", " +
                    "" + i % 2 + ", " +
                    "2, " +
                    "" + i % 2 + ")";
            sqLiteDatabase.execSQL(query);
        }

        String[] categories = new String[]{"Основная категория", "Категория №2", "Категория №3", "Категория №4"};
        for (String categoryName : categories) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDescription.CategoryTable.COLUMN_TITLE, categoryName);
            sqLiteDatabase.insert(DatabaseDescription.CategoryTable.TABLE_NAME, null, contentValues);
        }

        /*for (int i = 1; i < 5; i++){
            String query = "INSERT INTO " + CategoryTable.TABLE_NAME + " (title) values('Категория " + i + "')";
            sqLiteDatabase.execSQL(query);
        }*/
    }

    private long getDate() {
        long time = System.currentTimeMillis();
        time -= nextLong(new Random(), 31536000000L);
        return time;
    }

    long nextLong(Random rng, long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }
}
