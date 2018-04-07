package com.iamkatrechko.clipboardmanager.view.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.data.model.Category;
import com.iamkatrechko.clipboardmanager.domain.loader.callback.CategoriesLoaderCallback;
import com.iamkatrechko.clipboardmanager.view.DialogManager;
import com.iamkatrechko.clipboardmanager.view.adapter.CategoriesCursorAdapter;
import com.iamkatrechko.clipboardmanager.view.adapter.ItemDivider;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.CategoryTable;
import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable;

/**
 * Фрагмент экрана со списком категорий
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class CategoriesListFragment extends Fragment {

    /** Адаптер списка категорий заметок */
    private CategoriesCursorAdapter categoriesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoriesAdapter = new CategoriesCursorAdapter(new CategoriesCursorAdapter.MyClickListener() {

            @Override
            public void onEditClick(long categoryId) {
                DialogManager.showDialogCategoryEdit(CategoriesListFragment.this, categoryId);
            }

            @Override
            public void onDeleteClick(long categoryId) {
                DialogManager.showDialogCategoryDelete(CategoriesListFragment.this, categoryId);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_categories, container, false);

        /* Виджет списка категорий */
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(categoriesAdapter);
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        getLoaderManager().initLoader(CategoriesLoaderCallback.CATEGORIES_LOADER, null, new CategoriesLoaderCallback(getContext(), new Function1<List<Category>, Unit>() {

            @Override
            public Unit invoke(List<Category> categories) {
                categoriesAdapter.setCursor(categories);
                return Unit.INSTANCE;
            }
        }));
        return v;
    }

    /** Отображает диалог создания категории */
    public void showDialogAdd() {
        DialogManager.showDialogCategoryAdd(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (DialogManager.DIALOG_EDIT == requestCode) {
            long categoryId = data.getLongExtra("categoryId", 1);
            String newName = data.getStringExtra("newName");

            Uri uri = CategoryTable.buildClipUri(categoryId);
            ContentValues contentValues = new ContentValues();
            contentValues.put(CategoryTable.COLUMN_TITLE, newName);
            getActivity().getContentResolver().update(uri, contentValues, null, null);
        }
        if (DialogManager.DIALOG_ADD == requestCode) {
            String newName = data.getStringExtra("newName");

            Uri uri = CategoryTable.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(CategoryTable.COLUMN_TITLE, newName);
            getActivity().getContentResolver().insert(uri, contentValues);
        }
        if (DialogManager.DIALOG_DELETE == requestCode) {
            long deleteCategoryId = data.getLongExtra("deleteCategoryId", -1);
            long newCategoryId = data.getLongExtra("newCategoryId", 1);

            Uri uriMove = DatabaseDescription.ClipsTable.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(ClipsTable.COLUMN_CATEGORY_ID, newCategoryId);

            // Перемещение записей из удаляемой категории в новую
            getActivity().getContentResolver().update(uriMove,
                    contentValues,
                    DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID + "=" + deleteCategoryId,
                    null);

            // Удаление категории (теперь уже пустой)
            Uri uriDelete = CategoryTable.buildClipUri(deleteCategoryId);
            getActivity().getContentResolver().delete(uriDelete,
                    null,
                    null);
        }
    }
}
