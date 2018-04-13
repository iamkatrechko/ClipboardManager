package com.iamkatrechko.clipboardmanager.view

import android.app.Notification
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences

/**
 * Менеджер уведомлений
 * @author iamkatrechko
 *         Date: 03.11.17
 */
class NotificationManager {

    /**
     * Возвращает уведомление со списком записей
     * @param [context] контекст
     * @return уведомление со списком записей
     */
    fun getNotification(context: Context): Notification {
        val priority = UtilPreferences.getNotificationPriority(context)
        val displayHistory = SettingsValues.getInstance().displayHistory

        val builder = NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.current_clipboard_text))
                .setContentText("> " + ClipUtils.getClipboardText(context))
                .setSmallIcon(R.drawable.ic_icon)

        if (displayHistory) {
            builder.setCustomBigContentView(RemoteViewCreator.createHistoryRemoteView(context))
        }

        when (priority) {
            1 -> builder.priority = NotificationCompat.PRIORITY_MAX
            2 -> builder.priority = NotificationCompat.PRIORITY_HIGH
            3 -> builder.priority = NotificationCompat.PRIORITY_DEFAULT
            4 -> builder.priority = NotificationCompat.PRIORITY_LOW
            5 -> builder.priority = NotificationCompat.PRIORITY_MIN
        }

        return builder.build()
    }
}