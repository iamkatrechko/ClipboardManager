package com.iamkatrechko.clipboardmanager.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * Диалог включения служебного сервиса
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogEnableAccessibility extends DialogFragment {

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static DialogEnableAccessibility newInstance() {
        return new DialogEnableAccessibility();
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
                .setNegativeButton("Отмена", null).create();
    }
}
