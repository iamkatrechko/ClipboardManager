package com.iamkatrechko.clipboardmanager.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

/**
 * Cубкласс ContentProvider, определяющий операции получения данных, вставки, обновления и удаления с базой данных.
 */
public class ClipboardContentProvider extends ContentProvider {
    private final String TAG = "ContentProvider";

    /** Экземпляр базы данных записей */
    private ClipboardDatabaseHelper dbHelper;
    /** UriMatcher помогает ContentProvider определить выполняемую операцию */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Константы, используемые для определения выполняемой операции
    private static final int ONE_CLIP = 1;                                                          // Одна запись
    private static final int CLIPS = 2;                                                             // Таблица записей
    private static final int ONE_CATEGORY = 3;                                                             // Одна запись
    private static final int CATEGORIES = 4;                                                             // Таблица записей

    // Статический блок для настройки UriMatcher объекта ContentProvider
    static {
        // Uri для записи с заданным идентификатором
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Clip.TABLE_NAME + "/#", ONE_CLIP);
        // Uri для таблицы
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Clip.TABLE_NAME, CLIPS);
        // Uri для записи с заданным идентификатором
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Category.TABLE_NAME + "/#", ONE_CATEGORY);
        // Uri для таблицы
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Category.TABLE_NAME, CATEGORIES);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ClipboardDatabaseHelper(getContext());
        Log.d("ContentProvider", "Объект ContentProvider создан успешно");

        return true;                                                                                // Объект ContentProvider создан успешно
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Создание SQLiteQueryBuilder для запроса к таблице clips
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Log.d("ContentProvider", "query");

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP: // Выбрать запись с заданным идентификатором
                queryBuilder.setTables(Clip.TABLE_NAME);
                queryBuilder.appendWhere(
                        Clip._ID + "=" + uri.getLastPathSegment());
                break;
            case ONE_CATEGORY: // Выбрать запись с заданным идентификатором
                queryBuilder.setTables(Category.TABLE_NAME);
                queryBuilder.appendWhere(
                        Category._ID + "=" + uri.getLastPathSegment());
                break;
            case CLIPS: // Выбрать все записи
                queryBuilder.setTables(Clip.TABLE_NAME);
                break;
            case CATEGORIES:
                queryBuilder.setTables(Category.TABLE_NAME);
                break;
            default:
                throw new UnsupportedOperationException(
                getContext().getString(R.string.invalid_query_uri) + uri);
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
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = null;
        String tableName;

        switch (uriMatcher.match(uri)) {
            case CLIPS:
                // При успехе возвращается идентификатор новой записи
                long rowId = dbHelper.getWritableDatabase().insert(
                        Clip.TABLE_NAME, null, values);
                // Если запись была вставлеан, создать подходящий Uri;
                // в противном случае выдать исключение
                if (rowId > 0) { // SQLite row IDs start at 1
                    newUri = Clip.buildClipUri(rowId);

                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            case CATEGORIES:
                // При успехе возвращается идентификатор новой записи
                long rowId2 = dbHelper.getWritableDatabase().insert(
                        Category.TABLE_NAME, null, values);
                // Если запись была вставлеан, создать подходящий Uri;
                // в противном случае выдать исключение
                if (rowId2 > 0) { // SQLite row IDs start at 1
                    newUri = Category.buildClipUri(rowId2);

                    // Оповестить наблюдателей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP:
                // Получение из URI идентификатора записи
                String id = uri.getLastPathSegment();

                // Удаление записи
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Clip.TABLE_NAME, Clip._ID + "=" + id, selectionArgs);
                break;
            case ONE_CATEGORY:
                // Получение из URI идентификатора записи
                String id2 = uri.getLastPathSegment();

                // Удаление записи
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        Category.TABLE_NAME, Category._ID + "=" + id2, selectionArgs);
				//TODO добавить удаление записей данной категории
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // Оповестить наблюдателей об изменениях в базе данных
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String s, String[] selectionArgs) {
        int numberOfRowsUpdated; // 1, если обновление успешно; 0 при неудаче

        switch (uriMatcher.match(uri)) {
            case ONE_CLIP:
                // Получение идентификатора записи из Uri
                String id = uri.getLastPathSegment();

                // Обновление записи
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Clip.TABLE_NAME, values, Clip._ID + "=" + id,
                        selectionArgs);
                break;
            case CLIPS:
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Clip.TABLE_NAME,
                        values,
                        s,
                        selectionArgs);
                break;
            case ONE_CATEGORY:
                // Получение идентификатора записи из Uri
                String id2 = uri.getLastPathSegment();

                // Обновление записи
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        Category.TABLE_NAME, values, Category._ID + "=" + id2,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // Если были внесены изменения, оповестить наблюдателей
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
