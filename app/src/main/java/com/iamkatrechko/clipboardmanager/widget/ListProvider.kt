package com.iamkatrechko.clipboardmanager.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import java.util.*

/**
 * Поставщик данных для виджета списка
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ListProvider(
        context: Context,
        intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    /** Список заметок  */
    private val clips = ArrayList<String>()
    /** Имя пакета  */
    private val packageName: String

    init {
        Log.d(TAG, "ListProvider()")
        packageName = context.packageName
        /*val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);*/
        populateListItem()
    }

    /** Генерирует список записей  */
    private fun populateListItem() {
        repeat(15) {
            clips.add("Запись №$it")
        }
    }

    override fun onCreate() {}

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount() = clips.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds() = true

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(packageName, R.layout.list_view_item)
        remoteView.setTextViewText(R.id.text_view_title, clips[position])
        return remoteView
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }
}
