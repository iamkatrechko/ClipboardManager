package com.iamkatrechko.clipboardmanager.domain.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor;
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.view.adapter.ClipsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Category;
import static com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Clip;

/**
 * Плавающий виджет со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class FloatingViewService extends Service {

    /** Тег для логирования */
    private static final String TAG = FloatingViewService.class.getSimpleName();

    /** Менеджер экрана */
    private WindowManager windowManager;

    /** Главный виджет */
    private View mainView;
    /** Виджет списка с записями */
    private RecyclerView recyclerView;
    /** Кнопка закрытия окна */
    private ImageView ivClose;
    /** Кнопка перемещения окна */
    private ImageView ivMove;
    /** Выпадающий список с категориями записей */
    private Spinner spinner;

    /** Адаптер списка записей */
    private ClipsAdapter cursorAdapter;

    /** Адаптер выпадающего списка */
    private ArrayAdapter<CharSequence> spinnerAdapter;
    /** Загручик списка записей */
    private CursorLoader cursorLoader;

    private Loader.OnLoadCompleteListener<Cursor> loaderListener = new Loader.OnLoadCompleteListener<Cursor>() {
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            cursorAdapter.setCursor(data);
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mainView = LayoutInflater.from(this).inflate(R.layout.float_view, null);
        cursorLoader = new CursorLoader(getApplicationContext(), Clip.CONTENT_URI, null, null, null, Clip._ID + " DESC");

        recyclerView = (RecyclerView) mainView.findViewById(R.id.recyclerView);
        ivClose = (ImageView) mainView.findViewById(R.id.ivClose);
        ivMove = (ImageView) mainView.findViewById(R.id.ivMove);
        spinner = (Spinner) mainView.findViewById(R.id.spinner);

        final ContentResolver cr = getContentResolver();

        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        // Улучшает быстродействие, если размер макета RecyclerView не изменяется
        recyclerView.setHasFixedSize(true);
        cursorAdapter = new ClipsAdapter(new ClipsAdapter.ClipClickListener() {
            @Override
            public void onClick(long clipId) {
                ClipCursor c = new ClipCursor(cr.query(Clip.buildClipUri(clipId), null, null, null, null));
                c.moveToFirst();

                ClipUtils.sendClipToMyAccessibilityService(getApplicationContext(), c.getContent());
                Toast.makeText(getApplicationContext(), "Id = " + clipId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectedChange(boolean isSelectedMode, int selectedCount) {

            }
        });
        // TODO Добавить пустой view
        recyclerView.setAdapter(cursorAdapter);

        cursorLoader.registerListener(125125, loaderListener);
        cursorLoader.startLoading();

        //Cursor clips = cr.query(Clip.CONTENT_URI, null, null, null, Clip._ID + " DESC");
        //////////////////
        List<String> list = new ArrayList<String>();
        Uri uri = Category.CONTENT_URI;
        Cursor c = cr.query(uri, null, null, null, null);
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            list.add(c.getString(1));
        }
        String[] s = new String[list.size()];
        s = list.toArray(s);
        c.close();
        spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, s);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //////////////////

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
                Math.round(320 * displayMetrics.density),
                Math.round(320 * displayMetrics.density),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.START;
        myParams.x = 300;
        myParams.y = 600;

        // add a floatingfacebubble icon in window
        /*final TextView textView = (TextView) mainView.findViewById(R.id.textView8);
        mainView.findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("asdsad");
            }
        });*/
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(mainView);
                stopSelf();
            }
        });

        try {
            //for moving the picture on touch and slide
            mainView.setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams paramsT = myParams;
                private int initialX;   //Откуда стартовал левый угол
                private int initialY;
                private float initialTouchX;    //Откуда стартовало движение от нажатия
                private float initialTouchY;
                private long touchStartTime = 0;
                private float lastX = 0;
                private float lastY = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Log.d(TAG, "onTouch");
                    //remove face bubble on long press
                    if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()) {
                        //windowManager.removeView(mainView);
                        //stopSelf();
                        return false;
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(TAG, "ACTION_DOWN - " + event.getRawX() + " : " + event.getRawY());
                            //touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "ACTION_UP - " + event.getRawX() + " : " + event.getRawY());
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //Log.d(TAG, "ACTION_MOVE - " + event.getRawX() + " : " + event.getRawY());

                            lastX = v.getX();

                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);        //>0
                            windowManager.updateViewLayout(mainView, myParams);

                            /*Point size = new Point();
                            windowManager.getDefaultDisplay().getSize(size);*/
                            float posLeft = myParams.x;
                            float posRight = posLeft + mainView.getLayoutParams().height;

                            float posTop = myParams.y;
                            float posBottom = posTop + mainView.getLayoutParams().width;

                            if (posTop < 0) {
                                Log.d("ERROR", "YYYYYYYYYY");
                            }
                            if (posLeft < 0) {
                                Log.d("ERROR", "XXXXXXXXXX");
                            }

                            /*if (lastX == v.getX()){
                                Log.d("ACTION_MOVE", "=========================");
                            }else{
                                Log.d("ACTION_MOVE", "!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }*/

                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        windowManager.addView(mainView, myParams);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
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
        Log.i(TAG, "onDestroy");
        if (cursorLoader != null) {
            cursorLoader.unregisterListener(loaderListener);
            cursorLoader.cancelLoad();
            cursorLoader.stopLoading();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
    }
}
