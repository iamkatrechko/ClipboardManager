package com.iamkatrechko.clipboardmanager.domain.util;

import android.content.Context;
import android.preference.PreferenceManager;

import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType;
import com.iamkatrechko.clipboardmanager.view.activity.SettingsActivity;

/**
 * Утилиты по работы с SharedPreferences
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class UtilPreferences {

    private static final String PREF_SHOW_ONLY_FAVORITE = "spOnlyFavorite";
    private static final String PREF_SHOW_ONLY_FAVORITE_IN_NOTIFICATION = "spOnlyFavoriteInNotification";
    private static final String PREF_SHOW_META_IN_ADAPTER = "showMetaInAdapter";
    private static final String PREF_SPLIT_CHAR = "splitChar";
    private static final String PREF_ORDER_TYPE = "order_type";

    /**
     * Отображатся ли в списке записей только избранные
     */
    public static boolean isShowOnlyFavorite(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SHOW_ONLY_FAVORITE, false);
    }

    /**
     * Сохраняет булевую запись о том, отображаются ли в списке записей только избранные
     */
    public static void setShowOnlyFavorite(Context context, boolean isOnlyFavorite) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SHOW_ONLY_FAVORITE, isOnlyFavorite)
                .apply();
    }

    /**
     * Отображаются ли в адаптере всех записей метаданные
     */
    public static boolean isShowMetaInAdapter(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SHOW_META_IN_ADAPTER, false);
    }

    /**
     * Сохраняет булевую запись о том, отображаются ли в адаптере всех записей метаданные
     */
    public static void setShowMetaInAdapter(Context context, boolean showMeta) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SHOW_META_IN_ADAPTER, showMeta)
                .apply();
    }

    /**
     * Возвращает строку-разделитель, использующуюся для соединения нескольких записей
     */
    public static String getSeparator(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SPLIT_CHAR, " | ");
    }

    /**
     * Сохраняет строку-разделитель, использующуюся для соединения нескольких записей
     */
    public static void setSplitChar(Context context, String splitChar) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SPLIT_CHAR, splitChar)
                .apply();
    }

    /**
     * Отображатся ли в списке записей только избранные (в уведомлении)
     */
    public static boolean isShowOnlyFavoriteInNotification(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SHOW_ONLY_FAVORITE_IN_NOTIFICATION, false);
    }

    /**
     * Сохраняет булевую запись о том, отображаются ли в списке записей только избранные (в уведомлении)
     */
    public static void setShowOnlyFavoriteInNotification(Context context, boolean isOnlyFavorite) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SHOW_ONLY_FAVORITE_IN_NOTIFICATION, isOnlyFavorite)
                .apply();
    }

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

    public static int getNotificationPriority(Context context) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.PREF_NOTIFICATION_PRIORITY, "1"));
    }

    public static boolean getShowSaveDialogBeforeExit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_SHOW_SAVE_DIALOG_BEFORE_EXIT, true);
    }
}
