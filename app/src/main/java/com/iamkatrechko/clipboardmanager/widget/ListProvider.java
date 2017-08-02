package com.iamkatrechko.clipboardmanager.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.iamkatrechko.clipboardmanager.R;

import java.util.ArrayList;

/**
 * Поставщик данных для виджета списка
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    /** Список заметок */
    private ArrayList<String> clips = new ArrayList<>();
    /** Имя пакета */
    private String packageName;
    //private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        Log.d("ListProvider", "ListProvider");
        packageName = context.getPackageName();
        /*appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);*/

        populateListItem();
    }

    /** Генерирует список записей */
    private void populateListItem() {
        for (int i = 0; i < 5; i++) {
            clips.add("Запись №" + i);
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return clips.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(packageName, R.layout.list_view_item);
        remoteView.setTextViewText(R.id.text_view_title, clips.get(position));
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
