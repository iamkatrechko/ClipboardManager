package com.iamkatrechko.clipboardmanager;

import android.content.Context;
import android.preference.PreferenceManager;

public class UtilPreferences {

    private static final String PREF_SHOW_ONLY_FAVORITE = "spOnlyFavorite";
    private static final String PREF_SHOW_META_IN_ADAPTER = "showMetaInAdapter";
    private static final String PREF_SPLIT_CHAR = "splitChar";



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
    public static String getSplitChar(Context context) {
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





    public static boolean getEnableService(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_ENABLE_SERVICE, true);
    }

    public static int getNotificationPriority(Context context){
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SettingsActivity.PREF_NOTIFICATION_PRIORITY, "1"));
    }

    public static boolean getDisplayNotification(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_DISPLAY_NOTIFICATION, true);
    }

    public static boolean getDisplayHistory(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivity.PREF_DISPLAY_HISTORY, true);
    }
}
