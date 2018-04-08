package com.iamkatrechko.clipboardmanager.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.iamkatrechko.clipboardmanager.R;

/**
 * Диалог сохранения заметки перед выходом
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogSaveClip extends DialogFragment {

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static DialogSaveClip newInstance() {
        return new DialogSaveClip();
    }

    /**
     * Возвращает данные обратно во фрагмент
     * @param save флаг сохранения заметки
     */
    private void sendResult(boolean save) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("save", save);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.question_cancel)
                .setMessage(R.string.question_exit_without_save)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(false);
                            }
                        })
                .setNegativeButton(R.string.cancel, null).create();
    }
}
