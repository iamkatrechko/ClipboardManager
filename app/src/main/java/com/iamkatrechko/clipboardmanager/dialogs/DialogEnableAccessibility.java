package com.iamkatrechko.clipboardmanager.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DialogEnableAccessibility extends DialogFragment{

    public static DialogEnableAccessibility newInstance() {
        return new DialogEnableAccessibility();
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
                .setTitle("Название")
                .setMessage("Для включения необходимо перейти в настройки и включить специальную слежбу блаблабла")
                .setPositiveButton("Перейти",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent, 122161);
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
