package com.iamkatrechko.clipboardmanager.domain.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.iamkatrechko.clipboardmanager.R;

/**
 * Сервис для скрытия уведомления другого сервиса
 * @author iamkatrechko
 *         Date: 08.11.2016
 */
public class HideNotificationService extends Service {

    /** Ключ идентификатора уведомления */
    private static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";

    /**
     * Возвращает интент сервиса
     * @param context        контекст
     * @param notificationId идентификатор уведомления
     * @return интент сервиса
     */
    public static Intent newIntent(Context context, int notificationId) {
        Intent hideIntent = new Intent(context, HideNotificationService.class);
        hideIntent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        return hideIntent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_icon)
                .build();
        startForeground(intent.getIntExtra(KEY_NOTIFICATION_ID, 0), notification);
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
