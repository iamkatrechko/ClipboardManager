package com.iamkatrechko.clipboardmanager.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Сервис виджета со списком
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("WidgetService", "WidgetService");
        return (new ListProvider(getApplicationContext(), intent));
    }
}
