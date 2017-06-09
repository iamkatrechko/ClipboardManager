package com.iamkatrechko.clipboardmanager.util;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.jar.Manifest;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Util {

    public static int dp2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    /**
     * Отправляет текст письмом
     * @param shareText Содержимое письма
     */
    public static void shareText(Context context, String shareText){
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.select_app_to_share)));
    }

    //FIXME Удалить секунды и привести дату к читаемому формату (запись в одном, вывод в другом)
    public static String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static String getTimeInString(String timeInMillis){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df;
        try {
            int currentYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(Long.valueOf(timeInMillis));
            if (calendar.get(Calendar.YEAR) == currentYear) {
                df = new SimpleDateFormat("dd MMMM - HH:mm");
            }else {
                df = new SimpleDateFormat("dd MMMM yyyy - HH:mm");
            }
            return df.format(calendar.getTime());
        }catch (Exception e){
            return timeInMillis;
        }
    }

    public static void copyToClipboard(Context context, String copyText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("891652", copyText);
        clipboard.setPrimaryClip(clip);
    }

    public static CharSequence getClipboardCharText(Context context){
        ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if (clipBoard.hasPrimaryClip()){
            return clipBoard.getPrimaryClip().getItemAt(0).getText();
        }else{
            return "";
        }
        //if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)))
    }

    public static String getClipboardText(Context context) {
        CharSequence clipText = getClipboardCharText(context);

        return clipText != null ? clipText.toString() : "";
    }

    public static String getClipboardLabel(Context context){
        ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        CharSequence label = clipBoard.getPrimaryClipDescription().getLabel();

        if (label != null){
            return label.toString();
        }else{
            return "";
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        for (AccessibilityServiceInfo id : ((AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE)).getEnabledAccessibilityServiceList(-1)) {
            if (id.getId().contains("com.iamkatrechko.clipboardmanager/.services.MyAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkSupportActionPaste(AccessibilityNodeInfo source){
        int supportedActions = source.getActions();
        return  (supportedActions & AccessibilityNodeInfoCompat.ACTION_PASTE) == AccessibilityNodeInfoCompat.ACTION_PASTE;
    }

    public static void sendClipToMyAccessibilityService(Context context, String text){
        ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText("126126126", text);
        clipBoard.setPrimaryClip(clip);
    }
}
