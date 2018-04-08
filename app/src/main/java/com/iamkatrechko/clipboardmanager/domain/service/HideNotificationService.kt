package com.iamkatrechko.clipboardmanager.domain.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

import com.iamkatrechko.clipboardmanager.R

/**
 * Сервис для скрытия уведомления другого сервиса
 * @author iamkatrechko
 *         Date: 08.11.2016
 */
class HideNotificationService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_icon)
                .build()
        startForeground(intent.getIntExtra(KEY_NOTIFICATION_ID, 0), notification)
        stopForeground(true)
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        /** Ключ идентификатора уведомления  */
        private const val KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID"

        /**
         * Возвращает интент сервиса
         * @param context        контекст
         * @param notificationId идентификатор уведомления
         * @return интент сервиса
         */
        fun newIntent(context: Context, notificationId: Int): Intent {
            val hideIntent = Intent(context, HideNotificationService::class.java)
            hideIntent.putExtra(KEY_NOTIFICATION_ID, notificationId)
            return hideIntent
        }
    }
}
