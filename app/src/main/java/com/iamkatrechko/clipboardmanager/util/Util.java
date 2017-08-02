package com.iamkatrechko.clipboardmanager.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.iamkatrechko.clipboardmanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Набор утилитных методов
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class Util {

    /**
     * Переводит DP в PX
     * @param context  контекст
     * @param dipValue количество DP
     * @return количество PX
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * Переводит PX в DP
     * @param context контекст
     * @param pxValue количество PX
     * @return количество DP
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Отправляет текст письмом
     * @param shareText Содержимое письма
     */
    public static void shareText(Context context, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.select_app_to_share)));
    }

    //FIXME Удалить секунды и привести дату к читаемому формату (запись в одном, вывод в другом)
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.getTimeInMillis());
    }

    /**
     * Возвращает дату в формате "dd MMMM - HH:mm"
     * @param timeInMillis миллисекунды
     * @return дата в формате "dd MMMM - HH:mm"
     */
    public static String getTimeInString(String timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df;
        try {
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(Long.valueOf(timeInMillis));
            if (calendar.get(Calendar.YEAR) == currentYear) {
                df = new SimpleDateFormat("dd MMMM - HH:mm");
            } else {
                df = new SimpleDateFormat("dd MMMM yyyy - HH:mm");
            }
            return df.format(calendar.getTime());
        } catch (Exception e) {
            return timeInMillis;
        }
    }

    /**
     * Определяет, запущен ли сервис
     * @param context      контекст
     * @param serviceClass класс сервиса
     * @return да или нет
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Определяет, запущен ли {@link android.accessibilityservice.AccessibilityService}
     * @param context контекст
     * @return да или нет
     */
    public static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager manager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        for (AccessibilityServiceInfo serviceInfo : manager.getEnabledAccessibilityServiceList(-1)) {
            if (serviceInfo.getId().contains("com.iamkatrechko.clipboardmanager/.services.MyAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkSupportActionPaste(AccessibilityNodeInfo source) {
        int supportedActions = source.getActions();
        return (supportedActions & AccessibilityNodeInfoCompat.ACTION_PASTE) == AccessibilityNodeInfoCompat.ACTION_PASTE;
    }
}
