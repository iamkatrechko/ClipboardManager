package com.iamkatrechko.clipboardmanager.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription;

public class FloatingCancelViewService extends Service {
    final private static String TAG = "CancelViewService";

    final private static int ANIMATE_DURATION = 1000;
    final private static int ANIMATE_DELAY = 2000;
    final private static int ANIMATE_DELAY_TO_STOP = ANIMATE_DURATION + ANIMATE_DELAY;


    private WindowManager windowManager;

    private View mainView;
    private TextView tvInfo;
    private ImageView ivCancel;
    private LinearLayout linearCancel;
    private ContentResolver mContentResolver;

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Запуск сервиса");
        final long deleteClipId = intent.getLongExtra("clipId", -1);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mContentResolver = getContentResolver();

        mainView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_cancel_view, null);

        tvInfo = (TextView) mainView.findViewById(R.id.tvInfo);
        linearCancel = (LinearLayout) mainView.findViewById(R.id.linearCancel);

        //tvInfo.setText("" + deleteClipId);
        tvInfo.setText("Добавлено в менеджер буфера");
        /////
        final Uri uriDelete = DatabaseDescription.Clip.buildClipUri(deleteClipId);

        linearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(mainView);

                if (deleteClipId != -1) {
                    mContentResolver.delete(uriDelete, null, null);
                }

                Toast.makeText(getApplicationContext(), "Удалено: " + deleteClipId, Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
                Math.round(340 * displayMetrics.density),
                Math.round(48 * displayMetrics.density),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        //Log.d(TAG, String.valueOf(size.y));

        myParams.x = 0;
        myParams.y = size.y / 3;

        windowManager.addView(mainView, myParams);

        mainView.animate().alphaBy(1.0f).alpha(0.0f).setDuration(ANIMATE_DURATION).setStartDelay(ANIMATE_DELAY);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    windowManager.removeView(mainView);
                    stopSelf();
                } catch (Exception ignored) {
                }
            }
        }, ANIMATE_DELAY_TO_STOP);
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "Service: onTaskRemoved");
    }
}
