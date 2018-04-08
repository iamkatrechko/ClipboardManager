package com.iamkatrechko.clipboardmanager.domain.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository;

/**
 * Сервис для отображения кнопки "отмена сохранения записи"
 * @author iamkatrechko
 *         Date: 09.11.2016
 */
public class CancelViewService extends Service {

    /** Тег для логирования */
    final private static String TAG = CancelViewService.class.getSimpleName();

    /** Ключ идентификатора заметки для удаления */
    private static final String KEY_CLIP_ID = "KEY_CLIP_ID";

    /** Время проигрывания анимации исчезновения */
    private static final int ANIMATE_DURATION_TIME = 1000;
    /** Пауза перед началом анимации */
    private static final int ANIMATE_DELAY_TIME = 2000;
    /** Время, через которое закрывается диалог */
    private static final int ANIMATE_DELAY_TO_STOP_TIME = ANIMATE_DURATION_TIME + ANIMATE_DELAY_TIME;

    /** Менеджер экрана */
    private WindowManager windowManager;
    /** Репозиторий записей */
    private ClipboardRepository repository;

    /** Главный виджет диалога */
    private View mainView;
    /** Кнопка отмены */
    private LinearLayout linearCancel;
    /** Действие закрытия сервиса */
    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                windowManager.removeView(mainView);
                stopSelf();
            } catch (Exception ignored) {
            }
        }
    };

    /**
     * Возвращает интент
     * @param context контекст
     * @param clipId  идентификатор заметки в БД
     * @return интент
     */
    public static Intent newIntent(Context context, String clipId) {
        Intent intent = new Intent(context, CancelViewService.class);
        intent.putExtra(KEY_CLIP_ID, Long.valueOf(clipId));
        return intent;
    }

    @Override
    public void onCreate() {
        repository = ClipboardRepository.Companion.getInstance();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        final long deleteClipId = intent.getLongExtra(KEY_CLIP_ID, -1);
        if (deleteClipId == -1) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        mainView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_cancel_view, null);
        linearCancel = (LinearLayout) mainView.findViewById(R.id.linear_cancel);

        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(mainView);
                repository.deleteClip(getApplicationContext(), deleteClipId);
                Toast.makeText(getApplicationContext(), getString(R.string.deleted) + deleteClipId, Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });

        showCancelView();
        return Service.START_NOT_STICKY;
    }

    /** Отображает кнопку отмены сохранения записи */
    private void showCancelView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
                Math.round(340 * displayMetrics.density),
                Math.round(48 * displayMetrics.density),
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        myParams.x = 0;
        myParams.y = size.y / 3;
        windowManager.addView(mainView, myParams);
        mainView.animate()
                .alphaBy(1.0f)
                .alpha(0.0f)
                .setDuration(ANIMATE_DURATION_TIME)
                .setStartDelay(ANIMATE_DELAY_TIME);

        new Handler().postDelayed(stopRunnable, ANIMATE_DELAY_TO_STOP_TIME);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved");
    }
}
