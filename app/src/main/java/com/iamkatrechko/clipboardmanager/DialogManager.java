package com.iamkatrechko.clipboardmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.iamkatrechko.clipboardmanager.dialogs.DialogCategoryDelete;
import com.iamkatrechko.clipboardmanager.dialogs.DialogCategoryEdit;
import com.iamkatrechko.clipboardmanager.dialogs.DialogChangeCategory;
import com.iamkatrechko.clipboardmanager.dialogs.DialogSplitClips;

class DialogManager {
    static final int DIALOG_SPLIT_CLIPS = 611752;
    static final int DIALOG_DELETE = 823663;
    static final int DIALOG_ADD = 734723;
    static final int DIALOG_EDIT = 125812;
    static final int DIALOG_CHANGE_CATEGORY = 121262;

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
}
