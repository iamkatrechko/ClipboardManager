package com.iamkatrechko.clipboardmanager.domain.util;

import android.content.Context;
import android.preference.PreferenceManager;

import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType;
import com.iamkatrechko.clipboardmanager.view.activity.SettingsActivity;
import com.iamkatrechko.clipboardmanager.view.fragment.SettingsFragment;

/**
 * Утилиты по работы с SharedPreferences
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class UtilPreferences {

    public static boolean getShowSaveDialogBeforeExit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsFragment.PREF_SHOW_SAVE_DIALOG_BEFORE_EXIT, true);
    }
}
