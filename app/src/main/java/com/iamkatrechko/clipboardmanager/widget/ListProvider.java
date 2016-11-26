package com.iamkatrechko.clipboardmanager.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.iamkatrechko.clipboardmanager.R;

import java.util.ArrayList;

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<String> listItemList = new ArrayList<>();
    private Context context;
    //private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        Log.d("ListProvider", "ListProvider");
        this.context = context;
        /*appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);*/

        populateListItem();
    }

    private void populateListItem() {
        for (int i = 0; i < 5; i++) {
            listItemList.add("Запись №" + i);
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
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_view_item);

        remoteView.setTextViewText(R.id.textView2, listItemList.get(position));

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
