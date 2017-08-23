package com.iamkatrechko.clipboardmanager.services;

import android.app.Notification;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.Clip;
import com.iamkatrechko.clipboardmanager.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.RemoteViewCreator;
import com.iamkatrechko.clipboardmanager.util.UtilPreferences;

import static com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper.ClipCursor;

/**
 * Сервис для прослушки буфера обмена
 * @author iamkatrechko
 *         Date: 07.11.2016
 */
public class ClipboardService extends Service {

    /** Команда отображения избранных заметок */
    public static final String ACTION_SHOW_ONLY_FAVORITE = "action_show_only_favorite";
    /** Команда копирования заметки в буфер */
    public static final String ACTION_COPY_TO_CLIPBOARD = "action_copy_to_clipboard";
    /** Тег для логирования */
    private static final String TAG = ClipboardService.class.getSimpleName();
    /** Идентификатор уведомления сервиса */
    private static final int NOTIFICATION_ID = 98431;
    /** Менеджер буфера обмена */
    private ClipboardManager clipBoard;

    /** Конструктор по умолчанию */
    public ClipboardService() {
    }

    /**
     * Запускает сервис
     * @param context контекст
     */
    public static void startMyService(Context context) {
        context.startService(new Intent(context, ClipboardService.class));
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d(TAG, "Отловлена новая запись в буфере обмена");
                String clipText = ClipUtils.getClipboardText(ClipboardService.this);
                String clipDescription = ClipUtils.getClipboardLabel(ClipboardService.this);

                if (clipDescription.equals(ClipUtils.CLIP_LABEL) || clipDescription.equals(ClipUtils.CLIP_LABEL_ACCESSIBILITY)) {
                    Toast.makeText(ClipboardService.this, "Отмена: копирование из приложения", Toast.LENGTH_SHORT).show();
                } else {
                    if (clipText.length() == 0) {
                        Toast.makeText(ClipboardService.this, "Пустая запись", Toast.LENGTH_SHORT).show();
                        ClipboardService.startMyService(ClipboardService.this);//Для обновления уведомления
                        return;
                    }
                    if (recordAlreadyExists(clipText)) {
                        Toast.makeText(ClipboardService.this, "Запись уже существует (в базе)", Toast.LENGTH_SHORT).show();
                        ClipboardService.startMyService(ClipboardService.this);//Для обновления уведомления
                        return;
                    }
                    addNewClip(clipText);
                }
                ClipboardService.startMyService(ClipboardService.this);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        String action = intent.getAction();
        if (action != null) {
            if (action.equals(ACTION_SHOW_ONLY_FAVORITE)) {
                boolean show = UtilPreferences.isShowOnlyFavoriteInNotification(this);
                UtilPreferences.setShowOnlyFavoriteInNotification(this, !show);
                startMyService(this);
            }
            if (action.equals(ACTION_COPY_TO_CLIPBOARD)) {
                long id = intent.getLongExtra("id", -1);
                Uri uri = Clip.buildClipUri(id);
                ClipCursor cursor = new ClipCursor(getContentResolver().query(uri, null, null, null, null));
                if (cursor.moveToFirst()) {
                    ClipUtils.copyToClipboard(this, cursor.getContent());
                }
                //TODO Сделать настройку "Если уже существует: ничего не делать || изменить дату на новую
                startMyService(this);
            }
            return Service.START_NOT_STICKY;
        }
        boolean displayNotification = UtilPreferences.getDisplayNotification(this);

        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        if (!displayNotification) {
            startService(HideNotificationService.newIntent(this, NOTIFICATION_ID));
        }/*else{
            startForeground(98431, notification);
        }*/

        return Service.START_NOT_STICKY;
    }

    /**
     * Проверка существования данной записи в базе данных
     * @param clipText Текст проверяемой записи
     * @return true - существует, false - не существует
     */
    private boolean recordAlreadyExists(String clipText) {
        Cursor cursor = getContentResolver().query(Clip.CONTENT_URI,
                null,
                Clip.COLUMN_CONTENT + " = ?",
                new String[]{clipText},
                null);
        return cursor != null && (cursor.getCount() != 0);
    }

    /**
     * Добавление новой записи в базу данных
     * @param content Содержимое записи
     */
    private void addNewClip(String content) {
        int titleLength = 25;

        ContentValues contentValues = Clip.getDefaultContentValues();
        if (content.length() < titleLength) {
            titleLength = content.length();
        }
        contentValues.put(Clip.COLUMN_TITLE, content.substring(0, titleLength));
        contentValues.put(Clip.COLUMN_CONTENT, content);

        Uri newClipUri = getContentResolver().insert(Clip.CONTENT_URI, contentValues);

        if (newClipUri != null) {
            startActivity(FloatingCancelViewService.newIntent(this, newClipUri.getLastPathSegment()));
        }
    }

    private Notification createNotification() {
        int notificationPriority = UtilPreferences.getNotificationPriority(this);
        boolean displayHistory = UtilPreferences.getDisplayHistory(this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.current_clipboard_text))
                .setContentText("> " + ClipUtils.getClipboardText(this))
                .setSmallIcon(R.drawable.ic_icon);

        if (displayHistory) {
            builder.setCustomBigContentView(RemoteViewCreator.createHistoryRemoteView(this));
        }

        switch (notificationPriority) {
            case 1:
                builder.setPriority(NotificationCompat.PRIORITY_MAX);
                break;
            case 2:
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                break;
            case 3:
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                break;
            case 4:
                builder.setPriority(NotificationCompat.PRIORITY_LOW);
                break;
            case 5:
                builder.setPriority(NotificationCompat.PRIORITY_MIN);
                break;
        }

        return builder.build();
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
