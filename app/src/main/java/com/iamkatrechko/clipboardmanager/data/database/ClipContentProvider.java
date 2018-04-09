package com.iamkatrechko.clipboardmanager.data.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.AUTHORITY;
import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable;
import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable;

/**
 * Провайдер данных с заметками
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipContentProvider extends ContentProvider {

    /** Тег для логирования */
    private static final String TAG = ClipContentProvider.class.getSimpleName();
    /** UriMatcher помогает ContentProvider определить выполняемую операцию */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Тип запроса. Одна заметка */
    private static final int ONE_CLIP = 1;
    /** Тип запроса. Несколько заметок */
    private static final int CLIPS = 2;
    /** Тип запроса. Одна категория */
    private static final int ONE_CATEGORY = 3;
    /** Тип запроса. Несколько категорий */
    private static final int CATEGORIES = 4;

    static {
        uriMatcher.addURI(AUTHORITY, ClipsTable.TABLE_NAME + "/#", ONE_CLIP);
        uriMatcher.addURI(AUTHORITY, ClipsTable.TABLE_NAME, CLIPS);
        uriMatcher.addURI(AUTHORITY, CategoryTable.TABLE_NAME + "/#", ONE_CATEGORY);
        uriMatcher.addURI(AUTHORITY, CategoryTable.TABLE_NAME, CATEGORIES);
    }

    /** Экземпляр базы данных записей */
    private ClipDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ClipDatabaseHelper(getContext());
        Log.d(TAG, "Успешное создание");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Запрос на выборку данных");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP: // Выбрать запись с заданным идентификатором
                queryBuilder.setTables(ClipsTable.TABLE_NAME);
                queryBuilder.appendWhere(ClipsTable._ID + "=" + uri.getLastPathSegment());
                break;
            case ONE_CATEGORY: // Выбрать запись с заданным идентификатором
                queryBuilder.setTables(CategoryTable.TABLE_NAME);
                queryBuilder.appendWhere(CategoryTable._ID + "=" + uri.getLastPathSegment());
                break;
            case CLIPS: // Выбрать все записи
                queryBuilder.setTables(ClipsTable.TABLE_NAME);
                break;
            case CATEGORIES:
                queryBuilder.setTables(CategoryTable.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException("Invalid query Uri:" + uri);
        }

        // Выполнить запрос для получения одной или всех записей
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // Настройка отслеживания изменений в контенте
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "Запрос на вставку данных");
        Uri newUri;
        switch (uriMatcher.match(uri)) {
            case CLIPS:
                // При успехе возвращается идентификатор новой записи
                long clipId = dbHelper.getWritableDatabase().insert(ClipsTable.TABLE_NAME, null, values);
                // Если запись была вставлеан, создать подходящий Uri;
                // в противном случае выдать исключение
                if (clipId > 0) { // SQLite row IDs start at 1
                    newUri = ClipsTable.buildClipUri(clipId);
                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException("Insert failed: s" + uri);
                break;
            case CATEGORIES:
                // При успехе возвращается идентификатор новой записи
                long categoryId = dbHelper.getWritableDatabase().insert(CategoryTable.TABLE_NAME, null, values);
                // Если запись была вставлеан, создать подходящий Uri;
                // в противном случае выдать исключение
                if (categoryId > 0) { // SQLite row IDs start at 1
                    newUri = CategoryTable.buildClipUri(categoryId);
                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException("Insert failed: s" + uri);
                break;
            default:
                throw new UnsupportedOperationException("Invalid insert Uri:" + uri);
        }

        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] selectionArgs) {
        Log.d(TAG, "Запрос на удаление данных");
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP:
                // Получение из URI идентификатора записи
                String clipId = uri.getLastPathSegment();

                // Удаление записи
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(ClipsTable.TABLE_NAME, ClipsTable._ID + "=" + clipId, selectionArgs);
                break;
            case ONE_CATEGORY:
                // Получение из URI идентификатора записи
                String categoryId = uri.getLastPathSegment();

                // Удаление записи
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(CategoryTable.TABLE_NAME, CategoryTable._ID + "=" + categoryId, selectionArgs);
                //TODO добавить удаление записей данной категории
                break;
            default:
                throw new UnsupportedOperationException("Invalid delete Uri:" + uri);
        }

        // Оповестить наблюдателей об изменениях в базе данных
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String filter, String[] selectionArgs) {
        Log.d(TAG, "Запрос на обновление данных");
        int numberOfRowsUpdated; // 1, если обновление успешно; 0 при неудаче

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP:
                // Получение идентификатора записи из Uri
                String clipId = uri.getLastPathSegment();

                // Обновление записи
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        ClipsTable.TABLE_NAME,
                        values,
                        ClipsTable._ID + "=" + clipId,
                        selectionArgs);
                break;
            case CLIPS:
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        ClipsTable.TABLE_NAME,
                        values,
                        filter,
                        selectionArgs);
                break;
            case ONE_CATEGORY:
                // Получение идентификатора записи из Uri
                String categoryId = uri.getLastPathSegment();

                // Обновление записи
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        CategoryTable.TABLE_NAME,
                        values,
                        CategoryTable._ID + "=" + categoryId,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Invalid update Uri:" + uri);
        }

        // Если были внесены изменения, оповестить наблюдателей
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
