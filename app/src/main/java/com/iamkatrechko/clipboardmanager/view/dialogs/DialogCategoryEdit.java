package com.iamkatrechko.clipboardmanager.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper;
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor;

/**
 * Диалог редактирования категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogCategoryEdit extends DialogFragment {
    private long categoryId = 0;
    private boolean isAddNewCategory = false;

    private EditText etTitle;

    public static DialogCategoryEdit newInstance(long categoryId) {
        DialogCategoryEdit fragment = new DialogCategoryEdit();
        Bundle args = new Bundle();

        args.putLong("categoryId", categoryId);

        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(long categoryId, String newName) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("categoryId", categoryId);
        a.putExtra("newName", newName);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_category_edit, null);

        categoryId = getArguments().getLong("categoryId");

        etTitle = (EditText) view.findViewById(R.id.etTitle);

        if (categoryId == -1){
            isAddNewCategory = true;
            etTitle.setText("Новая категория");
        }else{
            Uri categoryUri = DatabaseDescription.Category.buildClipUri(categoryId);
            CategoryCursor cursor =
                    new CategoryCursor(getActivity().getContentResolver().query(categoryUri,
                            null,
                            null,
                            null,
                            null));

            cursor.moveToFirst();

            etTitle.setText(cursor.getTitle());
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Изменение имени")
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int n) {
                        sendResult(categoryId, etTitle.getText().toString());
                    }
                })
                .setNegativeButton("Отмена", null)
                .create();
    }
}
