package com.iamkatrechko.clipboardmanager.services;

import android.app.Notification;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.Util;
import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.data.DatabaseDescription.*;

public class ClipboardService extends Service {
    private static final String TAG = "ClipboardService";

    private WindowManager windowManager;
    private ImageView floatingFaceBubble;
    private ClipboardManager clipBoard;

    public ClipboardService() {
    }

    @Override
    public void onCreate() {
        Log.i("ClipboardService", "Service: onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Запуск сервиса");

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notification = builder
                .setContentTitle("Clipboard Manager")
                .build();
        startForeground(98431, notification);

        //здесь вы можете запустить новый поток или задачу
        clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d("ClipboardService", "Новая запись");
                String clipText = Util.getClipboardText(getApplicationContext());
                String clipDescription = Util.getClipboardLabel(getApplicationContext());
                
                if (clipDescription.equals("891652")){
                    Toast.makeText(getApplicationContext(), "Отмена: копирование из приложения", Toast.LENGTH_SHORT).show();
                }else{
                    Uri uri = Clip.CONTENT_URI;
                    Cursor cursor = getContentResolver().query(uri,
                            new String[]{Clip._ID, Clip.COLUMN_CONTENT},
                            null,
                            null,
                            Clip._ID + " ASC");
                    if (clipText.length() == 0){
                        Toast.makeText(getApplicationContext(), "Пустая запись", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (recordAlreadyExists(clipText)){
                        Toast.makeText(getApplicationContext(), "Запись уже существует (в базе)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (cursor.moveToLast()){
                        if (cursor.getString(1).equals(clipText)){
                            Toast.makeText(getApplicationContext(), "Запись уже существует (последняя)", Toast.LENGTH_SHORT).show();
                        }else{
                            addNewClip(clipText);
                        }
                    }else{
                        addNewClip(clipText);
                    }
                }
            }
        });

       /*Intent hideIntent = new Intent(this, HideNotificationService.class);
        startService(hideIntent);*/

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
                /*listener.onAddEditCompleted(newClipUri);*/
            //Toast.makeText(getApplicationContext(), "Сохранено:\n" + content, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), FloatingCancelViewService.class);
            intent.putExtra("clipId", Long.valueOf(newClipUri.getLastPathSegment()));
            startService(intent);
        } else {
                /*Snackbar.make(coordinatorLayout,
                        R.string.contact_not_added, Snackbar.LENGTH_LONG).show();*/
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ClipboardService", "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("ClipboardService", "Service: onTaskRemoved");
    }
}
