package com.iamkatrechko.clipboardmanager.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.iamkatrechko.clipboardmanager.R;

public class HideNotificationService extends Service {
    public HideNotificationService() {
    }

    @Override
    public void onCreate() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_icon);
        Notification notification;
        notification = builder.build();
        startForeground(98431, notification);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
