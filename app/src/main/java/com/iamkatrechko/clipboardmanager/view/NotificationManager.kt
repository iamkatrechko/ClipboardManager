package com.iamkatrechko.clipboardmanager.view

import android.app.Notification
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues

/**
 * Менеджер уведомлений
 * @author iamkatrechko
 *         Date: 03.11.17
 */
class NotificationManager {

    /** Хранилище настроек программы */
    private val settings = SettingsValues.getInstance()

    /** Возвращает уведомление со списком записей */
    fun getNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.current_clipboard_text))
                .setContentText("> " + ClipUtils.getClipboardText(context))
                .setSmallIcon(R.drawable.ic_icon)

        if (settings.displayHistory) {
            builder.setCustomBigContentView(RemoteViewCreator.createHistoryRemoteView(context))
        }

        builder.priority = when (settings.notificationPriority.toInt()) {
            1 -> NotificationCompat.PRIORITY_MAX
            2 -> NotificationCompat.PRIORITY_HIGH
            3 -> NotificationCompat.PRIORITY_DEFAULT
            4 -> NotificationCompat.PRIORITY_LOW
            5 -> NotificationCompat.PRIORITY_MIN
            else -> NotificationCompat.PRIORITY_MAX
        }

        return builder.build()
    }
}