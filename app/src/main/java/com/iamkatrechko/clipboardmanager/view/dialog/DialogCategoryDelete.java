package com.iamkatrechko.clipboardmanager.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Диалог удаления категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogCategoryDelete extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Идентификатор загрузчика категорий */
    private static final int CATEGORIES_LOADER = 1;

    /** Идентификатор удаляемой категории */
    private long deleteCategoryId;
    private Spinner mSpinner;
    private boolean moveInNewCategory = true;
    private ArrayAdapter<CharSequence> adapter;
    private Map<Integer, Long> hm = new HashMap<>();
    private int moveCategoryPos = 0;

    /**
     * Возвращает новый экземпляр диалога
     * @param categoryId идентификатор удаляемой категории
     * @return новый экземпляр диалога
     */
    public static DialogCategoryDelete newInstance(long categoryId) {
        DialogCategoryDelete fragment = new DialogCategoryDelete();
        Bundle args = new Bundle();

        args.putLong("deleteCategoryId", categoryId);

        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(long deleteCategoryId, long newCategoryId, boolean moveToNewCategory) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("deleteCategoryId", deleteCategoryId);
        a.putExtra("newCategoryId", newCategoryId);
        a.putExtra("moveToNewCategory", moveToNewCategory);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        deleteCategoryId = getArguments().getLong("deleteCategoryId", -1);

        Cursor categoriesCursor = getActivity()
                .getContentResolver()
                .query(DatabaseDescription.Category.CONTENT_URI, null, null, null, null);

        String[] categories = new String[categoriesCursor.getCount() - 1];
        categoriesCursor.moveToFirst();
        int i = 0;
        // Удаляем из списка категорий на перенос ту, что удаляется из базы
        do {
            if (categoriesCursor.getLong(0) != deleteCategoryId) {
                categories[i] = categoriesCursor.getString(1);
                hm.put(i, categoriesCursor.getLong(0));
                i++;
            }
        } while (categoriesCursor.moveToNext());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Удалить категорию и переместить ее записи в:")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("DialogCategoryDelete", "" + deleteCategoryId + "-" + hm.get(moveCategoryPos) + "-" + moveCategoryPos);
                        sendResult(deleteCategoryId, hm.get(moveCategoryPos), true);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                })
                .setSingleChoiceItems(categories, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moveCategoryPos = i;
                    }
                })
                .create();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CATEGORIES_LOADER:
                Log.d("MainActivity", "onCreateLoader");
                return new CursorLoader(getActivity(),
                        DatabaseDescription.Category.CONTENT_URI, // Uri таблицы contacts
                        null, // все столбцы
                        null, // все записи
                        null, // без аргументов
                        null); // сортировка
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String[] categories = new String[data.getCount() - 1];
        data.moveToFirst();
        int i = 0;
        // Удаляем из списка категорий на перенос ту, что удаляется из базы
        do {
            if (data.getLong(0) != deleteCategoryId) {
                categories[i] = data.getString(1);
                hm.put(i, data.getLong(0));
                i++;
            }
        } while (data.moveToNext());
        adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, categories);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
