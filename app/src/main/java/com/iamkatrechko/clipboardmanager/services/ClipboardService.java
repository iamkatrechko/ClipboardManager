package com.iamkatrechko.clipboardmanager.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.Util;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.UtilPreferences;
import com.iamkatrechko.clipboardmanager.widget.WidgetService;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class ClipboardService extends Service {
    private static final String TAG = "ClipboardService";

    private ClipboardManager clipBoard;

    public ClipboardService() {
    }

    public static void startMyService(Context context){
        startMyService(context, UtilPreferences.getDisplayNotification(context), UtilPreferences.getNotificationPriority(context));
    }

    public static void startMyService(Context context, boolean displayNotification){
        startMyService(context, displayNotification, UtilPreferences.getNotificationPriority(context));
    }

    public static void startMyService(Context context, int notificationPriority){
        startMyService(context, UtilPreferences.getDisplayNotification(context), notificationPriority);

    }

    public static void startMyService(Context context, boolean displayNotification, int notificationPriority){
        Intent i = new Intent(context, ClipboardService.class);
        i.putExtra("displayNotification", displayNotification);
        i.putExtra("notificationPriority", notificationPriority);
        context.startService(i);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        final Intent intent2 = intent;
        String action = intent.getAction();
        if (action != null){
            if (action.equals("ACTION_1")){
                Log.d(TAG, "Нажата кнопочка");
            }
            return Service.START_NOT_STICKY;
        }
        boolean displayNotification = intent.getBooleanExtra("displayNotification", true);

        Notification notification = createNotification(intent);
        startForeground(98431, notification);

        //здесь вы можете запустить новый поток или задачу
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d(TAG, "Новая запись");
                String clipText = Util.getClipboardText(getApplicationContext());
                String clipDescription = Util.getClipboardLabel(getApplicationContext());

                if (clipDescription.equals("891652") || clipDescription.equals("126126126")){
                    Toast.makeText(getApplicationContext(), "Отмена: копирование из приложения", Toast.LENGTH_SHORT).show();
                }else{
                    /*Uri uri = Clip.CONTENT_URI;
                    Cursor cursor = getContentResolver().query(uri,
                            new String[]{Clip._ID, Clip.COLUMN_CONTENT},
                            null,
                            null,
                            Clip._ID + " ASC");*/
                    if (clipText.length() == 0){
                        Toast.makeText(getApplicationContext(), "Пустая запись", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (recordAlreadyExists(clipText)){
                        Toast.makeText(getApplicationContext(), "Запись уже существует (в базе)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addNewClip(clipText);
                    /*if (cursor.moveToLast()){
                        if (cursor.getString(1).equals(clipText)){
                            Toast.makeText(getApplicationContext(), "Запись уже существует (последняя)", Toast.LENGTH_SHORT).show();
                        }else{
                        }
                    }else{
                        addNewClip(clipText);
                    }*/
                }

                ClipboardService.startMyService(getApplicationContext());
            }
        });

        if (!displayNotification) {
            Intent hideIntent = new Intent(this, HideNotificationService.class);
            startService(hideIntent);
        }/*else{
            startForeground(98431, notification);
        }*/

        return Service.START_NOT_STICKY;
    }

    private boolean recordAlreadyExists(String clipText) {
        Cursor cursor = getContentResolver().query(Clip.CONTENT_URI,
                null,
                Clip.COLUMN_CONTENT + " = ?",
                new String[]{clipText},
                null);
        return cursor != null && (cursor.getCount() != 0);
    }

    private void addNewClip(String content){
        int titleLength = 25;
        String formattedDate = Util.getCurrentTime();

        ContentValues contentValues = Clip.getDefaultContentValues();
        if (content.length() < titleLength) titleLength = content.length();
        contentValues.put(Clip.COLUMN_TITLE, content.substring(0, titleLength));
        contentValues.put(Clip.COLUMN_CONTENT, content);
        contentValues.put(Clip.COLUMN_DATE, formattedDate);

        Uri newClipUri = getContentResolver().insert(Clip.CONTENT_URI, contentValues);

        if (newClipUri != null) {
            Intent intent = new Intent(getApplicationContext(), FloatingCancelViewService.class);
            intent.putExtra("clipId", Long.valueOf(newClipUri.getLastPathSegment()));
            startService(intent);
        }
    }

    private Notification createNotification(Intent intent){
        int notificationPriority = intent.getIntExtra("notificationPriority", 1);

        RemoteViews contentView = createGeneralRemoteViews();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Clipboard Manager")
                .setContentText(Util.getClipboardText(getApplicationContext()))
                .setSmallIcon(R.drawable.ic_launcher)
                .setCustomBigContentView(contentView);

        switch (notificationPriority) {
            case 1:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                break;
            case 2:
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                break;
            case 3:
                builder.setPriority(NotificationCompat.PRIORITY_MIN);
                break;
        }

        return builder.build();
    }

    private RemoteViews createGeneralRemoteViews() {
        RemoteViews generalRemoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);

        generalRemoteViews.setTextViewText(R.id.tvCurrent, Util.getClipboardText(getApplicationContext()));

        Intent activeRefresh = new Intent(getApplicationContext(), ClipboardService.class);                                                       //Настройка интента слушателя для кнопки "обновить"
        activeRefresh.setAction("ACTION_1");                                                                //Установка метки для интента
        PendingIntent pendingIntentUpdateC = PendingIntent.getService(getApplicationContext(), 0, activeRefresh, 0);
        generalRemoteViews.setOnClickPendingIntent(R.id.button6, pendingIntentUpdateC);

        generalRemoteViews.removeAllViews(R.id.ListClips);
        for (int i = 0; i < 5; i++){
            generalRemoteViews.addView(R.id.ListClips, createClipListItem("Запись №" + i));
        }
        return generalRemoteViews;
    }

    private RemoteViews createClipListItem(String text){
        RemoteViews clipRemoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_list_item);
        clipRemoteViews.setTextViewText(R.id.tvTitle, text);
        return clipRemoteViews;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipBoard.removePrimaryClipChangedListener(null);
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
    }
}
