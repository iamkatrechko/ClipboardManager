package com.iamkatrechko.clipboardmanager.domain.util

import android.app.ActivityManager
import android.content.Context
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Утилиты по работе с сервисами
 * @author iamkatrechko
 *         Date: 07.11.17
 */
object ServiceUtils {

    /**
     * Определяет, запущен ли сервис
     * @param context      контекст
     * @param serviceClass класс сервиса
     * @return да или нет
     */
    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    /**
     * Определяет, запущен ли [android.accessibilityservice.AccessibilityService]
     * @param context контекст
     * @return да или нет
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        for (serviceInfo in manager.getEnabledAccessibilityServiceList(-1)) {
            if (serviceInfo.id.contains("com.iamkatrechko.clipboardmanager/.services.MyAccessibilityService")) {
                return true
            }
        }
        return false
    }

    fun checkSupportActionPaste(source: AccessibilityNodeInfo): Boolean {
        val supportedActions = source.actions
        return supportedActions and AccessibilityNodeInfoCompat.ACTION_PASTE == AccessibilityNodeInfoCompat.ACTION_PASTE
    }
}