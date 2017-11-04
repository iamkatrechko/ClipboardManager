package com.iamkatrechko.clipboardmanager.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;

/**
 * Диалог выбора сортировки заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogSetOrderType extends DialogFragment {

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static DialogSetOrderType newInstance() {
        return new DialogSetOrderType();
    }

    /**
     * Возвращает данные обратно во фрагмент
     * @param orderType тип сортировки
     */
    private void sendResult(String orderType) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("orderType", orderType);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final String[] mItemsNames = getResources().getStringArray(R.array.orders_title);
        final String[] mItemsValues = getResources().getStringArray(R.array.orders_values);
        int currentOrderType = Integer.valueOf(UtilPreferences.getOrderType(getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setTitle("Сортировка:")
                .setSingleChoiceItems(mItemsNames, currentOrderType - 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        sendResult(mItemsValues[item]);
                        dismiss();
                    }
                })
                .create();
    }
}
