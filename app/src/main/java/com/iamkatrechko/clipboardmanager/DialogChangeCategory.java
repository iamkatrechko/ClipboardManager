package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.iamkatrechko.clipboardmanager.data.DatabaseDescription;
import com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper.*;

public class DialogChangeCategory extends DialogFragment {

    public static DialogChangeCategory newInstance() {
        return new DialogChangeCategory();
    }

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
        CategoryCursor categories = new CategoryCursor(getActivity().getContentResolver().query(DatabaseDescription.Category.CONTENT_URI,
                null,
                null,
                null,
                null));

        final String[] mItemsNames = new String[categories.getCount()];
        final long[] mItemsValues = new long[categories.getCount()];
        for (int i = 0; i < categories.getCount(); i++){
            categories.moveToPosition(i);
            mItemsNames[i] = categories.getTitle();
            mItemsValues[i] = categories.getID();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Переместить в:");
        builder.setItems(mItemsNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                sendResult(mItemsValues[item]);
            }
        });
        return builder.create();
    }
}

