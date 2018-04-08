package com.iamkatrechko.clipboardmanager.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;

/**
 * Диалог объединения заметок в одну
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DialogSplitClips extends DialogFragment {

    /** Текстовое поле с разделитетем */
    private EditText etSplitChar;
    /** Чекбокс необходимости удаления объединенных цитат */
    private CheckBox cbDelete;

    /**
     * Возвращает новый экземпляр диалога
     * @return новый экземпляр диалога
     */
    public static DialogSplitClips newInstance() {
        return new DialogSplitClips();
    }

    /**
     * Возвращает данные обратно во фрагмент
     * @param splitChar      символ-разделитель
     * @param deleteOldClips флаг удаления объединенных заметок
     */
    private void sendResult(String splitChar, boolean deleteOldClips) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent a = new Intent();
        a.putExtra("splitChar", splitChar);
        a.putExtra("deleteOldClips", deleteOldClips);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, a);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_split_clips, null);

        etSplitChar = (EditText) view.findViewById(R.id.etSplitChar);
        cbDelete = (CheckBox) view.findViewById(R.id.cbDelete);

        etSplitChar.setText(UtilPreferences.getSeparator(getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.title_split_items)
                .setPositiveButton(R.string.split, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int n) {
                        String splitChar = etSplitChar.getText().toString();
                        boolean deleteOldClips = cbDelete.isChecked();

                        UtilPreferences.setSplitChar(getActivity(), splitChar);

                        sendResult(splitChar, deleteOldClips);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
