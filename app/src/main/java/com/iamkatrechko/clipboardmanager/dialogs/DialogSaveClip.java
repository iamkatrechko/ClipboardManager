package com.iamkatrechko.clipboardmanager.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DialogSaveClip extends DialogFragment {

    public static DialogSaveClip newInstance() {
        return new DialogSaveClip();
    }

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
                .setTitle("Отменить?")
                .setMessage("Изменения не сохранены. Выйти без сохранения?")
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                sendResult(false);
                            }
                        })
                /*.setNeutralButton("Гав",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })*/
                .setNegativeButton("Отмена", null).create();
    }
}
