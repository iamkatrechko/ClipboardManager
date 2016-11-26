package com.iamkatrechko.clipboardmanager;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
	/*
	 * So pretty simple just defining the Adapter of the listview
	 * here Adapter is ListProvider
	 * */

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WidgetService", "WidgetService");

        return (new ListProvider(this.getApplicationContext(), intent));
    }

}
