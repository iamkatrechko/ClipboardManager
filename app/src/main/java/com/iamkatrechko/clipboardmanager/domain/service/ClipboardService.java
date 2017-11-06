package com.iamkatrechko.clipboardmanager.domain.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.data.model.SimpleClip;
import com.iamkatrechko.clipboardmanager.view.NotificationManager;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository;
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;

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
    /** Репозиторий с записями в базе данных */
    private ClipboardRepository repository;
    /** Слушатель новых записей в буфере обмена */
    private ClipboardManager.OnPrimaryClipChangedListener clipListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            Log.d(TAG, "Отловлена новая запись в буфере обмена");
            SimpleClip clip = ClipUtils.getClip(ClipboardService.this);

            Context context = getApplicationContext();
            if (clip.getLabel().equals(ClipUtils.CLIP_LABEL) || clip.getLabel().equals(ClipUtils.CLIP_LABEL_ACCESSIBILITY)) {
                Log.d(TAG, "Отмена: копирование из приложения");
                Toast.makeText(context, "Отмена: копирование из приложения", Toast.LENGTH_SHORT).show();
            } else {
                if (clip.getText().length() == 0) {
                    Toast.makeText(context, R.string.empty_record, Toast.LENGTH_SHORT).show();
                    // Обновляем уведомление
                    ClipboardService.startMyService(context);
                    return;
                }
                if (repository.alreadyExists(context, clip.getText())) {
                    //TODO Сделать настройку "Если уже существует: ничего не делать || изменить дату на новую
                    Toast.makeText(context, R.string.record_already_exists, Toast.LENGTH_SHORT).show();
                    // Обновляем уведомление
                    ClipboardService.startMyService(context);
                    return;
                }
                addNewClip(clip.getText());
            }
            // Обновляем уведомление
            ClipboardService.startMyService(context);
        }
    };

    /**
     * Запускает сервис
     * @param context контекст
     */
    public static void startMyService(Context context) {
        context.startService(new Intent(context, ClipboardService.class));
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        repository = ClipboardRepository.Companion.getInstance();
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(clipListener);
        // TODO Отображать уведомление при запуске
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
                String text = repository.getClip(this, id);
                if (text != null) {
                    ClipUtils.copyToClipboard(this, text);
                }
                startMyService(this);
            }
            return Service.START_NOT_STICKY;
        }
        boolean displayNotification = UtilPreferences.getDisplayNotification(this);

        startForeground(NOTIFICATION_ID, new NotificationManager().getNotification(this));

        if (!displayNotification) {
            startService(HideNotificationService.newIntent(this, NOTIFICATION_ID));
        }/*else{
            startForeground(98431, notification);
        }*/

        return Service.START_NOT_STICKY;
    }

    /**
     * Добавление новой записи в базу данных
     * @param text текст записи
     */
    private void addNewClip(String text) {
        Uri newClipUri = repository.addClip(this, text);
        if (newClipUri != null) {
            startService(CancelViewService.newIntent(this, newClipUri.getLastPathSegment()));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Не реализовано");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        clipBoard.removePrimaryClipChangedListener(clipListener);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
    }
}
