package com.iamkatrechko.clipboardmanager.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.iamkatrechko.clipboardmanager.data.model.Category;
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository;

import java.util.List;

/**
 * Диалог смены категории для заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogChangeCategory extends DialogFragment {

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static DialogChangeCategory newInstance() {
        return new DialogChangeCategory();
    }

    /**
     * Возвращает данные обратно во фрагмент
     * @param categoryId идентификатор выбранной категории
     */
    private void sendResult(long categoryId) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("categoryId", categoryId);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        List<Category> categoryList = CategoryRepository.Companion.getInstance().getCategories(getActivity());

        final String[] mItemsNames = new String[categoryList.size()];
        final long[] mItemsValues = new long[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            mItemsNames[i] = category.getTitle();
            mItemsValues[i] = category.getId();
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle("Переместить в:")
                .setItems(mItemsNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        sendResult(mItemsValues[item]);
                    }
                })
                .create();
    }
}

