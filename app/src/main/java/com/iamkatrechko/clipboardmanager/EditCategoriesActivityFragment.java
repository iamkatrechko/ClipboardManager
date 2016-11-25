package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class EditCategoriesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final static String TAG = "EditCategoryFragment";
    private static final int CATEGORIES_LOADER = 0;

    private RecyclerView mRecyclerView;
    private CategoriesCursorAdapter mAdapter;

    public EditCategoriesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new CategoriesCursorAdapter(getActivity(), new CategoriesCursorAdapter.MyClickListener() {
            @Override
            public void onEditClick(long categoryId) {
                DialogManager.showDialogCategoryEdit(EditCategoriesActivityFragment.this, categoryId);
            }

            @Override
            public void onDeleteClick(long categotyId) {
                DialogManager.showDialogCategoryDelete(EditCategoriesActivityFragment.this, categotyId);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
        return v;
    }

    public void showDialogAdd(){
        DialogManager.showDialogCategoryAdd(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_EDIT) {
            long categoryId = data.getLongExtra("categoryId", 1);
            String newName = data.getStringExtra("newName");

            Uri uri = Category.buildClipUri(categoryId);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category.COLUMN_TITLE, newName);
            getActivity().getContentResolver().update(uri, contentValues, null, null);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_ADD) {
            String newName = data.getStringExtra("newName");

            Uri uri = Category.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category.COLUMN_TITLE, newName);
            getActivity().getContentResolver().insert(uri, contentValues);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_DELETE) {
            long deleteCategoryId = data.getLongExtra("deleteCategoryId", -1);
            long newCategoryId = data.getLongExtra("newCategoryId", 1);

            Uri uriMove = Clip.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(Clip.COLUMN_CATEGORY_ID, newCategoryId);

            // Перемещение записей из удаляемой категории в новую
            getActivity().getContentResolver().update(uriMove,
                    contentValues,
                    Clip.COLUMN_CATEGORY_ID + "=" + deleteCategoryId,
                    null);

            // Удаление категории (теперь уже пустой)
            Uri uriDelete = Category.buildClipUri(deleteCategoryId);
            getActivity().getContentResolver().delete(uriDelete,
                    null,
                    null);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CATEGORIES_LOADER:
                return new CursorLoader(getActivity(),
                        Category.CONTENT_URI, // Uri таблицы contacts
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
        if (mAdapter != null) {
            mAdapter.setCursor(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
