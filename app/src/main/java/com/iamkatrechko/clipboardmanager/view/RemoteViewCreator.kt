package com.iamkatrechko.clipboardmanager.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.service.ClipboardService
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity

/**
 * Помощник по созданию уведомления с историей записи
 * @author iamkatrechko
 *         Date: 23.08.17
 */
object RemoteViewCreator {

    /** Создает кастомное уведомление со списком последних записей */
    fun createHistoryRemoteView(context: Context): RemoteViews {
        val onlyFavorite = UtilPreferences.isShowOnlyFavoriteInNotification(context)

        val generalRemoteViews = RemoteViews(context.packageName, R.layout.remote_view_notification)
        val currentClip = ClipUtils.getClipboardText(context)

        generalRemoteViews.setTextViewText(R.id.tvCurrent, "> " + currentClip)

        val intentAdd = Intent(context, ClipEditActivity::class.java)
        intentAdd.action = "ACTION_ADD"
        val pIntentAdd = PendingIntent.getActivity(context, 612452, intentAdd, 0)
        generalRemoteViews.setOnClickPendingIntent(R.id.button_add, pIntentAdd)

        val intentFavorite = Intent(context, ClipboardService::class.java)
        intentFavorite.action = ClipboardService.ACTION_SHOW_ONLY_FAVORITE
        val pIntentFavorite = PendingIntent.getService(context, 171251, intentFavorite, 0)
        generalRemoteViews.setOnClickPendingIntent(R.id.image_view_star, pIntentFavorite)

        if (onlyFavorite) {
            generalRemoteViews.setImageViewResource(R.id.image_view_star, R.drawable.ic_star)
            generalRemoteViews.setInt(R.id.image_view_star, "setColorFilter", Color.parseColor("#009688"))
        } else {
            generalRemoteViews.setImageViewResource(R.id.image_view_star, R.drawable.ic_star_border)
            generalRemoteViews.setInt(R.id.image_view_star, "setColorFilter", Color.parseColor("#808080"))
        }
        generalRemoteViews.removeAllViews(R.id.linear_clips)

        val lastClips = getLastClips(context, currentClip, onlyFavorite)
        lastClips.forEachIndexed { index, clip ->
            val clipRemoteViews = createClipListItem(context, clip.id, clip.text)
            if (index == 0) {
                clipRemoteViews.setViewVisibility(R.id.flSeparator, View.GONE)
            }
            generalRemoteViews.addView(R.id.linear_clips, clipRemoteViews)
        }
        return generalRemoteViews
    }

    /** Возвращает список последних 4-х записей */
    private fun getLastClips(context: Context, currentClipText: String, onlyFavorite: Boolean): List<Clip> {
        val queryWithoutCurrent = DatabaseDescription.ClipsTable.COLUMN_CONTENT + " <> '$currentClipText'"
        val queries = mutableListOf(queryWithoutCurrent)
        if (onlyFavorite) {
            val queryOnlyFavorite = DatabaseDescription.ClipsTable.COLUMN_IS_FAVORITE + " = 1"
            queries.add(queryOnlyFavorite)
        }

        val lastRecords = ClipCursor(context.contentResolver.query(DatabaseDescription.ClipsTable.CONTENT_URI,
                null,
                queries.joinToString(" AND "),
                null,
                OrderType.BY_DATE_ASC.query + " LIMIT 4")
        )

        return CursorToClipMapper().toClips(ClipCursor(lastRecords))
    }

    /** Создает элемент списка последних записей */
    private fun createClipListItem(context: Context, id: Long, clipText: String): RemoteViews {
        val clipRemoteViews = RemoteViews(context.packageName, R.layout.remote_view_notification_list_item)
        clipRemoteViews.setTextViewText(R.id.tvTitle, clipText)

        val intentCopy = Intent(context, ClipboardService::class.java)
        intentCopy.action = ClipboardService.ACTION_COPY_TO_CLIPBOARD
        intentCopy.putExtra("id", id)
        val pIntentCopy = PendingIntent.getService(context, id.toInt(), intentCopy, 0)
        clipRemoteViews.setOnClickPendingIntent(R.id.ivCopy, pIntentCopy)

        return clipRemoteViews
    }
}
