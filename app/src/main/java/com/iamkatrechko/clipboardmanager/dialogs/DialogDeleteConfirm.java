package com.iamkatrechko.clipboardmanager.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Диалог подтверждения удаления
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogDeleteConfirm extends DialogFragment {

    /**
     * Возвращает новый экземпляр фрагмента
     * @return новый экземпляр фрагмента
     */
    public static DialogDeleteConfirm newInstance() {
        return new DialogDeleteConfirm();
    }

    /**
     * Возвращает данные обратно во фрагмент
     * @param delete флаг подтверждения удаления
     */
    private void sendResult(boolean delete) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("delete", delete);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Подтверждение")
                .setMessage("Удалить выделенные записи?")
                .setPositiveButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(true);
                            }
                        })
                .setNegativeButton("Отмена", null).create();
    }
}
