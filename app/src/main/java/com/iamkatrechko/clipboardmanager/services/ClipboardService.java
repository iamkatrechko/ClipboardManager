package com.iamkatrechko.clipboardmanager.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.activity.ClipEditActivity;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.Clip;
import com.iamkatrechko.clipboardmanager.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.util.UtilPreferences;

import static com.iamkatrechko.clipboardmanager.data.ClipboardDatabaseHelper.ClipCursor;

/**
 * Сервис для прослушки буфера обмена
 * @author iamkatrechko
 *         Date: 07.11.2016
 */
public class ClipboardService extends Service {

    /** Тег для логирования */
    private static final String TAG = ClipboardService.class.getSimpleName();

    /** Идентификатор уведомления сервиса */
    private static final int NOTIFICATION_ID = 98431;

    /** Команда отображения избранных заметок */
    private static final String ACTION_SHOW_ONLY_FAVORITE = "action_show_only_favorite";
    /** Команда копирования заметки в буфер */
    private static final String ACTION_COPY_TO_CLIPBOARD = "action_copy_to_clipboard";

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
            RemoteViews contentView = createGeneralRemoteViews();
            builder.setCustomBigContentView(contentView);
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

    private RemoteViews createGeneralRemoteViews() {
        boolean showOnlyFavorite = UtilPreferences.isShowOnlyFavoriteInNotification(this);

        RemoteViews generalRemoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        String currentClipText = ClipUtils.getClipboardText(this);

        generalRemoteViews.setTextViewText(R.id.tvCurrent, "> " + currentClipText);

        Intent intentAdd = new Intent(this, ClipEditActivity.class);
        intentAdd.setAction("ACTION_ADD");
        PendingIntent pIntentAdd = PendingIntent.getActivity(this, 612452, intentAdd, 0);
        generalRemoteViews.setOnClickPendingIntent(R.id.btnAdd, pIntentAdd);

        Intent intentFavorite = new Intent(this, ClipboardService.class);
        intentFavorite.setAction(ACTION_SHOW_ONLY_FAVORITE);
        PendingIntent pIntentFavorite = PendingIntent.getService(this, 171251, intentFavorite, 0);
        generalRemoteViews.setOnClickPendingIntent(R.id.ivStar, pIntentFavorite);

        ClipCursor lastRecords;
        if (showOnlyFavorite) {
            generalRemoteViews.setImageViewResource(R.id.ivStar, R.drawable.ic_star);
            generalRemoteViews.setInt(R.id.ivStar, "setColorFilter", Color.parseColor("#009688"));

            lastRecords = new ClipCursor(getContentResolver().query(Clip.CONTENT_URI,
                    null,
                    Clip.COLUMN_CONTENT + " <> ? AND " + Clip.COLUMN_IS_FAVORITE + " = ?",
                    new String[]{currentClipText, "1"},
                    Clip.COLUMN_DATE + " DESC LIMIT 4"));
        } else {
            generalRemoteViews.setImageViewResource(R.id.ivStar, R.drawable.ic_star_border);
            generalRemoteViews.setInt(R.id.ivStar, "setColorFilter", Color.parseColor("#808080"));

            lastRecords = new ClipCursor(getContentResolver().query(Clip.CONTENT_URI,
                    null,
                    Clip.COLUMN_CONTENT + " <> ?",
                    new String[]{currentClipText},
                    Clip.COLUMN_DATE + " DESC LIMIT 4"));
        }

        generalRemoteViews.removeAllViews(R.id.ListClips);


        for (int i = 0; i < lastRecords.getCount(); i++) {
            lastRecords.moveToPosition(i);
            long id = lastRecords.getID();
            String title = lastRecords.getTitle();
            RemoteViews clipRemoteViews = createClipListItem(id, title);
            if (i == 0) {
                clipRemoteViews.setViewVisibility(R.id.flSeparator, View.GONE);
            }
            generalRemoteViews.addView(R.id.ListClips, clipRemoteViews);
        }
        return generalRemoteViews;
    }

    private RemoteViews createClipListItem(long id, String text) {
        RemoteViews clipRemoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_list_item);
        clipRemoteViews.setTextViewText(R.id.tvTitle, text);

        Intent serviceCopy = new Intent(this, ClipboardService.class);
        serviceCopy.setAction(ACTION_COPY_TO_CLIPBOARD);
        serviceCopy.putExtra("id", id);
        PendingIntent pIntentCopy = PendingIntent.getService(this, (int) id, serviceCopy, 0);
        clipRemoteViews.setOnClickPendingIntent(R.id.ivCopy, pIntentCopy);

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
