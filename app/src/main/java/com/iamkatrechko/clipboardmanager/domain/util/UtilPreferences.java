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

    private static final String PREF_ORDER_TYPE = "order_type";

    /**
     * Возвращает тип сортировки записей
     * @return 1 - пользовательский, 2 - по дате (сначала новые), 3 - по дате (сначала старые)
     */
    public static OrderType getOrderType(Context context) {
        int pos = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getInt(PREF_ORDER_TYPE, 0);
        return OrderType.values()[pos];
    }

    /**
     * Сохраняет тип сортировки записей
     */
    public static void setOrderType(Context context, OrderType orderType) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_ORDER_TYPE, orderType.ordinal())
                .apply();
    }

    public static boolean getShowSaveDialogBeforeExit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsFragment.PREF_SHOW_SAVE_DIALOG_BEFORE_EXIT, true);
    }
}
