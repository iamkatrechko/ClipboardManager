package com.iamkatrechko.clipboardmanager.widget

import android.content.Intent
import android.util.Log
import android.widget.RemoteViewsService

/**
 * Сервис виджета со списком
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        Log.d("WidgetService", "WidgetService")
        return ListProvider(this, intent)
    }
}
