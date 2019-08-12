package com.iamkatrechko.clipboardmanager

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Менеджер каналов уведомлений
 * @author iamkatrechko
 *         Date: 12.08.19
 *
 * @param ctx контекст
 */
class NotificationsChannelsManager(
        ctx: Context
) {

    /** Сервис уведомлений */
    var notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** Создает основной канал уведомлений */
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.O)
    fun createMainChannel(): NotificationChannel {
        val channelId = "main"
        return notificationManager.getNotificationChannel(channelId)
                ?: NotificationChannel(channelId, "Главное уведомление", NotificationManager.IMPORTANCE_DEFAULT)
                        .apply {
                            enableLights(false)
                            enableVibration(false)
                        }
                        .also { notificationManager.createNotificationChannel(it) }
    }
}