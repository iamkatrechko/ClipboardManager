package com.iamkatrechko.clipboardmanager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.iamkatrechko.clipboardmanager.dialogs.DialogCategoryDelete;
import com.iamkatrechko.clipboardmanager.dialogs.DialogCategoryEdit;
import com.iamkatrechko.clipboardmanager.dialogs.DialogChangeCategory;
import com.iamkatrechko.clipboardmanager.dialogs.DialogDeleteConfirm;
import com.iamkatrechko.clipboardmanager.dialogs.DialogEnableAccessibility;
import com.iamkatrechko.clipboardmanager.dialogs.DialogSaveClip;
import com.iamkatrechko.clipboardmanager.dialogs.DialogSetOrderType;
import com.iamkatrechko.clipboardmanager.dialogs.DialogSplitClips;

class DialogManager {
    static final int DIALOG_SPLIT_CLIPS = 611752;
    static final int DIALOG_DELETE = 823663;
    static final int DIALOG_ADD = 734723;
    static final int DIALOG_EDIT = 125812;
    static final int DIALOG_CHANGE_CATEGORY = 121262;
    static final int DIALOG_CANCEL_CHANGES = 465444;
    static final int DIALOG_DELETE_CONFIRM = 621262;
    static final int DIALOG_SET_ORDER_TYPE = 361372;
    static final int DIALOG_ENABLE_ACCESSIBILITY = 171251;

    static void showDialogSplitClips(Fragment fragment){
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogSplitClips fragmentDialog = DialogSplitClips.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_SPLIT_CLIPS);
        fragmentDialog.show(fragmentManager, "DIALOG_SPLIT_CLIPS");
    }

    static void showDialogCategoryDelete(Fragment fragment, long categoryId){
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogCategoryDelete fragmentDialog = DialogCategoryDelete.newInstance(categoryId);
        fragmentDialog.setTargetFragment(fragment, DIALOG_DELETE);
        fragmentDialog.show(fragmentManager, "dialog_delete_category");
    }

    static void showDialogCategoryAdd(Fragment fragment){
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogCategoryEdit fragmentDialog = DialogCategoryEdit.newInstance(-1);
        fragmentDialog.setTargetFragment(fragment, DIALOG_ADD);
        fragmentDialog.show(fragmentManager, "dialog_category_edit");
    }

    static void showDialogCategoryEdit(Fragment fragment, long categoryId){
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogCategoryEdit fragmentDialog = DialogCategoryEdit.newInstance(categoryId);
        fragmentDialog.setTargetFragment(fragment, DIALOG_EDIT);
        fragmentDialog.show(fragmentManager, "dialog_category_edit");
    }

    static void showDialogChangeCategory(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogChangeCategory fragmentDialog = DialogChangeCategory.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_CHANGE_CATEGORY);
        fragmentDialog.show(fragmentManager, "dialog_change_category");
    }

    static void showDialogCancel(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogSaveClip fragmentDialog = DialogSaveClip.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_CANCEL_CHANGES);
        fragmentDialog.show(fragmentManager, "DIALOG_CANCEL_CHANGES");
    }

    static void showDialogDeleteConfirm(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogDeleteConfirm fragmentDialog = DialogDeleteConfirm.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_DELETE_CONFIRM);
        fragmentDialog.show(fragmentManager, "DIALOG_CANCEL_CHANGES");
    }

    static void showDialogSetOrderType(Fragment fragment) {
        FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogSetOrderType fragmentDialog = DialogSetOrderType.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_SET_ORDER_TYPE);
        fragmentDialog.show(fragmentManager, "DIALOG_SET_ORDER_TYPE");
    }

    static void showDialogEnableAccessibility(final Activity activity) {
        /*FragmentManager fragmentManager = fragment.getActivity().getSupportFragmentManager();
        DialogEnableAccessibility fragmentDialog = DialogEnableAccessibility.newInstance();
        fragmentDialog.setTargetFragment(fragment, DIALOG_ENABLE_ACCESSIBILITY);
        fragmentDialog.show(fragmentManager, "DIALOG_ENABLE_ACCESSIBILITY");*/
        new AlertDialog.Builder(activity)
                .setTitle("Название")
                .setMessage("Для включения необходимо перейти в настройки и включить специальную слежбу блаблабла")
                .setPositiveButton("Перейти",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                activity.startActivityForResult(intent, 122161);
                            }
                        })
                /*.setNeutralButton("Гав",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })*/
                .setNegativeButton("Отмена", null).create().show();
    }
}
