package com.iamkatrechko.clipboardmanager;

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

public class DialogSplitClips extends DialogFragment {

    private EditText etSplitChar;
    private CheckBox cbDelete;

    public static DialogSplitClips newInstance() {
        return new DialogSplitClips();
    }

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

        etSplitChar.setText(UtilPrefences.getSplitChar(getActivity()));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Объединение записей")
                .setPositiveButton("Объединить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int n) {
                        String splitChar = etSplitChar.getText().toString();
                        boolean deleteOldClips = cbDelete.isChecked();

                        UtilPrefences.setSplitChar(getActivity(), splitChar);

                        sendResult(splitChar, deleteOldClips);
                    }
                })
                .setNegativeButton("Отмена", null)
                .create();
    }
}
